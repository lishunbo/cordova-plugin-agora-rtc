package io.agora.rtcn.webrtc.models;

import android.util.Log;

import io.agora.rtcn.webrtc.enums.RTCBundlePolicy;
import io.agora.rtcn.webrtc.enums.RTCIceTransportPolicy;
import io.agora.rtcn.webrtc.enums.RTCRtcpMuxPolicy;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RTCConfiguration {
    static final String TAG = RTCConfiguration.class.getCanonicalName();

    public RTCBundlePolicy bundlePolicy;
    public RTCCertificate[] certificates;
    public int iceCandidatePoolSize;
    public RTCIceServer[] iceServers;
    public RTCIceTransportPolicy iceTransportPolicy;
    public String peerIdentity;
    public RTCRtcpMuxPolicy rtcpMuxPolicy;

    public static RTCConfiguration fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, RTCConfiguration.class);
        } catch (JsonProcessingException e) {
//            System.out.println(e.toString());
            Log.e(TAG, "++++++++++++++"+ e.toString());
        }
        return null;
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
