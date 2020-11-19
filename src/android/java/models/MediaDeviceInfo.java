package com.agora.cordova.plugin.webrtc.models;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MediaDeviceInfo {
    static final String TAG = MediaDeviceInfo.class.getCanonicalName();

    public String deviceId;
    public String groupId;
    public String kind;
    public String label;

    public MediaDeviceInfo(String deviceId, String groupId, String kind, String label){
        this.deviceId = deviceId;
        this.groupId = groupId;
        this.kind = kind;
        this.label = label;
    }

    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            Log.e(TAG, e.toString());
        }

        return "{}";
    }
}
