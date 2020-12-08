package com.agora.cordova.plugin.webrtc;

public enum Action {
    enumerateDevices("enumerateDevices"),
    getUserMedia("getUserMedia"),
    stopMediaStreamTrack("stopMediaStreamTrack"),

    createInstance("createInstance"),
    createOffer("createOffer"),
    setLocalDescription("setLocalDescription"),
    setRemoteDescription("setRemoteDescription"),
    addIceCandidate("addIceCandidate"),
    close("close"),
    removeTrack("removeTrack"),
    getTransceivers("getTransceivers"),
    addTransceiver("addTransceiver"),
    addTrack("addTrack"),
    getStats("getStats"),
    replaceTrack("replaceTrack"),

    setSenderParameter("setSenderParameter"),

    onIceCandidate("onIceCandidate"),
    onICEConnectionStateChange("onICEConnectionStateChange"),
    onConnectionStateChange("onConnectionStateChange"),
    onSignalingStateChange("onSignalingStateChange"),
    onIceGatheringChange("onIceGatheringChange"),
    onIceConnectionReceivingChange("onIceConnectionReceivingChange"),
    onIceCandidatesRemoved("onIceCandidatesRemoved"),
    onRenegotiationNeeded("onRenegotiationNeeded"),

    onAddTrack("onAddTrack"),


    MaxAction("maxAction");

    private String name;

    Action(String val) {
        this.name = val;
    }

    public String toString() {
        return this.name;
    }
}
