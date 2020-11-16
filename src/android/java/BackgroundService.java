package com.agora.cordova.plugin.webrtc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

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
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

public class BackgroundService extends Service implements RTCPeerConnection.PCViewer {
    private final static String TAG = BackgroundService.class.getCanonicalName();

    String hook_id;
    String service_id;
    MessageBusClient client;

    List<RTCPeerConnection> allPC = new LinkedList<>();

    WindowManager mWindowManager;

    BackgroundService that;
    Handler handler;
    SurfaceViewRenderer view;

    void getUserMedia(MediaStreamConstraints constraints) {
        client.getUserMediaResp();
    }

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        handler = new Handler();
        super.onCreate();

        that = this;

        view = new SurfaceViewRenderer(getApplicationContext());
        view.setMirror(false);
        view.init(PCFactory.eglBase(), new RendererCommon.RendererEvents() {
            @Override
            public void onFirstFrameRendered() {
                Log.v(TAG, "onFirstFrameRendered");
            }

            @Override
            public void onFrameResolutionChanged(int i, int i1, int i2) {
                Log.v(TAG, "onFrameResolutionChanged");

            }
        });

        view.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        view.setMirror(true);
        view.setZOrderMediaOverlay(true);
        Log.v(TAG, "onCreate");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.v(TAG, "onStartCommand");

        hook_id = intent.getStringExtra(getString(R.string.hook_id));
        service_id = getString(R.string.service_id);
        try {
            client = new MessageBusClient(new URI(getString(R.string.internalws) + service_id.toString()));
            client.setReuseAddr(true);
            client.setTcpNoDelay(true);
            client.connectBlocking();
        } catch (Exception e) {
            Log.e(TAG, "Fault, cannot create message bus client" + e.toString());
        }
        Log.e(TAG, "found holder:" + hook_id);

        PCFactory.initializationOnce(getApplicationContext());

        return START_STICKY;
    }

    void createInstance(String id, RTCConfiguration cfg) {
//        pc_local = ;
        allPC.add(new RTCPeerConnection(this, hook_id, id, getString(R.string.internalws), cfg));
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    public VideoTrack getLocalVideoTrackAndPlay(boolean isFront, int w, int h, int fps) {
        VideoCapturer videoCapturer = createCameraCapturer(isFront);
        if (videoCapturer == null) {
            return null;
        }
        Log.v(TAG, "=============== getLocalVideoTrackAndPlay 0");
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", PCFactory.eglBase());
        VideoSource videoSource = PCFactory.factory().createVideoSource(videoCapturer.isScreencast());

        videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());
        videoCapturer.startCapture(w, h, fps);

        // create VideoTrack
        VideoTrack videoTrack = PCFactory.factory().createVideoTrack("100", videoSource);
        // display in localView
//        videoTrack.addSink(pcViewer.getRemoteViewer());

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        final LayoutParams paramsF = new WindowManager.LayoutParams(
                width,
                height/3,
                LAYOUT_FLAG,
                LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        paramsF.gravity = Gravity.TOP | Gravity.LEFT;
        paramsF.x = 0;
        paramsF.y = 0;

        Log.v(TAG, "=============== getLocalVideoTrackAndPlay 1"+height+"   "+width);
        ProxyVideoSink sink = new ProxyVideoSink();
        sink.setTarget(view);

        videoTrack.addSink(sink);

        that.runOnUiThread(new Runnable() {
            @Override
            public void run() {

//                ImageView image = new ImageView(that);
//
//                image.setImageResource(R.drawable.btn_startcall_normal);

                Log.v(TAG, "=============== getLocalVideoTrackAndPlay 2");
                try {
                    mWindowManager.addView(view, paramsF);
                } catch (Exception e){
                    Log.v(TAG, "=============== getLocalVideoTrackAndPlay 2.1 "+e.toString());
                    Log.e(TAG, "maybe no permission to Draw over other apps");
                }
            }
        });



        Log.v(TAG, "=============== getLocalVideoTrackAndPlay 3");
        return videoTrack;
    }

    @Override
    public void onAddStream(MediaStream mediaStream, String usage) {
        for (VideoTrack track :
                mediaStream.videoTracks) {
            Log.v(TAG, usage + " onAddVideoTrack to remote Viewer " + track.kind() + track.id());
            track.setEnabled(true);
//            track.addSink(pcViewer.getLocalViewer());
        }

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

        void getUserMediaResp() {
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.action = Action.getUserMedia;
            msg.payload = "{}";
            send(msg.toString());
        }
    }

}
