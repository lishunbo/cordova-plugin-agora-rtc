package com.agora.cordova.plugin.view.model;

import android.util.Log;

import com.agora.cordova.plugin.view.enums.PlayConfigFit;
import com.agora.cordova.plugin.webrtc.models.RTCConfiguration;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayConfig {
    static final String TAG = RTCConfiguration.class.getCanonicalName();

    public boolean mirror;
    public PlayConfigFit fit;
    public String trackId;

    public static PlayConfig fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, PlayConfig.class);
        } catch (JsonProcessingException e) {
//            System.out.println(e.toString());
            Log.e(TAG, "fromJson exception " + e.toString());
        }
        return null;
    }

    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "toString exception " + e.toString());
        }

        return "{}";
    }
}
