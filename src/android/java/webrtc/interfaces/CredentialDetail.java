package io.agora.rtc.webrtc.interfaces;

import io.agora.rtc.webrtc.models.RTCIceServer;
import io.agora.rtc.webrtc.models.RTCOAuthCredential;

import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonSubTypes({
        @JsonSubTypes.Type(value = RTCIceServer.CredentialDetailStringImp.class, name = "password"),
        @JsonSubTypes.Type(value = RTCIceServer.CredentialDetailStringImp.class, name = ""),
        @JsonSubTypes.Type(value = RTCOAuthCredential.class, name = "oauth")
})
public interface CredentialDetail {
    String toString();
}