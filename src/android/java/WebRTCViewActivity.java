package com.agora.cordova.plugin.webrtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.agora.cordova.plugin.webrtc.models.MediaStreamConstraints;
import com.agora.cordova.plugin.webrtc.models.RTCConfiguration;
import com.agora.cordova.plugin.webrtc.services.PCFactory;
import com.agora.cordova.plugin.webrtc.services.RTCPeerConnection;
import com.agora.cordova.plugin.webrtc.utils.MessageBus;
import com.agora.demo.four.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.webrtc.Camera1Enumerator;
import org.webrtc.MediaStream;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;

import java.net.URI;

public class WebRTCViewActivity extends Activity implements RTCPeerConnection.PCViewer {
    private final static String TAG = WebRTCViewActivity.class.getCanonicalName();

    String webrtc_view_id;
    String hook_id;

//    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();

    RTCPeerConnection pc_local;
    RTCPeerConnection pc_remote;
    SurfaceViewRenderer localView;
    MediaStream mediaStream;

    MessageBusClient client;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.local_view);

        Intent intent = getIntent();
        hook_id = intent.getStringExtra(getString(R.string.hook_id));
        webrtc_view_id = getString(R.string.webrtc_view_id);
        try {
            client = new MessageBusClient(new URI(getString(R.string.internalws) + webrtc_view_id.toString()));
            client.setReuseAddr(true);
            client.setTcpNoDelay(true);
            client.connectBlocking();
        } catch (Exception e) {
            Log.e(TAG, "Fault, cannot create messagebus client" + e.toString());
        }
        Log.e(TAG, "found holder:" + hook_id);

        PCFactory.initializationOnce(getApplicationContext());


//        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", PCFactory.eglBase());
//        // create VideoCapturer
//        VideoCapturer videoCapturer = createCameraCapturer(true);
//        if (videoCapturer == null) {
//            Log.e(TAG, "Cannot create CameraCapture...");
//            return;
//        }
//        VideoSource videoSource = PCFactory.factory().createVideoSource(videoCapturer.isScreencast());
//        videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());
//
//        videoCapturer.startCapture(810, 1080, 30);
//
//        localView = findViewById(R.id.local_view);
//        localView.setMirror(true);
//        localView.init(PCFactory.eglBase(), null);
//
//        // create VideoTrack
//        VideoTrack videoTrack = PCFactory.factory().createVideoTrack("100", videoSource);
////        // display in localView
//        videoTrack.addSink(localView);
//
//        mediaStream = PCFactory.factory().createLocalMediaStream("mediaStream");
//        mediaStream.addTrack(videoTrack);
//
//        iceServers.add(PeerConnection.IceServer.builder("stun:10.83.2.233:3478").createIceServer());
//
//        local = PCFactory.factory().createPeerConnection(iceServers, new Peer("createPeerConnection"));
//        local.addStream(mediaStream);
//        local.createOffer(new Peer("createOffer") {
//            @Override
//            public void onCreateSuccess(SessionDescription sessionDescription) {
//                local.setLocalDescription(new Peer("setoffer"), sessionDescription);
////                Log.v(TAG, " onCreateSuccess by overwrite" + sessionDescription.description);
//
//                MessageBus.Message msg = new MessageBus.Message();
//                msg.Target = hook_id;
//                msg.Action = Action.valueOf(sessionDescription.type.canonicalForm());
//                msg.Payload = sessionDescription.description;
//
//                client.send(msg.toString());
//            }
//        }, new MediaConstraints());

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

    public void onSwitchCameraClicked(View view) {
    }

    public void onLocalAudioMuteClicked(View view) {
    }

    public void onCallClicked(View view) {
    }

    void createInstance(String id, RTCConfiguration cfg) {
        pc_local = new RTCPeerConnection(this, hook_id, id, getString(R.string.internalws), cfg);
    }

    void getUserMedia(MediaStreamConstraints constraints){
        client.getUserMediaResp();
    }

//    @Override
//    public PeerConnection createPeerConnection(LinkedList<PeerConnection.IceServer> iceServers, RTCPeerConnection.Observer observer) {
//        return ;
//    }

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
            assert msg != null;
            switch (msg.action) {
                case createInstance:
                    createInstance(msg.object, RTCConfiguration.fromJson(msg.payload));
                    break;
                case getUserMedia:
                    getUserMedia(MediaStreamConstraints.fromJson(msg.payload));
                    break;
                default:
                    Log.e(TAG, "onMessage not implement action:" + msg.action);
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

        void getUserMediaResp(){
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.action = Action.getUserMedia;
            msg.payload = "{}";
            send(msg.toString());
        }
    }

}
