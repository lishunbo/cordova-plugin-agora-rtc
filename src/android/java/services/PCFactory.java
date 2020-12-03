package com.agora.cordova.plugin.webrtc.services;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;

import org.webrtc.Loggable;
import org.webrtc.Logging;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;
import org.webrtc.voiceengine.WebRtcAudioRecord;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class PCFactory {
    public PeerConnectionFactory factory;
    public EglBase.Context eglBase;
    JavaAudioDeviceModule adm;

    static PCFactory _factory = null;
    static final AtomicBoolean once = new AtomicBoolean();
    static Context context;

    private PCFactory() {
    }

    public static PeerConnectionFactory factory() {
        if (_factory == null) {
            _factory = Builder.createPCFactory();
        }
        return _factory.factory;
    }

    public static JavaAudioDeviceModule audioDeviceModule() {
        if (_factory == null) {
            _factory = Builder.createPCFactory();
        }
        return _factory.adm;
    }

    public static EglBase.Context eglBase() {
        if (_factory == null) {
            _factory = new PCFactory();
        }
        return _factory.eglBase;
    }

    static class PCFLoggable implements Loggable {
        static final String TAG = "native.webrtc";

        @Override
        public void onLogMessage(String s, Logging.Severity severity, String s1) {
            switch (severity) {
                case LS_VERBOSE:
                    Log.v(TAG, s + " " + s1);
                    break;
                case LS_INFO:
                    Log.i(TAG, s + " " + s1);
                case LS_WARNING:
                    Log.w(TAG, s + " " + s1);
                case LS_ERROR:
                    Log.e(TAG, s + " " + s1);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void initializationOnce(Context applicationContext) {
        if (once.get()) return;
        if (once.compareAndSet(false, true)) {
            context = applicationContext;
            PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(applicationContext)
//                    .setInjectableLogger(new PCFLoggable(), Logging.Severity.LS_WARNING)
                    .createInitializationOptions());

            if (_factory == null) {
                _factory = Builder.createPCFactory(applicationContext, "", 0, false, null);
            }
        }
    }

    public static class Builder {
        PeerConnectionFactory.Builder builder = null;
        PeerConnectionFactory.Options options = null;
        EglBase.Context eglBase;
        DefaultVideoEncoderFactory defaultVideoEncoderFactory = null;
        DefaultVideoDecoderFactory defaultVideoDecoderFactory = null;

        static Builder that = new Builder();

        private Builder() {
            builder = PeerConnectionFactory.builder();
            options = new PeerConnectionFactory.Options();
            eglBase = EglBase.create().getEglBaseContext();
            defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(eglBase, true, true);
            defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(eglBase);
        }

        public static PCFactory createPCFactory() {
            PCFactory factory = new PCFactory();
            JavaAudioDeviceModule.Builder admBuilder = JavaAudioDeviceModule.builder(context);
            factory.adm = (JavaAudioDeviceModule) admBuilder.createAudioDeviceModule();

            factory.factory = that.builder.setOptions(that.options)
                    .setAudioDeviceModule(factory.adm)
                    .setVideoDecoderFactory(that.defaultVideoDecoderFactory)
                    .setVideoEncoderFactory(that.defaultVideoEncoderFactory)
                    .createPeerConnectionFactory();
            return factory;
        }

        @TargetApi(Build.VERSION_CODES.M)
        public static PCFactory createPCFactory(Context applicationContext, String deviceId, int sampleRate, boolean stereo, JavaAudioDeviceModule.SamplesReadyCallback callback) {
            PCFactory factory = new PCFactory();

            JavaAudioDeviceModule.Builder admBuilder = JavaAudioDeviceModule.builder(applicationContext)
                    .setUseStereoInput(stereo);
            if (sampleRate != 0) {
                admBuilder.setInputSampleRate(sampleRate);
            }


            factory.adm = (JavaAudioDeviceModule) admBuilder.createAudioDeviceModule();

            if (deviceId.length() > 0) {
                AudioDeviceInfo info = MediaDevice.getAudioDeviceByID(deviceId);
                if (info != null) {
                    factory.adm.setPreferredInputDevice(info);
                }
            }

            factory.factory = that.builder.setOptions(that.options)
                    .setAudioDeviceModule(factory.adm)
                    .setVideoDecoderFactory(that.defaultVideoDecoderFactory)
                    .setVideoEncoderFactory(that.defaultVideoEncoderFactory)
                    .createPeerConnectionFactory();
//            adm.release();
            return factory;
        }
    }
}
