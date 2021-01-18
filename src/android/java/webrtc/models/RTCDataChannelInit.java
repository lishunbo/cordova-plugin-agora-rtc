package io.agora.rtcn.webrtc.models;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RTCDataChannelInit {
    static final String TAG = RTCDataChannelInit.class.getCanonicalName();

    public long id;
    public long maxPacketLifeTime;
    public long maxRetransmits;
    public boolean negotiated;
    public boolean ordered;
    public String priority;
    public String protocol;

    public static RTCDataChannelInit fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, RTCDataChannelInit.class);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "RTCDataChannelInit parseJson failed: " + e.toString());
        }
        return null;
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
