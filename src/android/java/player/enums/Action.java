package io.agora.rtc.player.enums;

public enum Action {
    createVideoPlayer("createVideoPlayer"),
    updateConfig("updateConfig"),
    playVideoPlayer("playVideoPlayer"),
    pauseVideoPlayer("pauseVideoPlayer"),
    destroyVideoPlayer("destroyVideoPlayer"),
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
    getVolume("getVolume"),
    setVolume("setVolume"),
    setSinkId("setSinkId"),

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
