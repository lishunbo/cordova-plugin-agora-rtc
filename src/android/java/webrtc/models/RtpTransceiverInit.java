package io.agora.rtcn.webrtc.models;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.webrtc.RtpParameters;
import org.webrtc.RtpTransceiver;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RtpTransceiverInit {
    static final String TAG = RtpTransceiverInit.class.getCanonicalName();

    //public RTCRtpTransceiverDirection direction;
    public String direction;
    public RTCRtpEncodingParameters[] sendEncodings;
//    public long streams;

    public static RtpTransceiverInit fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, RtpTransceiverInit.class);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "RtpTransceiverInit parseJson failed: " + e.toString());
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

    public RtpTransceiver.RtpTransceiverInit toNative() {
        RtpTransceiver.RtpTransceiverDirection direction =
                RtpTransceiver.RtpTransceiverDirection.SEND_RECV;
        if (this.direction != null) {
            switch (this.direction) {
                case "sendonly":
                    direction = RtpTransceiver.RtpTransceiverDirection.SEND_ONLY;
                    break;
                case "recvonly":
                    direction = RtpTransceiver.RtpTransceiverDirection.RECV_ONLY;
                    break;
                case "inactive":
                    direction = RtpTransceiver.RtpTransceiverDirection.INACTIVE;
                    break;
            }
        }
        List<RtpParameters.Encoding> encodings = new LinkedList<>();
        if (sendEncodings == null) {
            sendEncodings = new RTCRtpEncodingParameters[]{};
        }
        for (RTCRtpEncodingParameters parameter : sendEncodings) {
            encodings.add(parameter.toNative());
        }
        return new RtpTransceiver.RtpTransceiverInit(direction,
                Collections.emptyList(), encodings);
    }
}
