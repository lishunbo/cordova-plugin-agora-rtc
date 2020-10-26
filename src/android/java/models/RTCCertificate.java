package com.agora.cordova.plugin.webrtc.models;

import java.util.LinkedList;
import java.util.List;

public class RTCCertificate {
    public long expires;

    List<RTCDtlsFingerprint> getFingerprints() {
        return new LinkedList<>();
    }

    public class RTCDtlsFingerprint {
        public String algorithm;
        public String value;
    }
}
