package io.agora.rtcn.webrtc.enums;

public enum Action {
    createPC("createPC"),
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

    onConnectionStateChange("connectionstatechange"),
    onDataChannel("datachannel"),
    onIceCandidate("icecandidate"),
    onIceCandidateError("icecandidateerror"),
    onIceConnectionStateChange("iceconnectionstatechange"),
    onIceGatheringStateChange("icegatheringstatechange"),
    onNegotiationNeeded("negotiationneeded"),
    onSignalingStateChange("signalingstatechange"),
    onStatsEnded("statsended"),
    onTrack("track"),

    MaxAction("maxAction");

    private String name;

    Action(String val) {
        this.name = val;
    }

    public String toString() {
        return this.name;
    }
}
