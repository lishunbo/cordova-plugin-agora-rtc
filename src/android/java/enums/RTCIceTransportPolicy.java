package com.agora.cordova.plugin.webrtc.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RTCIceTransportPolicy {
    @JsonProperty("all")
    all("all"),
    @JsonProperty("relay")
    relay("relay");

    private String name;

    RTCIceTransportPolicy(String val) {
        this.name = val;
    }

    public String toString() {
        return this.name;
    }
}
