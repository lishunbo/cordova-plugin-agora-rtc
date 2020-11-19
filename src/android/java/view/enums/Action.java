package com.agora.cordova.plugin.view.enums;

public enum Action {
    createInstance("createInstance"),
    updateConfig("updateConfig"),
    play("play"),
    pause("pause"),
    destroy("destroy"),
    getCurrentFrame("getCurrentFrame"),
    updateVideoTrack("updateVideoTrack"),
    getWindowAttribute("getWindowAttribute"),
    setViewAttribute("setViewAttribute"),

    onFirstFrameDecoded("onFirstFrameDecoded"),

    MaxAction("maxAction");

    private String name;

    Action(String val) {
        this.name = val;
    }

    public String toString() {
        return this.name;
    }
}
