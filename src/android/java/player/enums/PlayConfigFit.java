package io.agora.rtcn.player.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PlayConfigFit {
//     "cover" | "contain" | "fill"
    @JsonProperty("cover")
    cover("cover"),
    @JsonProperty("fill")
    fill("fill"),
    @JsonProperty("contain")
    contain("contain");

    private String name;

    PlayConfigFit(String val) {
        this.name = val;
    }

    public String toString() {
        return this.name;
    }
}
