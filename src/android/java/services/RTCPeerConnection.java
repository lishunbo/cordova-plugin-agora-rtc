package com.agora.cordova.plugin.webrtc.services;

import android.util.Log;

import com.agora.cordova.plugin.webrtc.Action;
import com.agora.cordova.plugin.webrtc.models.RTCConfiguration;
import com.agora.cordova.plugin.webrtc.utils.MessageBus;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;


public class RTCPeerConnection {
    static final String TAG = RTCPeerConnection.class.getCanonicalName();

    PCViewer pcViewer;
    String hook_id;
    String id;
    MessageBusClient client;

    RTCConfiguration config;
    PeerConnection peerConnection;


    public interface PCViewer {
//        PeerConnection createPeerConnection(LinkedList<PeerConnection.IceServer> iceServers, Observer observer);
    }

    public RTCPeerConnection(PCViewer pcviewer, String hook_id, String id, String internalws, RTCConfiguration config) {
        this.pcViewer = pcviewer;
        this.hook_id = hook_id;
        this.id = id;
        this.config = config;

        try {
            this.client = new MessageBusClient(new URI(internalws + this.id));
            this.client.connect();
        } catch (URISyntaxException e) {
            Log.e(TAG, "new MessageBusClient error:" + e.toString());
        }
    }


    public String getId() {
        return id;
    }

    void createInstance() {
        LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:10.83.2.233:3478").createIceServer());

        peerConnection = PCFactory.factory().createPeerConnection(iceServers, new Observer("createPeerConnection:" + id));

        client.createInstanceResp();
    }

    void addTrack() {
        client.addTrackResp();
    }

    void createOffer() {
        client.createOfferResp();
    }

    void setLocalDescription() {
        client.setLocalDescriptionResp();
    }

    void setRemoteDescription() {
        client.setRemoteDescriptionResp();
    }

    void addIceCandidate() {
        client.addIceCandidateResp();
    }

    public class MessageBusClient extends WebSocketClient {

        public MessageBusClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.v(TAG, "Plugin connected to server:" + handshakedata.getHttpStatusMessage());
            createInstance();
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.e(TAG, "onClose: " + reason);
        }

        @Override
        public void onError(Exception ex) {
            Log.e(TAG, "onError" + ex.toString());
        }

        @Override
        public void onMessage(String message) {
            Log.e(TAG, "onMessage:" + message);
            MessageBus.Message msg = MessageBus.Message.formString(message);
            if (!msg.target.equals(id.toString())) {
                Log.e(TAG, "invalid message has been received");
                return;
            }

            switch (msg.action) {
                case addTrack:
                    addTrack();
                    break;
                case createOffer:
                    createOffer();
                    break;
                case setLocalDescription:
                    setLocalDescription();
                    break;
                case setRemoteDescription:
                    setRemoteDescription();
                    break;
                case addIceCandidate:
                    addIceCandidate();
                    break;
                default:
                    Log.e(TAG, "onMessage not implement action:" + message);
            }
        }

        void createInstanceResp() {
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.object = id;
            msg.action = Action.createInstance;
//            msg.payload = "{}";
            send(msg.toString());
        }


        public void addTrackResp() {
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.object = id;
            msg.action = Action.addTrack;
//            msg.payload = "{}";
            send(msg.toString());
        }

        public void createOfferResp() {
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.object = id;
            msg.action = Action.createOffer;
//            msg.payload = "{}";
            send(msg.toString());
        }

        public void setLocalDescriptionResp() {
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.object = id;
            msg.action = Action.setLocalDescription;
//            msg.payload = "{}";
            send(msg.toString());
        }

        public void setRemoteDescriptionResp() {
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.object = id;
            msg.action = Action.setRemoteDescription;
//            msg.payload = "{}";
            send(msg.toString());
        }

        public void addIceCandidateResp() {
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.object = id;
            msg.action = Action.addIceCandidate;
//            msg.payload = "{}";
            send(msg.toString());
        }
    }

    public static class Observer implements SdpObserver, PeerConnection.Observer {
        private PeerConnection pc;
        private String id;
        private int endPoint;
        private String usage;

        public Observer(String u) {
            usage = u;
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.v(TAG, usage + " onSignalingChange " + signalingState.toString());
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.v(TAG, usage + " onIceConnectionChange " + iceConnectionState.toString());

        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

            Log.v(TAG, usage + " onIceConnectionReceivingChange ");
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

            Log.v(TAG, usage + " onIceGatheringChange " + iceGatheringState.toString());
            if (iceGatheringState.toString().equals("COMPLETE")) {
                Log.v(TAG, usage + " onIceGatheringChange has completed");
//                SessionDescription sdp = local.getLocalDescription();
//                client.send(sdp.type.canonicalForm(), sdp.description);
            }
        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {

            Log.v(TAG, usage + " onIceCandidate " + iceCandidate.toString());
            Log.v(TAG, usage + " onIceCandidateSDP " + iceCandidate.sdp);
//            client.send("candidate", iceCandidate.toString());
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

            Log.v(TAG, usage + " onIceCandidatesRemoved ");
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {

            Log.v(TAG, usage + " onAddStream ");
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {

            Log.v(TAG, usage + " onRemoveStream ");
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

            Log.v(TAG, usage + " onDataChannel ");
        }

        @Override
        public void onRenegotiationNeeded() {

            Log.v(TAG, usage + " onRenegotiationNeeded ");
        }

        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
            Log.v(TAG, usage + " onAddTrack ");
        }


        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {

            Log.v(TAG, usage + " onCreateSuccess" + sessionDescription.description.toString());
        }

        @Override
        public void onSetSuccess() {

            Log.v(TAG, usage + " onSetSuccess");
        }

        @Override
        public void onCreateFailure(String s) {

            Log.v(TAG, usage + " onCreateFailure" + s.toString());
        }

        @Override
        public void onSetFailure(String s) {

            Log.v(TAG, usage + " onSetFailure" + s.toString());
        }
    }
}
