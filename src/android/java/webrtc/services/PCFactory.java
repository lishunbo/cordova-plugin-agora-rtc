package io.agora.rtcn.webrtc.services;

import android.content.Context;
import android.util.Log;

import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.Loggable;
import org.webrtc.Logging;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.audio.JavaAudioDeviceModule;

import java.util.concurrent.atomic.AtomicBoolean;

import io.agora.rtcn.media.services.MediaDevice;


public class PCFactory {
    public PeerConnectionFactory factory;
    public EglBase.Context eglBase;
    JavaAudioDeviceModule adm;

    static PCFactory _factory = null;
    static final AtomicBoolean once = new AtomicBoolean();

    private PCFactory() {
    }

    public static PCFactory GetInstance() {
        if (_factory == null) {
            _factory = new PCFactory();
        }
        return _factory;
    }

    public static PeerConnectionFactory factory() {
        if (_factory == null) {
            _factory = new PCFactory();
        }
        return _factory.factory;
    }

    public static EglBase.Context eglBase() {
        if (_factory == null) {
            _factory = new PCFactory();
        }
        return _factory.eglBase;
    }

    public static JavaAudioDeviceModule audioDeviceModule() {
        if (_factory == null) {
            _factory = new PCFactory();
        }
        return _factory.adm;
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

    public class abc implements JavaAudioDeviceModule.SamplesReadyCallback {

        @Override
        public void onWebRtcAudioRecordSamplesReady(JavaAudioDeviceModule.AudioSamples audioSamples) {

        }
    }

    public static void initializationOnce(Context applicationContext) {
        if (once.get()) return;
        if (once.compareAndSet(false, true)) {
            if (_factory == null) {
                _factory = new PCFactory();
            }
            _factory.adm = (JavaAudioDeviceModule) JavaAudioDeviceModule.builder(applicationContext)
                    .setSamplesReadyCallback(MediaDevice.LocalAudioSampleSupervisor.supervisor)
                    .createAudioDeviceModule();

            _factory.eglBase = EglBase.create().getEglBaseContext();
            PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(applicationContext)
//                    .setInjectableLogger(new PCFLoggable(), Logging.Severity.LS_WARNING)
                    .createInitializationOptions());
            PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
            DefaultVideoEncoderFactory defaultVideoEncoderFactory =
                    new DefaultVideoEncoderFactory(_factory.eglBase, true, true);
            DefaultVideoDecoderFactory defaultVideoDecoderFactory =
                    new DefaultVideoDecoderFactory(_factory.eglBase);

            SoftwareVideoDecoderFactory dec = new SoftwareVideoDecoderFactory();
            SoftwareVideoEncoderFactory enc = new SoftwareVideoEncoderFactory();
            dec.getSupportedCodecs();
            enc.getSupportedCodecs();
            defaultVideoDecoderFactory.getSupportedCodecs();
            _factory.factory = PeerConnectionFactory.builder()
                    .setOptions(options)
                    .setAudioDeviceModule(_factory.adm)
                    .setVideoEncoderFactory(defaultVideoEncoderFactory)
                    .setVideoDecoderFactory(defaultVideoDecoderFactory)
                    .createPeerConnectionFactory();
            _factory.adm.release();
        }

    }
}
