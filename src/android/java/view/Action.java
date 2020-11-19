package com.agora.cordova.plugin.view;

public enum Action {
    createInstance("createInstance"),
    updateConfig("updateConfig"),
    play("play"),
    pause("pause"),
    destroy("destroy"),
    getCurrentFrame("getCurrentFrame"),
    aaa("aaa"),
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
