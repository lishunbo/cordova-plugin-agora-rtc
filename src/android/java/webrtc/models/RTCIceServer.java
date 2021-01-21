package io.agora.rtc.webrtc.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import io.agora.rtc.webrtc.interfaces.CredentialDetail;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RTCIceServer {
//    static final String TAG = RTCIceServer.class.getCanonicalName();

    //    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "credentialType", defaultImpl = String.class)
    @JsonDeserialize(using = CredentialDetailDeserializer.class)
    public CredentialDetail credential;

    public String credentialType;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public String[] urls;
    public String username;

    @Override
    public String toString() {
        return toJSONObject().toString();
    }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            JSONArray ua = new JSONArray();
            for (String u : urls) {
                ua.put(u);
            }
            obj.put("urls", ua);
            if (username.length() > 0) {
                obj.put("username", username);
            }
            if (credential != null) {
                obj.put("credentialType", credentialType);
                obj.put("credential", credential);
            }
        } catch (Exception e) {

        }
        return obj;
    }

    public static class CredentialDetailStringImp implements CredentialDetail {
        public String credential;

        public CredentialDetailStringImp() {
        }

        public CredentialDetailStringImp setCredential(String credential) {
            this.credential = credential;
            return this;
        }

        public String toString() {
            return credential;
        }
    }

    public static class CredentialDetailDeserializer extends JsonDeserializer<CredentialDetail> {

        @Override
        public CredentialDetail deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken jsonToken = p.getCurrentToken();
            if (jsonToken == JsonToken.VALUE_STRING) {
                return new CredentialDetailStringImp().setCredential(p.getValueAsString());
            } else if (jsonToken == JsonToken.START_OBJECT) {
                return p.readValueAs(RTCOAuthCredential.class);
//                RTCOAuthCredential oa = p.readValueAs(RTCOAuthCredential.class);
//                Log.e(TAG, "oa:"+oa.toString());
//                return oa;
            }  //                Log.e(TAG, "current val 6 :" + jsonToken.toString());

            return null;
        }
    }
}
