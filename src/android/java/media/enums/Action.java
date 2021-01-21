package io.agora.rtc.media.enums;

public enum Action {
    eventChannel("eventChannel"),

    enumerateDevices("enumerateDevices"),
    getUserMedia("getUserMedia"),
    stopMediaStreamTrack("stopMediaStreamTrack"),
    getSubVideoTrack("getSubVideoTrack"),

    MaxAction("maxAction");

    private String name;

    Action(String val) {
        this.name = val;
    }

    public String toString() {
        return this.name;
    }
}
