import {RTCIceServer} from "RTCIceServer"

class RTCConfiguration {
    sequence<RTCIceServer> iceServers;
    RTCIceTransportPolicy iceTransportPolicy;
    RTCBundlePolicy bundlePolicy;
    RTCRtcpMuxPolicy rtcpMuxPolicy;
    sequence<RTCCertificate> certificates;
    [EnforceRange] octet iceCandidatePoolSize = 0;
  };