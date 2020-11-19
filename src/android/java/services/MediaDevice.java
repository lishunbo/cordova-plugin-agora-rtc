package com.agora.cordova.plugin.webrtc.services;

import android.content.Context;
import android.util.Log;

import com.agora.cordova.plugin.webrtc.models.MediaStreamConstraints;

import org.webrtc.Camera1Enumerator;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class MediaDevice {
    private final static String TAG = MediaDevice.class.getCanonicalName();

    static Context _context;
    public static void Initialize(Context context){
        _context = context;
    }

    public static String getUserMedia(MediaStreamConstraints constraints) {
        if (constraints == null) {
            Log.e(TAG, "fault, getUserMedia no sdp data");
            return "";
        }

        VideoTrack track = createLocalVideoTrack(true, 500, 400, 20);
        RTCPeerConnection.cacheMediaStreamTrack(track);

        return "[" + RTCPeerConnection.summaryMediaStreamTrack(track) + "]";
    }

    public static VideoTrack createLocalVideoTrack(boolean isFront, int w, int h, int fps) {
        VideoCapturer videoCapturer = createCameraCapturer(isFront);
        if (videoCapturer == null) {
            return null;
        }
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", PCFactory.eglBase());
        VideoSource videoSource = PCFactory.factory().createVideoSource(videoCapturer.isScreencast());

        videoCapturer.initialize(surfaceTextureHelper, _context, videoSource.getCapturerObserver());
        videoCapturer.startCapture(w, h, fps);

        // create VideoTrack
        VideoTrack videoTrack = PCFactory.factory().createVideoTrack("100", videoSource);
        // display in localView

        return videoTrack;
    }

    private static VideoCapturer createCameraCapturer(boolean isFront) {
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
