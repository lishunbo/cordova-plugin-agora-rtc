package com.agora.cordova.plugin.webrtc.services;

import android.content.Context;

import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;

import org.webrtc.PeerConnectionFactory;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;

import java.util.concurrent.atomic.AtomicBoolean;


public class PCFactory {
    public PeerConnectionFactory factory;
    public EglBase.Context eglBase;

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

    public static void initializationOnce(Context applicationContext) {
        if (once.get()) return;
        if (once.compareAndSet(false, true)) {
            if (_factory == null) {
                _factory = new PCFactory();
            }

            _factory.eglBase = EglBase.create().getEglBaseContext();
            PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(applicationContext)
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
                    .setVideoEncoderFactory(defaultVideoEncoderFactory)
                    .setVideoDecoderFactory(defaultVideoDecoderFactory)
                    .createPeerConnectionFactory();
        }

    }
}
