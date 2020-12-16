package io.agora.rtcn.webrtc.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RTCRtcpMuxPolicy {
    @JsonProperty("negotiate")
    negotiate("negotiate"),
    @JsonProperty("require")
    require("require");

    private String name;

    RTCRtcpMuxPolicy(String val) {
        this.name = val;
    }

    public String toString() {
        return this.name;
    }
}
