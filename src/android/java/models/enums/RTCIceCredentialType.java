package com.agora.cordova.plugin.webrtc.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RTCIceCredentialType {
    @JsonProperty("oauth")
    oauth("oauth"),
    @JsonProperty("password")
    password("password");

    private String name;

    RTCIceCredentialType(String val) {
        this.name = val;
    }

    public String toString() {
        return this.name;
    }
}
