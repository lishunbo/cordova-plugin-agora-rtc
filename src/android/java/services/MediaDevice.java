package com.agora.cordova.plugin.webrtc.services;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import com.agora.cordova.plugin.webrtc.models.MediaDeviceInfo;
import com.agora.cordova.plugin.webrtc.models.MediaStreamConstraints;

import org.webrtc.Camera1Enumerator;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

public class MediaDevice {
    private final static String TAG = MediaDevice.class.getCanonicalName();

    static Activity _activity;
    static Context _context;

    public static void Initialize(Activity activity, Context context) {
        _activity = activity;
        _context = context;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static String enumerateDevices() {
        AudioManager audioManager = (AudioManager) _activity.getSystemService(Context.AUDIO_SERVICE);
        CameraManager cameraManager = (CameraManager) _activity.getSystemService(Context.CAMERA_SERVICE);

        List<MediaDeviceInfo> infos = new ArrayList<>();

        AudioDeviceInfo[] audioInputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);

        int builtinMicCnt = 1;
        for (AudioDeviceInfo device :
                audioInputDevices) {

            String label = "";
            switch (device.getType()) {
                case AudioDeviceInfo.TYPE_BUILTIN_MIC:
                    label = device.getProductName().toString() + " Built-in Microphone " + builtinMicCnt++;
                    break;
                case AudioDeviceInfo.TYPE_TELEPHONY:
                    label = device.getProductName().toString() + " Telephony Microphone";
                    break;
                case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                    label = device.getProductName().toString() + " Wired Microphone";
                    break;
                case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                    label = device.getProductName().toString() + " Bluetooth Microphone";
                    break;
                case AudioDeviceInfo.TYPE_USB_DEVICE:
                    label = device.getProductName().toString() + " USB Microphone";
                    break;
                case AudioDeviceInfo.TYPE_USB_HEADSET:
                    label = device.getProductName().toString() + " USB Headphones Microphone";
                    break;
                default:
                    label = Integer.toString(device.getType());
                    break;
            }

            MediaDeviceInfo info = new MediaDeviceInfo(Integer.toString(device.getId()), "", "audioinput", label);

            infos.add(info);
        }

        AudioDeviceInfo[] audioOutputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

        for (AudioDeviceInfo device :
                audioOutputDevices) {

            String label = "";
            switch (device.getType()) {
                case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER:
                    label = device.getProductName().toString() + " Built-in Speaker";
                    break;
                case AudioDeviceInfo.TYPE_BUILTIN_EARPIECE:
                    label = device.getProductName().toString() + " Earphone Speaker";
                    break;
                case AudioDeviceInfo.TYPE_TELEPHONY:
                    label = device.getProductName().toString() + " Telephony Speaker";
                    break;
                case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                    label = device.getProductName().toString() + " Bluetooth Speaker";
                    break;
                case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                    label = device.getProductName().toString() + " Bluetooth A2DP Speaker";
                    break;
                case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                    label = device.getProductName().toString() + " Wired Headphones Speaker";
                    break;
                default:
                    label = Integer.toString(device.getType());
                    break;
            }

            MediaDeviceInfo info = new MediaDeviceInfo(Integer.toString(device.getId()), "", "audiooutput", label);

            infos.add(info);
        }

        try {
            String[] cameraIds = cameraManager.getCameraIdList();
            int externalCamCnt = 1;
            for (String cameraId : cameraIds) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

                String label = "";
                switch (characteristics.get(CameraCharacteristics.LENS_FACING)) {
                    case CameraCharacteristics.LENS_FACING_FRONT:
                        label = "Front Camera";
                        break;
                    case CameraCharacteristics.LENS_FACING_BACK:
                        label = "Back Camera";
                        break;
                    case CameraCharacteristics.LENS_FACING_EXTERNAL:
                        label = "External Camera " + externalCamCnt++;
                        break;
                    default:
                        label = "Unknown Camera";
                }

                MediaDeviceInfo info = new MediaDeviceInfo(cameraId, "", "videoinput", label);

                infos.add(info);
            }
        } catch (Exception e) {
            Log.e(TAG, "enumerate CameraList exception: " + e.toString());
        }

        boolean firstComma = false;

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (MediaDeviceInfo info :
                infos) {
            if (firstComma) {
                builder.append(",");
            } else {
                firstComma = true;
            }
            builder.append(info.toString());
        }
        builder.append("]");

        return builder.toString();
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
