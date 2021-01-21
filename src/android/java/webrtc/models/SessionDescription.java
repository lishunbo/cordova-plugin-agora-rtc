package io.agora.rtc.webrtc.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionDescription {
    static final String TAG = SessionDescription.class.getCanonicalName();

    public String type;
    public String sdp;

    public static SessionDescription fromJson(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, SessionDescription.class);
    }

    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "{}";
    }
}
