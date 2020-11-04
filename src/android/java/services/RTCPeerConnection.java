package com.agora.cordova.plugin.webrtc.services;

import android.content.Context;
import android.util.Log;

import com.agora.cordova.plugin.webrtc.Action;
import com.agora.cordova.plugin.webrtc.models.RTCConfiguration;
import com.agora.cordova.plugin.webrtc.utils.MessageBus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSink;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

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
        Context getAppContext();

        VideoSink getLocalViewer();

        VideoSink getRemoteViewer();

        VideoCapturer getVideoCapturer();
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
        VideoCapturer videoCapturer = pcViewer.getVideoCapturer();
        if (videoCapturer == null) {
            client.addTrackResp();
            return;
        }
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", PCFactory.eglBase());
        VideoSource videoSource = PCFactory.factory().createVideoSource(videoCapturer.isScreencast());

        videoCapturer.initialize(surfaceTextureHelper, pcViewer.getAppContext(), videoSource.getCapturerObserver());
        videoCapturer.startCapture(810, 1080, 20);

        // create VideoTrack
        VideoTrack videoTrack = PCFactory.factory().createVideoTrack("100", videoSource);
        // display in localView
        videoTrack.addSink(pcViewer.getLocalViewer());
        MediaStream mediaStream = PCFactory.factory().createLocalMediaStream("localMediaStream");
        mediaStream.addTrack(videoTrack);

        peerConnection.addStream(mediaStream);
        client.addTrackResp();
    }

    void createOffer() {
        MediaConstraints mediaConstraints = new MediaConstraints();
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        peerConnection.createOffer(new Observer("createOffer:" + id) {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                String offer = "";
                try {
                    JSONObject sdp = new JSONObject();
                    sdp.put("type", "offer");
                    sdp.put("sdp", sessionDescription.description);
                    offer = sdp.toString();
                } catch (JSONException e) {
                    Log.e(TAG, "CreateOffer success buf to json string failed:" + e.toString());
                } finally {
                    client.createOfferResp(offer);
                }
            }
        }, mediaConstraints);
    }

    void setLocalDescription(String sdp) {
        try {
            JSONObject obj = new JSONObject(sdp);
            peerConnection.setLocalDescription(new Observer("setLocalDescription:" + id) {
            }, new SessionDescription(SessionDescription.Type.fromCanonicalForm(obj.getString("type")), obj.getString("description")));
        } catch (Exception e) {
            Log.e(TAG, "setLocalDescription exception:" + e.toString());
        }
        client.setLocalDescriptionResp();
    }

    void setRemoteDescription(String answer) {
        Log.e(TAG, "Debug....setRemoteDescription:" + answer);
        try {
            JSONObject obj = new JSONObject(answer);
            Log.e(TAG, "Debug....type:" + obj.getString("type"));
            Log.e(TAG, "Debug....sdp:" + obj.getString("description"));
            peerConnection.setRemoteDescription(new Observer("setRemoteDescription:" + id), new SessionDescription(SessionDescription.Type.fromCanonicalForm(obj.getString("type")), obj.getString("description")));
        } catch (Exception e) {
            Log.e(TAG, "setLocalDescription exception:" + e.toString());
        }
        client.setRemoteDescriptionResp();
    }

    void addIceCandidate(String candidate) {
        try {
            JSONObject obj = new JSONObject(candidate);
            Log.e(TAG, "Debug....addIceCandidate:" + candidate);
            Log.e(TAG, "Debug....sdpMid:" + obj.getString("sdpMid"));
            Log.e(TAG, "Debug....sdpMLineIndex:" + obj.getInt("sdpMLineIndex"));
            Log.e(TAG, "Debug....candidate:" + obj.getString("candidate"));
            peerConnection.addIceCandidate(new IceCandidate(obj.getString("sdpMid"), obj.getInt("sdpMLineIndex"), obj.getString("candidate")));
        } catch (Exception e) {
            Log.e(TAG, "setLocalDescription exception:" + e.toString());
        }
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
                    setLocalDescription(msg.payload);
                    break;
                case setRemoteDescription:
                    setRemoteDescription(msg.payload);
                    break;
                case addIceCandidate:
                    addIceCandidate(msg.payload);
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
            Log.d(TAG, "Debug.................. createInstanceResp");
        }


        public void addTrackResp() {
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.object = id;
            msg.action = Action.addTrack;
//            msg.payload = "{}";
            send(msg.toString());
        }

        public void createOfferResp(String offer) {
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.object = id;
            msg.action = Action.createOffer;
            msg.payload = offer;
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

        public void onIceCandidate(String candidate) {
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.object = id;
            msg.action = Action.onIceCandidate;
            msg.payload = candidate;
            send(msg.toString());
        }

        public void onICEConnectionStateChange(String state) {
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.object = id;
            msg.action = Action.onICEConnectionStateChange;
            msg.payload = state;
            send(msg.toString());
        }

        public void onConnectionStateChange(String state) {
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.object = id;
            msg.action = Action.onConnectionStateChange;
            msg.payload = state;
            send(msg.toString());
        }

        public void onSignalingStateChange(String state) {
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.object = id;
            msg.action = Action.onSignalingStateChange;
            msg.payload = state;
            send(msg.toString());
        }
    }

    public class Observer implements SdpObserver, PeerConnection.Observer {
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
            client.onSignalingStateChange(signalingState.toString().toLowerCase());
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.v(TAG, usage + " onIceConnectionChange " + iceConnectionState.toString());

            client.onICEConnectionStateChange(iceConnectionState.toString().toLowerCase());
        }

        @Override
        public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
            Log.v(TAG, usage + " onConnectionChange " + newState.toString());

            client.onConnectionStateChange(newState.toString().toLowerCase());
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

            Log.v(TAG, usage + " onIceConnectionReceivingChange ");
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

            Log.v(TAG, usage + " onIceGatheringChange " + iceGatheringState.toString());
            if (iceGatheringState == PeerConnection.IceGatheringState.COMPLETE) {
                client.onIceCandidate("");
            }
        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {

            Log.v(TAG, usage + " onIceCandidate " + iceCandidate.toString());
            Log.v(TAG, usage + " onIceCandidateSDP " + iceCandidate.sdp);
            JSONObject obj = new JSONObject();
            try {
                obj.put("sdpMid", iceCandidate.sdpMid);
                obj.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
                obj.put("candidate", iceCandidate.sdp);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            client.onIceCandidate(obj.toString());
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

            Log.v(TAG, usage + " onIceCandidatesRemoved ");
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {

            Log.v(TAG, usage + " onAddStream " + mediaStream.videoTracks.size());
            for (VideoTrack track :
                    mediaStream.videoTracks) {
                Log.v(TAG, usage + " onAddVideoTrack to remote Viewer " + track.toString());
                track.addSink(pcViewer.getRemoteViewer());
            }
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
