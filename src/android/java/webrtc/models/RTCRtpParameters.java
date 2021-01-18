package io.agora.rtcn.webrtc.models;

import java.util.List;

public class RTCRtpParameters {
    static final String TAG = RtpTransceiverInit.class.getCanonicalName();

    public List<RTCRtpCodecParameters> codecs;
    public List<RTCRtpHeaderExtensionParameters> headerExtensions;
    public RTCRtcpParameters rtcp;

    public static class RTCRtpCodecParameters {
        public long channels;
        public long clockRate;
        public String mimeType;
        public long payloadType;
        public String sdpFmtpLine;
    }

    public static class RTCRtpHeaderExtensionParameters {
        public boolean encrypted;
        public long id;
        public String uri;
    }

    public static class RTCRtcpParameters {
        public String cname;
        public boolean reducedSize;
    }

}
