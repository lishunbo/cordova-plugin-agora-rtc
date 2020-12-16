package io.agora.rtcn.media.services;

import android.util.Log;

import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.MediaStreamTrack;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.agora.rtcn.player.services.VideoView;

public class MediaStreamTrackWrapper {
    static final String TAG = MediaStreamTrackWrapper.class.getCanonicalName();

    static ReadWriteLock tracksLock = new ReentrantReadWriteLock();
    static Map<String, MediaStreamTrackWrapper> allTracks = new HashMap<>();

    String id;
    boolean isLocal;
    Object object;
    MediaStreamTrack track;
    ArrayList<Object> relatedObject;
    VideoView vv;

    MediaStreamTrackWrapper(boolean isLocal, Object track, Object... relatedObject) {
        this.object = track;
        if (track instanceof MediaStreamTrack) {
            this.track = (MediaStreamTrack) track;
        }
        this.isLocal = isLocal;
        this.id = UUID.randomUUID().toString();
        this.relatedObject = new ArrayList<>();
        this.relatedObject.addAll(Arrays.asList(relatedObject));
    }

    public MediaStreamTrack getTrack() {
        return track;
    }

    public boolean isLocal() {
        return isLocal;
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
        if (object == null) {
            return;
        }
        if (track != null) {
            if (track.kind().equals("audio") && relatedObject.size() != 0 &&
                    relatedObject.get(0) instanceof AudioSource) {
                ((AudioSource) relatedObject.get(0)).dispose();
            } else if (track.kind().equals("video")) {
                if (vv != null) {
                    ((VideoTrack) track).removeSink(vv.getSink());
                    vv.close();
                    vv = null;
                }
                try {
                    if (relatedObject.get(0) instanceof VideoCapturer) {
                        track.dispose();
                        try {
                            ((VideoCapturer) relatedObject.get(0)).stopCapture();
                        } catch (Exception e) {
                            Log.e(TAG, "VideoViewService.destroy.VideoCapturer.stopCapture exception: " + e.toString());
                        }
                    }
                    if (getRelatedObject().get(1) instanceof SurfaceTextureHelper) {
                        try {
                            ((SurfaceTextureHelper) getRelatedObject().get(1)).stopListening();
                            ((SurfaceTextureHelper) getRelatedObject().get(1)).dispose();
                        } catch (Exception e) {
                            Log.e(TAG, "VideoViewService.destroy.SurfaceTextureHelper.dispose exception: " + e.toString());
                        }
                    }
                    if (getRelatedObject().get(2) instanceof VideoSource) {
                        try {
                            ((VideoSource) getRelatedObject().get(2)).dispose();
                        } catch (Exception e) {
                            Log.e(TAG, "VideoViewService.destroy.VideoSource.dispose exception: " + e.toString());
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    //ignore
                }
            }
        }

        relatedObject.clear();
        id = null;
        object = null;
        track = null;
        relatedObject = null;
    }

    // static method
    public static MediaStreamTrackWrapper popMediaStreamTrackById(String id) {
        tracksLock.writeLock().lock();

        MediaStreamTrackWrapper wrapper = allTracks.get(id);
        allTracks.remove(id);

        tracksLock.writeLock().unlock();
        return wrapper;
    }

    // static method
    public static MediaStreamTrackWrapper popMediaStreamTrackByObject(Object object) {
        tracksLock.writeLock().lock();

        MediaStreamTrackWrapper wrapper = null;
        for (Map.Entry<String, MediaStreamTrackWrapper> ct :
                allTracks.entrySet()) {
            if (ct.getValue().object == object) {
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
    public static MediaStreamTrackWrapper getMediaStreamTrackById(String id) {
        tracksLock.readLock().lock();

        MediaStreamTrackWrapper wrapper = allTracks.get(id);

        tracksLock.readLock().unlock();
        return wrapper;
    }

    public static MediaStreamTrackWrapper cacheMediaStreamTrackWrapper(boolean isLocal, Object track, Object... relatedObject) {
        MediaStreamTrackWrapper wrapper = new MediaStreamTrackWrapper(isLocal, track, relatedObject);
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
