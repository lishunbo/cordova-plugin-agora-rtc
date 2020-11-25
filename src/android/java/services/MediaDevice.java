package com.agora.cordova.plugin.webrtc.services;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.agora.cordova.plugin.webrtc.models.MediaDeviceInfo;
import com.agora.cordova.plugin.webrtc.models.MediaStreamConstraints;
import com.agora.cordova.plugin.webrtc.models.MediaStreamTrackWrapper;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.MediaConstraints;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MediaDevice {
    private final static String TAG = MediaDevice.class.getCanonicalName();

    static Activity activity;
    static Context context;

    public static void initialize(Activity activity, Context context) {
        MediaDevice.activity = activity;
        MediaDevice.context = context;
        SettingsContentObserver.initialize(activity, new Handler());
    }

    public static void unInitialize() {
        SettingsContentObserver.unInitialize();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static String enumerateDevices() {
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

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
//                case AudioDeviceInfo.TYPE_TELEPHONY:
//                    label = device.getProductName().toString() + " Telephony Microphone";
//                    break;
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
//                case AudioDeviceInfo.TYPE_TELEPHONY:
//                    label = device.getProductName().toString() + " Telephony Speaker";
//                    break;
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

        StringBuilder builder = new StringBuilder();
        builder.append("[");

        if (constraints.audio != null) {
            MediaStreamTrackWrapper wrapper = createLocalAudioTrack();
            builder.append(wrapper.toString());
        }

        if (constraints.video != null) {
            MediaStreamTrackWrapper wrapper = createLocalVideoTrack(true, 500, 400, 20);
            builder.append(wrapper.toString());
        }
        builder.append("]");


        return builder.toString();
    }

    public static int getMaxVolume() {
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    @TargetApi(Build.VERSION_CODES.P)
    public static int getMinVolume() {
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC);
    }

    public static int getVolume() {
        return SettingsContentObserver.getSettingsContentObserver().getVolume();
    }

    public static void setVolume(int volume) {
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.ADJUST_SAME);
    }

    public static MediaStreamTrackWrapper createLocalAudioTrack() {
        AudioSource audioSource = PCFactory.factory().createAudioSource(new MediaConstraints());
        AudioTrack audioTrack = PCFactory.factory().createAudioTrack(UUID.randomUUID().toString(), audioSource);

        return MediaStreamTrackWrapper.cacheMediaStreamTrack("", audioTrack, audioSource);
    }

    public static MediaStreamTrackWrapper createLocalVideoTrack(boolean isFront, int w, int h, int fps) {
        VideoCapturer videoCapturer = createCameraCapturer(isFront);
        if (videoCapturer == null) {
            return null;
        }
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", PCFactory.eglBase());
        VideoSource videoSource = PCFactory.factory().createVideoSource(videoCapturer.isScreencast());

        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());
        videoCapturer.startCapture(w, h, fps);

        VideoTrack videoTrack = PCFactory.factory().createVideoTrack(UUID.randomUUID().toString(), videoSource);

        return MediaStreamTrackWrapper.cacheMediaStreamTrack("", videoTrack, videoCapturer, surfaceTextureHelper, videoSource);
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

    public static void reset() {
        MediaStreamTrackWrapper.reset();
        SettingsContentObserver.getSettingsContentObserver().clear();
    }
}
