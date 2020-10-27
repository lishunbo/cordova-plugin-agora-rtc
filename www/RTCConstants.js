
module.exports = {
    RTCIceCredentialType: {
        PASSOWRD: "password"
    },
    RTCIceTransportPolicy: {
        RELAY: "relay",
        ALL: "all"
    },
    RTCBundlePolicy: {
        BALANCED: "balanced",
        MAX_COMPAT: "max-compat",
        MAX_BUNDLE: "max-bundle"
    },
    RTCRtcpMuxPolicy: {
        REQUIRE: "require"
    },
    RTCSignalingState: {
        STABLE: "stable",
        HAVE_LOCAL_OFFER: "have-local-offer",
        HAVE_REMOTE_OFFER: "have-remote-offer",
        HAVE_LOCAL_PRANSWER: "have-local-pranswer",
        HAVE_REMOTE_PRANSWER: "have-remote-pranswer",
        CLOSED: "closed"
    },
    RTCIceGatheringState: {
        NEW: "new",
        GATHERING: "gathering",
        COMPLETE: "complete"
    },
    RTCPeerConnectionState: {
        CLOSED: "closed",
        FAILED: "failed",
        DISCONNECTED: "disconnected",
        NEW: "new",
        CONNECTIONG: "connecting",
        CONNECTED: "connected"
    },
    RTCIceConnectionState: {
        CLOSED: "closed",
        FAILED: "failed",
        DISCONNECTED: "disconnected",
        NEW: "new",
        CHECKIING: "checking",
        COMPLETED: "completed",
        CONNECTED: "connected"
    }
}
