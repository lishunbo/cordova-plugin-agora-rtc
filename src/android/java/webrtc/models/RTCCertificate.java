package io.agora.rtc.webrtc.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
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
