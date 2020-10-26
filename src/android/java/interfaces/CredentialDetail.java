package com.agora.cordova.plugin.webrtc.interfaces;

import com.agora.cordova.plugin.webrtc.model.RTCOAuthCredential;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonSubTypes({
        @JsonSubTypes.Type(value = String.class, name = "password"),
        @JsonSubTypes.Type(value = RTCOAuthCredential.class, name = "oauth")
})
public abstract class CredentialDetail {
    public CredentialDetail() {
    }

    public String toString() {
        return "{}";
    }
}