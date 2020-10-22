package com.agora.cordova.plugin.webrtc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.agora.demo.four.R;

import org.webrtc.Camera1Enumerator;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class WebRTCLocalActivity extends Activity {
    private final static String TAG = WebRTCLocalActivity.class.getCanonicalName();


    PeerConnectionFactory factory;
    EglBase.Context eglBase;

    SurfaceViewRenderer localView;
    MediaStream mediaStream;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_view);

        eglBase = EglBase.create().getEglBaseContext();

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

}
