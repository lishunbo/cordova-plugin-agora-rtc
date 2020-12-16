package io.agora.rtcn.webrtc.interfaces;

import io.agora.rtcn.webrtc.models.RTCIceServer;
import io.agora.rtcn.webrtc.models.RTCOAuthCredential;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonSubTypes({
        @JsonSubTypes.Type(value = RTCIceServer.CredentialDetailStringImp.class, name = "password"),
        @JsonSubTypes.Type(value = RTCIceServer.CredentialDetailStringImp.class, name = ""),
        @JsonSubTypes.Type(value = RTCOAuthCredential.class, name = "oauth")
})
public abstract class CredentialDetail {
    public CredentialDetail() {
    }

    public String toString() {
        return "{}";
    }
}