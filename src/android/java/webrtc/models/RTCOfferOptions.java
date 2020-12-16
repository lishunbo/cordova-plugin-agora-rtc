package io.agora.rtcn.webrtc.models;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RTCOfferOptions {
    public boolean iceRestart;
    public boolean offerToReceiveAudio;
    public boolean offerToReceiveVideo;
    public boolean voiceActivityDetection;

    public RTCOfferOptions() {
        this.iceRestart = false;
        this.offerToReceiveAudio = false;
        this.offerToReceiveVideo = false;
        this.voiceActivityDetection = true;
    }

    public static RTCOfferOptions fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, RTCOfferOptions.class);
        } catch (JsonProcessingException e) {
            Log.e("RTCOfferOptionsFromJson", e.toString());
        }
        return null;
    }
}
