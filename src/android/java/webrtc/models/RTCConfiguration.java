package io.agora.rtcn.webrtc.models;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RTCConfiguration {
    static final String TAG = RTCConfiguration.class.getCanonicalName();

    public String bundlePolicy;
    public RTCCertificate[] certificates;
    public int iceCandidatePoolSize;
    public RTCIceServer[] iceServers;
    public String iceTransportPolicy;
    public String peerIdentity;
    public String rtcpMuxPolicy;
//    public String sdpSemantics;

    public static RTCConfiguration fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, RTCConfiguration.class);
        } catch (JsonProcessingException e) {
//            System.out.println(e.toString());
            Log.e(TAG, e.toString());
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
