package com.agora.cordova.plugin.webrtc;

public enum Action {
    createInstance("CreateInstance"),
    createOffer("createOffer"),
    setRemoteDescription("setRemoteDescription"),
    addIceCandidate("addIceCandidate"),
    close("close"),
    removeTrack("removeTrack"),
    getTransceivers("getTransceivers"),
    addTransceiver("addTransceiver"),
    addTrack("addTrack"),
    getStats("getStats"),
    getUserMedia("getUserMedia");

    private String name;

    Action(String val) {
        this.name = val;
    }

    public String toString() {
        return this.name;
    }
}
