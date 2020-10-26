package com.agora.cordova.plugin.webrtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.agora.cordova.plugin.webrtc.utils.MessageBus;
import com.agora.demo.four.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.webrtc.Camera1Enumerator;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.net.URI;
import java.util.LinkedList;

public class WebRTCLocalActivity extends Activity {
    private final static String TAG = WebRTCLocalActivity.class.getCanonicalName();

    String webrtclocal_id;
    String hook_id;

    PeerConnectionFactory factory;
    EglBase.Context eglBase;
    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();

    PeerConnection local;
    SurfaceViewRenderer localView;
    MediaStream mediaStream;

    MessageBusClient client;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_view);

        Intent intent = getIntent();
        hook_id = intent.getStringExtra(getString(R.string.hook_id));
        Log.e(TAG, "found holder:" + hook_id);
        webrtclocal_id = getString(R.string.webrtclocal_id);

//        Intent returnIntent = new Intent();
//        returnIntent.putExtra(getString(R.string.webrtclocal_id), webrtclocal_id);
//        setResult(Activity.RESULT_OK, returnIntent);
//        finish();

        try {
            client = new MessageBusClient(new URI(getString(R.string.internalws) + webrtclocal_id.toString()));
            client.setReuseAddr(true);
            client.setTcpNoDelay(true);
            client.connectBlocking();
        } catch (Exception e) {
            Log.e(TAG, "cannot create messagebus client" + e.toString());
        }

        eglBase = EglBase.create().getEglBaseContext();

        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(getApplicationContext())
                .createInitializationOptions());
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        DefaultVideoEncoderFactory defaultVideoEncoderFactory =
                new DefaultVideoEncoderFactory(eglBase, true, true);
        DefaultVideoDecoderFactory defaultVideoDecoderFactory =
                new DefaultVideoDecoderFactory(eglBase);
        factory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory();

        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase);
        // create VideoCapturer
        VideoCapturer videoCapturer = createCameraCapturer(true);
        if (videoCapturer == null) {
            Log.e(TAG, "Cannot create CameraCapture...");
            return;
        }
        VideoSource videoSource = factory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());

        videoCapturer.startCapture(810, 1080, 30);

        localView = findViewById(R.id.local_view);
        localView.setMirror(true);
        localView.init(eglBase, null);

        // create VideoTrack
        VideoTrack videoTrack = factory.createVideoTrack("100", videoSource);
//        // display in localView
        videoTrack.addSink(localView);

        mediaStream = factory.createLocalMediaStream("mediaStream");
        mediaStream.addTrack(videoTrack);

        iceServers.add(PeerConnection.IceServer.builder("stun:10.83.2.233:3478").createIceServer());

        local = factory.createPeerConnection(iceServers, new Peer("createPeerConnection"));
        local.addStream(mediaStream);
        local.createOffer(new Peer("createOffer") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                local.setLocalDescription(new Peer("setoffer"), sessionDescription);
//                Log.v(TAG, " onCreateSuccess by overwrite" + sessionDescription.description);

                MessageBus.Message msg = new MessageBus.Message();
                msg.Target = hook_id;
                msg.Action = sessionDescription.type.canonicalForm();
                msg.Payload = sessionDescription.description;

                client.send(msg.toString());
            }
        }, new MediaConstraints());

    }

    private VideoCapturer createCameraCapturer(boolean isFront) {
        Camera1Enumerator enumerator = new Camera1Enumerator(false);
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            Log.v(TAG, "camera names" + deviceName);
            if (isFront ? enumerator.isFrontFacing(deviceName) : enumerator.isBackFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }


    private class Peer implements SdpObserver, PeerConnection.Observer {
        private PeerConnection pc;
        private String id;
        private int endPoint;
        private String usage;

        public Peer(String u) {
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
                SessionDescription sdp = local.getLocalDescription();
                client.send(sdp.type.canonicalForm(), sdp.description);
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

    private class MessageBusClient extends WebSocketClient {
        MessageBusClient(URI uri) {
            super(uri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.e(TAG, "MessageBusClient onOpen");

        }

        @Override
        public void onMessage(String message) {

            Log.e(TAG, "onMessage:" + message);
            MessageBus.Message msg = MessageBus.Message.formString(message);
            Log.e(TAG, "onMessage 2:" + msg.Target+msg.Action);
            if (msg.Action.equals("answer")) {
                Log.v(TAG, "onMessage have answer:" + msg.Payload);
                local.setRemoteDescription(new Peer("setoffer"), new SessionDescription(SessionDescription.Type.ANSWER, msg.Payload));
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.e(TAG, "not implement onClose" + reason);
        }

        @Override
        public void onError(Exception ex) {
            Log.e(TAG, "not implement onError" + ex.toString());
        }

        public void send(String type, String payload) {
            MessageBus.Message msg = new MessageBus.Message();
            msg.Target = hook_id;
            msg.Action = type;
            msg.Payload = payload;
            send(msg.toString());
        }
    }
}
