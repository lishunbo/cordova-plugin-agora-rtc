package com.agora.cordova.plugin.webrtc.models;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class MediaStreamConstraints {
    static final String TAG = RTCConfiguration.class.getCanonicalName();

    Map<String, String> parameters;

    public MediaStreamConstraints(HashMap<String, String> pairs) {
        parameters = pairs;
    }

    public String toString(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            Log.e(TAG, e.toString());
        }

        return "{}";
    }
}
