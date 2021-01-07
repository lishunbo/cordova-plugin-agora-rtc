package io.agora.rtcn.webrtc.models;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.webrtc.RtpParameters;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RTCRtpEncodingParameters {
    static final String TAG = RTCRtpEncodingParameters.class.getCanonicalName();

    public boolean active;
//    public long codecPayloadType;
//    public RTCDtxStatus dtx;
    public long maxBitrate;
    public long maxFramerate;
//    public long ptime;
    public long scaleResolutionDownBy;
    public String rid;

    public static RTCRtpEncodingParameters fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, RTCRtpEncodingParameters.class);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "RTCRtpEncodingParameters parseJson failed: " + e.toString());
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

    public RtpParameters.Encoding toNative() {
        RtpParameters.Encoding encoding = new RtpParameters.Encoding(
                rid == null ? "" : rid, active, (double) scaleResolutionDownBy);
        encoding.maxBitrateBps=(int)maxBitrate;
        encoding.maxFramerate=(int)maxFramerate;
        return encoding;
    }

    public enum RTCDtxStatus {
        @JsonProperty("disabled")
        disabled("disabled"),
        @JsonProperty("enabled")
        enabled("enabled");

        private String name;

        RTCDtxStatus(String val) {
            this.name = val;
        }

        public String toString() {
            return this.name;
        }
    }

}
