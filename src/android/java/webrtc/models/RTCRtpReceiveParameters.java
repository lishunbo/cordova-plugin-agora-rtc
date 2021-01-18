package io.agora.rtcn.webrtc.models;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RTCRtpReceiveParameters extends RTCRtpParameters {

    public List< RTCRtpDecodingParameters> encodings;

    public static RTCRtpReceiveParameters fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, RTCRtpReceiveParameters.class);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "RTCRtpReceiveParameters parseJson failed: " + e.toString());
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

    public class RTCRtpDecodingParameters{
        public String rid;
    }
}
