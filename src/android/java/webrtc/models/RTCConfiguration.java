package io.agora.rtc.webrtc.models;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.webrtc.PeerConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.webrtc.PeerConnection.RtcpMuxPolicy.NEGOTIATE;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RTCConfiguration {
    static final String TAG = RTCConfiguration.class.getCanonicalName();

    public String bundlePolicy;
    public RTCCertificate[] certificates;
    public int iceCandidatePoolSize;
    public RTCIceServer[] iceServers;
    public String iceTransportPolicy;
    public String peerIdentity;
    public String rtcpMuxPolicy;
//    public String sdpSemantics;

    public static RTCConfiguration fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, RTCConfiguration.class);
        } catch (JsonProcessingException e) {
//            System.out.println(e.toString());
            Log.e(TAG, e.toString());
        }
        return null;
    }

    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            Log.e(TAG, e.toString());
        }

        return "{}";
    }

    public PeerConnection.RTCConfiguration toNative() {
        LinkedList<PeerConnection.IceServer> servers = new LinkedList<>();
        for (RTCIceServer server : iceServers) {
            if (server.urls == null || server.urls.length == 0) {
                continue;
            }
            PeerConnection.IceServer.Builder builder =
                    PeerConnection.IceServer.builder(Arrays.asList(server.urls));
            if (server.username != null) {
                builder.setUsername(server.username);
            }
            if (server.credential != null &&
                    (server.credentialType == null ||
                            server.credentialType.equals("password"))) {
                builder.setPassword(server.credential.toString());
            }

            servers.add(builder.createIceServer());
        }

        PeerConnection.RTCConfiguration configuration =
                new PeerConnection.RTCConfiguration(servers);
        if (bundlePolicy != null) {
            switch (bundlePolicy) {
                case "max-bundle":
                    configuration.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
                    break;
                case "max-compat":
                    configuration.bundlePolicy = PeerConnection.BundlePolicy.MAXCOMPAT;
                    break;
            }
        }

        if (iceCandidatePoolSize != 0) {
            configuration.iceCandidatePoolSize = iceCandidatePoolSize;
        }

        if (iceTransportPolicy != null && iceTransportPolicy.equals("relay")) {
            configuration.iceTransportsType = PeerConnection.IceTransportsType.RELAY;
        }

        if (rtcpMuxPolicy != null && rtcpMuxPolicy.equals("negotiate")) {
            configuration.rtcpMuxPolicy = NEGOTIATE;
        }

//        if (config.sdpSemantics != null && config.sdpSemantics.equals("unified-plan")) {
//            configuration.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;
//        }

        return configuration;
    }

    public static String RTCConfigurationToString(PeerConnection.RTCConfiguration configuration) {
        JSONObject obj = new JSONObject();
        try {
            String bundlePolicy = "balanced";
            if (configuration.bundlePolicy == PeerConnection.BundlePolicy.MAXBUNDLE) {
                bundlePolicy = "max-bundle";
            } else if (configuration.bundlePolicy == PeerConnection.BundlePolicy.MAXCOMPAT) {
                bundlePolicy = "max-compat";
            }
            obj.put("bundlePolicy", bundlePolicy);
            obj.put("iceCandidatePoolSize", configuration.iceCandidatePoolSize);

            JSONArray ia = new JSONArray();
            for (PeerConnection.IceServer iceServer : configuration.iceServers) {
                RTCIceServer iceServer1 = new RTCIceServer();
                iceServer1.urls = (String[]) iceServer.urls.toArray();
                iceServer1.username = iceServer.username;
                if (iceServer.password.length() > 0) {
                    iceServer1.credential = new RTCIceServer.CredentialDetailStringImp()
                            .setCredential(iceServer.password);
                }
                iceServer1.credentialType = "password";

                ia.put(iceServer1.toJSONObject());
            }

            obj.put("iceServers", ia);
            obj.put("iceTransportPolicy",
                    configuration.iceTransportsType == PeerConnection.IceTransportsType.RELAY ?
                            "relay" : "all");
            obj.put("rtcpMuxPolicy", configuration.rtcpMuxPolicy == NEGOTIATE ?
                    "negotiate" : "require");
            obj.put("sdpSemantics",
                    configuration.sdpSemantics == PeerConnection.SdpSemantics.UNIFIED_PLAN ?
                            "unified-plan" : "plan-b");
        } catch (Exception e) {
            Log.e(TAG, "RTCConfigurationToString failed:" + e.toString());
        }
        return obj.toString();
    }
}
