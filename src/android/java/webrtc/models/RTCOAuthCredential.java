package io.agora.rtcn.webrtc.models;

import android.util.Log;

import io.agora.rtcn.webrtc.interfaces.CredentialDetail;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RTCOAuthCredential implements CredentialDetail {
    static final String TAG = RTCOAuthCredential.class.getCanonicalName();

    public String accessToken;
    public String macKey;

    public RTCOAuthCredential() {
    }

    public static RTCOAuthCredential fromJson(String json) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            RTCOAuthCredential obj = mapper.readValue(json, RTCOAuthCredential.class);
            return obj;
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
