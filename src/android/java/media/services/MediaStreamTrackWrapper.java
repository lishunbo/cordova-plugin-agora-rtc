package io.agora.rtcn.media.services;

import android.util.Log;

import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.MediaStreamTrack;
import org.webrtc.RtpReceiver;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
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
    String pcid;
    Object object;
    MediaStreamTrack track;
    ArrayList<Object> relatedObject;
    VideoView vv;

    MediaStreamTrackWrapper(String pcid, Object track, Object... relatedObject) {
        this.object = track;
        if (track instanceof MediaStreamTrack) {
            this.track = (MediaStreamTrack) track;
        } else if (track instanceof RtpReceiver) {
            this.track = ((RtpReceiver) track).track();
        }
        this.pcid = pcid;
        this.id = UUID.randomUUID().toString();
        this.relatedObject = new ArrayList<>();
        this.relatedObject.addAll(Arrays.asList(relatedObject));
    }

    public MediaStreamTrack getTrack() {
        return track;
    }

    public boolean isLocal() {
        return pcid.length() == 0;
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
            Log.e(TAG, "MediaStreamTrackWrapper toString exception: " + e.toString());
        }
        return obj.toString();
    }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("kind", this.track.kind());
            obj.put("id", this.id);
        } catch (Exception e) {
            Log.e(TAG, "MediaStreamTrackWrapper toString exception: " + e.toString());
        }
        return obj;
    }

    public void close() {
        if (object == null) {
            return;
        }
        if (object instanceof MediaStreamTrack) {
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
        Log.d(TAG, "allTrack sizes: " + allTracks.size());

        tracksLock.writeLock().unlock();
        return wrapper;
    }

    // static method
    public static void removeMediaStreamTrackByPCId(String pcid) {
        tracksLock.writeLock().lock();

        List<String> toRemove = new LinkedList<>();
        for (Map.Entry<String, MediaStreamTrackWrapper> ct :
                allTracks.entrySet()) {
            if (ct.getValue().pcid.equals(pcid)) {
                toRemove.add(ct.getKey());
            }
        }

        for (String id : toRemove) {
            allTracks.remove(id);
        }

        Log.d(TAG, "allTrack sizes: " + allTracks.size());

        tracksLock.writeLock().unlock();
    }

    // static method
    public static MediaStreamTrackWrapper getMediaStreamTrackById(String id) {
        tracksLock.readLock().lock();

        MediaStreamTrackWrapper wrapper = allTracks.get(id);

        tracksLock.readLock().unlock();
        return wrapper;
    }

    public static MediaStreamTrackWrapper cacheMediaStreamTrackWrapper(String pcid, Object track, Object... relatedObject) {
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
