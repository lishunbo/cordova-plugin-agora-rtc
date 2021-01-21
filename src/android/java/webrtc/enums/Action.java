package io.agora.rtc.webrtc.enums;

public enum Action {
    createPC("createPC"),
    setConfiguration("setConfiguration"),
    createOffer("createOffer"),
    createAnswer("createAnswer"),
    createDataChannel("createDataChannel"),
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

    closeDC("closeDC"),
    sendDC("sendDC"),

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
    onBufferedAmountChange("bufferedamountchange"),
    onStateChange("statechange"),
    onMessage("message"),
    onLocalSDP("localSDP"),
    onRemoteSDP("remoteSDP"),
    onConfiguration("configuration"),
    onSender("sender"),
    onReceiver("receiver"),

    MaxAction("maxAction");

    private String name;

    Action(String val) {
        this.name = val;
    }

    public String toString() {
        return this.name;
    }
}
