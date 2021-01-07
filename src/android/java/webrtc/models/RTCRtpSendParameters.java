package io.agora.rtcn.webrtc.models;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RTCRtpSendParameters extends RTCRtpParameters {
    public String degradationPreference;
    public List<RTCRtpEncodingParameters> encodings;
    public String priority;
    public String transactionId;

    public static RTCRtpSendParameters fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, RTCRtpSendParameters.class);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "RTCRtpSendParameters parseJson failed: " + e.toString());
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
