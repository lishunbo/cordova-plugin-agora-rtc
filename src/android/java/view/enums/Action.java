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


    createAudioPlayer("createAudioPlayer"),
    playAudioPlayer("playAudioPlayer"),
    pauseAudioPlayer("pauseAudioPlayer"),
    destroyAudioPlayer("destroyAudioPlayer"),
    updateTrackAudioPlayer("updateTrackAudioPlayer"),
    getVolumeRange("getVolumeRange"),
    AudioPlayer_getVolume("AudioPlayer_getVolume"),
    AudioPlayer_setVolume("AudioPlayer_setVolume"),
    AudioPlayer_setSinkId("AudioPlayer_setSinkId"),

    onVolumeChange("onVolumeChange"),
    onAudioLevel("onAudioLevel"),


    MaxAction("maxAction");

    private String name;

    Action(String val) {
        this.name = val;
    }

    public String toString() {
        return this.name;
    }
}
