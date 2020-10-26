package com.agora.cordova.plugin.webrtc.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RTCBundlePolicy {
    @JsonProperty("balanced")
    balanced("balanced"),
    @JsonProperty("max-bundle")
    max_bundle("max-bundle"),
    @JsonProperty("max-compat")
    max_compat("max-compat");

    private String name;

    RTCBundlePolicy(String val) {
        this.name = val;
    }

    public String toString() {
        return this.name;
    }
}
