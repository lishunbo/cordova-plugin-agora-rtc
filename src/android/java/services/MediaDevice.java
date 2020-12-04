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
import android.os.Handler;
import android.util.Log;

import com.agora.cordova.plugin.webrtc.models.MediaDeviceInfo;
import com.agora.cordova.plugin.webrtc.models.MediaStreamConstraints;
import com.agora.cordova.plugin.webrtc.models.MediaStreamTrackWrapper;
import com.agora.cordova.plugin.webrtc.models.MediaTrackConstraints;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Capturer;
import org.webrtc.Camera2Enumerator;
import org.webrtc.MediaConstraints;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.audio.JavaAudioDeviceModule;
import org.webrtc.voiceengine.WebRtcAudioRecord;
import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedList;
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
    public static AudioDeviceInfo getAudioDeviceByID(String id) {
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] audioInputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
        AudioDeviceInfo target = null;
        for (AudioDeviceInfo info :
                audioInputDevices) {
            if (info.getId() == Integer.parseInt(id)) {
                target = info;
            }
        }
        return target;
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
//                case AudioDeviceInfo.TYPE_USB_DEVICE:
//                    label = device.getProductName().toString() + " USB Microphone";
//                    break;
//                case AudioDeviceInfo.TYPE_USB_HEADSET:
//                    label = device.getProductName().toString() + " USB Headphones Microphone";
//                    break;
                default:
//                    label = Integer.toString(device.getType());
                    break;
            }
            if (label.length() > 0) {
                MediaDeviceInfo info = new MediaDeviceInfo(Integer.toString(device.getId()), "", "audioinput", label);
                infos.add(info);
            }
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
//                case AudioDeviceInfo.TYPE_WIRED_HEADSET:
//                    label = device.getProductName().toString() + " Wired Headphones Speaker";
//                    break;
                default:
//                    label = Integer.toString(device.getType());
                    break;
            }
            if (label.length() > 0) {
                MediaDeviceInfo info = new MediaDeviceInfo(Integer.toString(device.getId()), "", "audiooutput", label);
                infos.add(info);
            }
        }

        try {
            Camera1Enumerator enumerator = new Camera1Enumerator(false);
            for (String name :
                    enumerator.getDeviceNames()) {

                String label = "Front Camera";
                if (!enumerator.isFrontFacing(name)) {
                    label = "Back Camera";
                }

                MediaDeviceInfo info = new MediaDeviceInfo(name, "", "videoinput", label);

                infos.add(info);
            }
//            String[] cameraIds = cameraManager.getCameraIdList();
//            int externalCamCnt = 1;
//            for (String cameraId : cameraIds) {
//                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
//
//                String label = "";
//                switch (characteristics.get(CameraCharacteristics.LENS_FACING)) {
//                    case CameraCharacteristics.LENS_FACING_FRONT:
//                        label = "Front Camera";
//                        break;
//                    case CameraCharacteristics.LENS_FACING_BACK:
//                        label = "Back Camera";
//                        break;
//                    case CameraCharacteristics.LENS_FACING_EXTERNAL:
//                        label = "External Camera " + externalCamCnt++;
//                        break;
//                    default:
//                        label = "Unknown Camera";
//                }
//
//                Log.v(TAG, "cameraid: " + cameraId);
//
//            }
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

    @TargetApi(Build.VERSION_CODES.M)
    public static void setPlaybackDevice(int deviceId){
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] audioOutputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

        //if(shouldEnableExternalSpeaker) {
        //    if(isBlueToothConnected) {
        //        // 1. case - bluetooth device
        //        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        //        mAudioManager.startBluetoothSco();
        //        mAudioManager.setBluetoothScoOn(true);
        //    } else {
        //        // 2. case - wired device
        //        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        //        mAudioManager.stopBluetoothSco();
        //        mAudioManager.setBluetoothScoOn(false);
        //        mAudioManager.setSpeakerphoneOn(false);
        //    }
        //} else {
        //   // 3. case - phone speaker
        //   mAudioManager.setMode(AudioManager.MODE_NORMAL);
        //   mAudioManager.stopBluetoothSco();
        //   mAudioManager.setBluetoothScoOn(false);
        //   mAudioManager.setSpeakerphoneOn(true);
        //}
        for (AudioDeviceInfo device :
                audioOutputDevices) {
            if (device.getId() == deviceId){
                switch (device.getType()){
                    case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER:
                        audioManager.setMode(AudioManager.MODE_NORMAL);
                        audioManager.stopBluetoothSco();
                        audioManager.setBluetoothScoOn(false);
                        audioManager.setSpeakerphoneOn(true);
                        break;
                    case AudioDeviceInfo.TYPE_BUILTIN_EARPIECE:
                        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                        audioManager.stopBluetoothSco();
                        audioManager.setBluetoothScoOn(false);
                        audioManager.setSpeakerphoneOn(false);
                        break;
                    case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                        audioManager.startBluetoothSco();
                        audioManager.setBluetoothScoOn(true);
                        audioManager.setSpeakerphoneOn(false);
                        break;
                    case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                        audioManager.startBluetoothSco();
                        audioManager.setBluetoothScoOn(false);
                        audioManager.setSpeakerphoneOn(false);
                        break;
//                    case AudioDeviceInfo.TYPE_WIRED_HEADSET:
//                        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//                        audioManager.stopBluetoothSco();
//                        audioManager.setBluetoothScoOn(false);
//                        audioManager.setSpeakerphoneOn(false);
//                        break;
                    default:
                        break;
                }
            }
        }
    }

    public static String getUserMedia(MediaStreamConstraints constraints) {
        if (constraints == null) {
            Log.e(TAG, "fault, getUserMedia no sdp data");
            return "";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("[");

        if (constraints.audio != null) {
            int sampleRate = 48000;
            int channelCount = 1;
            boolean aec = false;
            boolean echoCancellation = false;
            boolean noiseSuppression = false;
            String deviceID = null;
            if (constraints.audio.deviceId != null) {
                if (constraints.audio.deviceId.exact != null) {
                    deviceID = constraints.audio.deviceId.exact;
                }
            }
            MediaStreamTrackWrapper wrapper = createLocalAudioTrack(deviceID, sampleRate, channelCount, aec, echoCancellation, noiseSuppression);
            Log.e(TAG, "VOLUME: trackid" + wrapper.getId());
            builder.append(wrapper.toString());
        }

        if (constraints.video != null) {
            int w = 640;
            int h = 480;
            int fps = 15;
            boolean isFront = true;
            String deviceId = "";
            if (constraints.video.optional != null) {
                for (MediaTrackConstraints.Optional opt :
                        constraints.video.optional) {
                    if (opt.minWidth > 0) {
                        w = opt.minWidth;
                    } else if (opt.minHeight > 0) {
                        h = opt.minHeight;
                    } else if (opt.minFrameRate > 0) {
                        fps = opt.minFrameRate;
                    } else if (opt.sourceId != null && opt.sourceId.length() > 0) {
                        deviceId = opt.sourceId;
                    }
                }
            }
            if (constraints.video.mandatory != null) {
                deviceId = constraints.video.mandatory.deviceId;
            }

            MediaStreamTrackWrapper wrapper = createLocalVideoTrack(deviceId, isFront, w, h, fps);
            builder.append(wrapper.toString());
        }
        builder.append("]");


        return builder.toString();
    }

    public static int getMaxVolume() {
        return SettingsContentObserver.getSettingsContentObserver().getStreamMaxVolume();
    }

    @TargetApi(Build.VERSION_CODES.P)
    public static int getMinVolume() {
        return SettingsContentObserver.getSettingsContentObserver().getStreamMinVolume();
    }

    public static int getVolume() {
        return SettingsContentObserver.getSettingsContentObserver().getVolume();
    }

    public static void setVolume(int volume) {
        SettingsContentObserver.getSettingsContentObserver().setVolume(volume);
    }

    public static class LocalAudioSampleSupervisor implements JavaAudioDeviceModule.SamplesReadyCallback {
        public static final LocalAudioSampleSupervisor supervisor = new LocalAudioSampleSupervisor();
        static List<LocalAudioSampleListener> listeners = new LinkedList<>();

        public interface LocalAudioSampleListener {
            public void onAudioLevel(double level);
        }

        public void addListener(LocalAudioSampleListener listener) {
            listeners.add(listener);
        }

        public void removeListener(LocalAudioSampleListener listener) {
            listeners.remove(listener);
        }

        @Override
        public void onWebRtcAudioRecordSamplesReady(JavaAudioDeviceModule.AudioSamples audioSamples) {
            short max = (short) ~(1 << 15);
            final ByteBuffer buf = ByteBuffer.wrap(audioSamples.getData())
                    .order(ByteOrder.LITTLE_ENDIAN);
//            StringBuilder builder = new StringBuilder();

            int sample = 0;
            while (buf.hasRemaining()) {
                sample += (int) buf.getShort();
            }
            sample = (int) (sample * 2.0 / audioSamples.getData().length);
            double level = (double) sample / max;
//            builder.append(sample).append(" ").append(max).append(" ").append(level);
//            Log.v("ADD", builder.toString());

            for (LocalAudioSampleListener listener :
                    listeners) {
                if (listener != null) {
                    listener.onAudioLevel(level);
                }
            }
        }

        private LocalAudioSampleSupervisor() {
        }
    }

    public static MediaStreamTrackWrapper createLocalAudioTrack(String deviceID, int sampleRate, int channelCount, boolean aec, boolean echo, boolean noise) {
        MediaConstraints mediaConstraints = new MediaConstraints();
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation2", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googDAEchoCancellation", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googTypingNoiseDetection", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googAutoGainControl", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googAutoGainControl2", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseSuppression", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseSuppression2", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googAudioMirroring", "false"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googHighpassFilter", "true"));

//        PCFactory factory = PCFactory.Builder.createPCFactory(context, deviceID, sampleRate, false, null);

        AudioSource audioSource = PCFactory.factory().createAudioSource(new MediaConstraints());
        AudioTrack audioTrack = PCFactory.factory().createAudioTrack(UUID.randomUUID().toString(), audioSource);

        return MediaStreamTrackWrapper.cacheMediaStreamTrack("", audioTrack, audioSource);
    }

    public static MediaStreamTrackWrapper createLocalVideoTrack(String deviceId, boolean isFront, int w, int h, int fps) {
        VideoCapturer videoCapturer = createCameraCapturer(deviceId, isFront);
        if (videoCapturer == null) {
            return null;
        }
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", PCFactory.eglBase());
        MediaConstraints mediaConstraints = new MediaConstraints();
        VideoSource videoSource = PCFactory.factory().createVideoSource(videoCapturer.isScreencast());

        videoSource.adaptOutputFormat(w, h, fps);

        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());
        videoCapturer.startCapture(w, h, fps);

        VideoTrack videoTrack = PCFactory.factory().createVideoTrack(UUID.randomUUID().toString(), videoSource);

        return MediaStreamTrackWrapper.cacheMediaStreamTrack("", videoTrack, videoCapturer, surfaceTextureHelper, videoSource);
    }

    private static VideoCapturer createCameraCapturer(String deviceId, boolean isFront) {

        Log.w(TAG, "camera2 is support: " + Camera2Enumerator.isSupported(context));

        Camera1Enumerator enumerator = new Camera1Enumerator(false);
        if (deviceId.length() > 0) {
            return enumerator.createCapturer(deviceId, null);
        }

        final String[] deviceNames = enumerator.getDeviceNames();
        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            Log.v(TAG, "camera names" + deviceName);
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            Log.v(TAG, "camera names" + deviceName);
            if (!enumerator.isFrontFacing(deviceName)) {
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
