package com.agora.cordova.plugin.webrtc.models;

import android.util.Log;

import com.agora.cordova.plugin.webrtc.services.RTCPeerConnection;

import org.json.JSONObject;
import org.webrtc.MediaStreamTrack;

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

public class MediaStreamTrackWrapper {
    static final String TAG = RTCPeerConnection.class.getCanonicalName();

    static ReadWriteLock tracksLock = new ReentrantReadWriteLock();
    static Map<String, MediaStreamTrackWrapper> allTracks = new HashMap<>();

    String id;
    String pcid;
    MediaStreamTrack track;
    ArrayList<Object> relatedObject;

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

    public MediaStreamTrack getTrack() {
        return track;
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

//    public static MediaStreamTrackWrapper cacheMediaStreamTrack(String pcid, MediaStreamTrack track) {
//        MediaStreamTrackWrapper wrapper = getMediaStreamTrackByTrack(track);
//        if (wrapper == null) {
//            wrapper = cacheMediaStreamTrackWrapper(pcid, track);
//        }
//        return wrapper;
//    }

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

}
