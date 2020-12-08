package com.agora.cordova.plugin.webrtc.models;

import android.util.Log;

import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.MediaStreamTrack;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSink;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.agora.cordova.plugin.view.VideoView;

public class MediaStreamTrackWrapper {
    static final String TAG = MediaStreamTrackWrapper.class.getCanonicalName();

    static ReadWriteLock tracksLock = new ReentrantReadWriteLock();
    static Map<String, MediaStreamTrackWrapper> allTracks = new HashMap<>();

    String id;
    String pcid;
    MediaStreamTrack track;
    ArrayList<Object> relatedObject;
    VideoView vv;

    MediaStreamTrackWrapper(String pcid, MediaStreamTrack track, Object... relatedObject) {
        this.track = track;
        this.pcid = pcid;
        this.id = UUID.randomUUID().toString();
        this.relatedObject = new ArrayList<>();
        this.relatedObject.addAll(Arrays.asList(relatedObject));
    }

    public String getId() {
        return id;
    }

    public boolean isLocal() {
        return this.pcid.length() == 0;
    }

    public MediaStreamTrack getTrack() {
        return track;
    }

    public void addVideoView(VideoView vv) {
        this.vv = vv;
    }

    public List<Object> getRelatedObject() {
        return relatedObject;
    }

    public String toString() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("kind", this.track.kind());
            obj.put("id", this.id);
        } catch (Exception e) {
//            Log.e(TAG, "MediaStreamTrackWrapper toString exception: " + e.toString());
        }
        return obj.toString();
    }

    public void close() {
        if (getTrack().kind().equals("audio") && getRelatedObject().size() != 0 &&
                getRelatedObject().get(0) != null && getRelatedObject().get(0) instanceof AudioSource) {
            ((AudioSource) getRelatedObject().get(0)).dispose();
        }

        if (getTrack().kind().equals("video")) {
            if (vv != null) {
                VideoTrack videoTrack = (VideoTrack) getTrack();
                videoTrack.removeSink(vv.getSink());
                vv.close();
                vv = null;
            }
            if (getRelatedObject().size() != 0) {
                if (getRelatedObject().get(0) != null && getRelatedObject().get(0) instanceof VideoCapturer) {
                    try {
                        ((VideoCapturer) getRelatedObject().get(0)).stopCapture();
                    } catch (Exception e) {
                        Log.e(TAG, "VideoViewService.destroy.VideoCapturer.stopCapture exception: " + e.toString());
                    }
                }
                if (getRelatedObject().get(1) != null && getRelatedObject().get(1) instanceof SurfaceTextureHelper) {
                    try {
                        ((SurfaceTextureHelper) getRelatedObject().get(1)).stopListening();
                        ((SurfaceTextureHelper) getRelatedObject().get(1)).dispose();
                    } catch (Exception e) {
                        Log.e(TAG, "VideoViewService.destroy.SurfaceTextureHelper.dispose exception: " + e.toString());
                    }
                }
                if (getRelatedObject().get(2) != null && getRelatedObject().get(2) instanceof VideoSource) {
                    try {
                        ((VideoSource) getRelatedObject().get(2)).dispose();
                    } catch (Exception e) {
                        Log.e(TAG, "VideoViewService.destroy.VideoSource.dispose exception: " + e.toString());
                    }
                }
            }
        }

        getRelatedObject().clear();
        getTrack().dispose();
        id = null;
        pcid = null;
        track = null;
        relatedObject = null;
        vv = null;
    }

    // static method
    public static MediaStreamTrackWrapper popMediaStreamTrackById(String id) {
        tracksLock.writeLock().lock();

        MediaStreamTrackWrapper wrapper = allTracks.get(id);
        allTracks.remove(id);
        Log.d(TAG, "allTrack sizes: " + allTracks.size());

        tracksLock.writeLock().unlock();
        return wrapper;
    }

    // static method
    public static MediaStreamTrackWrapper popMediaStreamTrackByTrack(MediaStreamTrack track) {
        tracksLock.writeLock().lock();

        MediaStreamTrackWrapper wrapper = null;
        for (Map.Entry<String, MediaStreamTrackWrapper> ct :
                allTracks.entrySet()) {
            if (ct.getValue().track == track) {
                wrapper = ct.getValue();
                break;
            }
        }
        if (wrapper != null) {
            allTracks.remove(wrapper.id);
        }
        Log.d(TAG, "allTrack sizes: " + allTracks.size());

        tracksLock.writeLock().unlock();
        return wrapper;
    }

    // static method
    public static void removeMediaStreamTrackByPCId(String id) {
        tracksLock.writeLock().lock();

        Set<String> ids = new HashSet<>();
        for (Map.Entry<String, MediaStreamTrackWrapper> ct :
                allTracks.entrySet()) {
            if (ct.getValue().pcid == id) {
                ids.add(ct.getKey());
            }
        }
        allTracks.keySet().removeAll(ids);
        Log.d(TAG, "allTrack sizes: " + allTracks.size());

        tracksLock.writeLock().unlock();
        return;
    }

    // static method
    public static MediaStreamTrackWrapper getMediaStreamTrackById(String id) {
        tracksLock.readLock().lock();

        MediaStreamTrackWrapper wrapper = allTracks.get(id);

        tracksLock.readLock().unlock();
        return wrapper;
    }

    public static MediaStreamTrackWrapper getMediaStreamTrackByTrack(MediaStreamTrack track) {
        MediaStreamTrackWrapper wrapper = null;

        tracksLock.readLock().lock();

        for (Map.Entry<String, MediaStreamTrackWrapper> ct :
                allTracks.entrySet()) {
            if (ct.getValue().track == track) {
                wrapper = ct.getValue();
                break;
            }
        }
        tracksLock.readLock().unlock();
        return wrapper;
    }

    public static MediaStreamTrackWrapper cacheMediaStreamTrack(String pcid, MediaStreamTrack track, Object... relatedObject) {
        MediaStreamTrackWrapper wrapper = getMediaStreamTrackByTrack(track);
        if (wrapper == null) {
            wrapper = cacheMediaStreamTrackWrapper(pcid, track, relatedObject);
        }
        return wrapper;
    }

    static MediaStreamTrackWrapper cacheMediaStreamTrackWrapper(String pcid, MediaStreamTrack track, Object... relatedObject) {
        MediaStreamTrackWrapper wrapper = new MediaStreamTrackWrapper(pcid, track, relatedObject);
        tracksLock.writeLock().lock();

        allTracks.put(wrapper.id, wrapper);

        Log.d(TAG, "allTrack sizes: " + allTracks.size());

        tracksLock.writeLock().unlock();
        return wrapper;
    }

    public static void reset() {
        tracksLock.writeLock().lock();

        for (Map.Entry<String, MediaStreamTrackWrapper> ct :
                allTracks.entrySet()) {
            ct.getValue().close();
        }

        allTracks.clear();

        tracksLock.writeLock().unlock();
    }
}
