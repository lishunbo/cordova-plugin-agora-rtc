package com.agora.cordova.plugin.webrtc.interfaces;

import com.agora.cordova.plugin.webrtc.models.RTCIceServer;
import com.agora.cordova.plugin.webrtc.models.RTCOAuthCredential;
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