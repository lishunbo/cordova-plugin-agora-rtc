package io.agora.rtcn.webrtc.services;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.RTCStats;
import org.webrtc.RTCStatsCollectorCallback;
import org.webrtc.RTCStatsReport;
import org.webrtc.RtpParameters;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpSender;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoTrack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import io.agora.rtcn.media.services.MediaStreamTrackWrapper;
import io.agora.rtcn.webrtc.enums.Action;
import io.agora.rtcn.webrtc.enums.RTCIceCredentialType;
import io.agora.rtcn.webrtc.models.RTCConfiguration;
import io.agora.rtcn.webrtc.models.RTCIceServer;
import io.agora.rtcn.webrtc.models.RTCOfferOptions;

import static org.webrtc.RtpParameters.DegradationPreference.BALANCED;
import static org.webrtc.RtpParameters.DegradationPreference.MAINTAIN_FRAMERATE;
import static org.webrtc.RtpParameters.DegradationPreference.MAINTAIN_RESOLUTION;


public class RTCPeerConnection {
    static final String TAG = RTCPeerConnection.class.getCanonicalName();

    Supervisor supervisor;
    String pc_id;

    RTCConfiguration config;
    PeerConnection peerConnection;
    //    MediaStream localStream;
    MediaStream remoteStream;
    PeerConnection.PeerConnectionState state;

    public interface Supervisor {
        void onDisconnect(RTCPeerConnection pc);

        void onObserveEvent(String id, Action action, String message, String usage);
    }

    public interface MessageHandler {
        void success();

        void success(String msg);

        void error(String msg);
    }

    public RTCPeerConnection(Supervisor supervisor, String pc_id, RTCConfiguration config) {
        this.supervisor = supervisor;
        this.pc_id = pc_id;
        Log.v(TAG, "DUALSTREAM pc_id" + pc_id);
        this.config = config;
    }

    public String getPc_id() {
        return pc_id;
    }

    public void createInstance(MessageHandler handler) {
        LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
        for (RTCIceServer iceServer : config.iceServers) {
            if (iceServer.urls == null || iceServer.urls.length == 0) {
                continue;
            }
            PeerConnection.IceServer.Builder builder = PeerConnection.IceServer.builder(Arrays.asList(iceServer.urls));
            if (iceServer.username != null) {
                builder.setUsername(iceServer.username);
            }
            if (iceServer.credential != null && (iceServer.credentialType == null || iceServer.credentialType == RTCIceCredentialType.password)) {
                builder.setPassword(((RTCIceServer.CredentialDetailStringImp) iceServer.credential).toString());
            }

            iceServers.add(builder.createIceServer());
        }

        //TODO
        peerConnection = PCFactory.factory().createPeerConnection(iceServers, new RTCObserver(this, "createPeerConnection:" + pc_id) {

        });
    }

    public void addTrack(String kind, MediaStreamTrackWrapper wrapper) {
        peerConnection.addTrack(wrapper.getTrack());
    }

    public void createOffer(MessageHandler handler, RTCOfferOptions options) {
        MediaConstraints mediaConstraints = new MediaConstraints();
        if (options != null) {
            if (options.iceRestart) {
                mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("IceRestart", "true"));
            }
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", String.valueOf(options.offerToReceiveAudio)));
            mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", String.valueOf(options.offerToReceiveVideo)));
            if (options.voiceActivityDetection) {
                mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("VoiceActivityDetection", "true"));
            }
        }
//        mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        peerConnection.createOffer(new RTCObserver(this, "createOffer:" + pc_id) {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                String offer = "";
                try {
                    JSONObject sdp = new JSONObject();
                    sdp.put("type", "offer");
                    sdp.put("sdp", sessionDescription.description);
                    offer = sdp.toString();
                    handler.success(offer);
                } catch (JSONException e) {
                    Log.e(TAG, "CreateOffer success buf to json string failed:" + e.toString());

                    handler.error(e.toString());
                }
            }
        }, mediaConstraints);
    }

    public void setLocalDescription(MessageHandler handler, String type, String description) {
        peerConnection.setLocalDescription(new RTCObserver(this, "setLocalDescription" + pc_id) {
            @Override
            public void onSetSuccess() {
                super.onSetSuccess();
                StringBuilder builder = new StringBuilder();
                builder.append("[");
                boolean first = true;
                for (RtpSender sender :
                        peerConnection.getSenders()) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("track", sender.track().kind());
                        ObjectMapper objectMapper = new ObjectMapper();

                        obj.put("parameter", objectMapper.writeValueAsString(sender.getParameters()));
                    } catch (Exception e) {
                        Log.e(TAG, "put parameter failed:" + e.toString());
                    }
                    if (first) {
                        first = false;
                    } else {
                        builder.append(",");
                    }
                    builder.append(obj.toString());
                }
                builder.append("]");
                Log.e(TAG, "getSenders:" + builder.toString());
                handler.success(builder.toString());

            }

            @Override
            public void onSetFailure(String s) {
                super.onSetFailure(s);
                handler.error(s);
            }
        }, new SessionDescription(SessionDescription.Type.fromCanonicalForm(type), description));
    }

    public void setRemoteDescription(MessageHandler handler, String type, String description) {
        peerConnection.setRemoteDescription(new RTCObserver(this, "setRemoteDescription" + pc_id + "desc: " + description) {
            @Override
            public void onSetSuccess() {
                super.onSetSuccess();
                handler.success();
            }

            @Override
            public void onSetFailure(String s) {
                super.onSetFailure(s);
                handler.error(s);
            }
        }, new SessionDescription(SessionDescription.Type.fromCanonicalForm(type), description));
    }

    public void addIceCandidate(MessageHandler handler, String candidate) {
        try {
            JSONObject obj = new JSONObject(candidate);
            Log.e(TAG, "Debug....addIceCandidate:" + candidate);
            Log.e(TAG, "Debug....sdpMid:" + obj.getString("sdpMid"));
            Log.e(TAG, "Debug....sdpMLineIndex:" + obj.getInt("sdpMLineIndex"));
            Log.e(TAG, "Debug....candidate:" + obj.getString("candidate"));
            peerConnection.addIceCandidate(new IceCandidate(obj.getString("sdpMid"), obj.getInt("sdpMLineIndex"), obj.getString("candidate")));
            handler.success();
        } catch (Exception e) {
            Log.e(TAG, "setLocalDescription exception:" + e.toString());
            handler.error(e.toString());
        }
    }

    public void removeTrack(String kind, MediaStreamTrackWrapper wrapper) {
        for (RtpSender sender :
                peerConnection.getSenders()) {
            if (sender.track().kind().equals(kind)) {
                Log.w(TAG, "remove track " + kind);
                peerConnection.removeTrack(sender);
                break;
            }
        }
    }

    public void replaceTrack(String kind, MediaStreamTrack track) {
        for (RtpSender sender :
                peerConnection.getSenders()) {
            if (sender.track().kind().equals(kind)) {
                sender.setTrack(track, false);
                break;
            }
        }
    }

    public void setRtpSenderParameters(String kind, String degradation, int maxBitrate, int minBitrate, double scaleDown) {
        for (RtpSender sender :
                peerConnection.getSenders()) {
            if (sender.track().kind().equals(kind)) {
                RtpParameters parameters = sender.getParameters();
                if (degradation.length() > 0) {
                    degradation = degradation.replace("-", "_");
                    Log.e(TAG, "degration:" + BALANCED + " " + MAINTAIN_FRAMERATE + " " + MAINTAIN_RESOLUTION + " " + degradation);
                    parameters.degradationPreference = RtpParameters.DegradationPreference.valueOf(degradation.toUpperCase());
                }
                if (maxBitrate > 0) {
                    parameters.encodings.get(0).maxBitrateBps = maxBitrate;
                }
                if (minBitrate > 0) {
                    parameters.encodings.get(0).minBitrateBps = minBitrate;
                }
                if (scaleDown > 0) {
                    parameters.encodings.get(0).scaleResolutionDownBy = scaleDown;
                }
                StringBuilder l = new StringBuilder();
                l.append("setRtpSenderParameter ").append(parameters.degradationPreference).append(" ").
                        append(parameters.encodings.get(0).maxBitrateBps).append(" ").
                        append(parameters.encodings.get(0).minBitrateBps).append(" ").
                        append(parameters.encodings.get(0).scaleResolutionDownBy);
                Log.v(TAG, l.toString());
                sender.setParameters(parameters);
                break;
            }
        }
    }

    void closeStream() {
        for (RtpSender sender :
                peerConnection.getSenders()) {
            peerConnection.removeTrack(sender);
        }
        if (remoteStream != null) {
            peerConnection.removeStream(remoteStream);
            remoteStream = null;
        }
    }

    public void getStats(MessageHandler handler) {
        peerConnection.getStats(new StatsReport(handler));
    }

    public void dispose() {
        if (supervisor == null) {
            return;
        }
        supervisor = null;

        MediaStreamTrackWrapper.removeMediaStreamTrackByPCId(pc_id);

        pc_id = null;
        closeStream();
        if (state == PeerConnection.PeerConnectionState.CONNECTED) {
            peerConnection.close();
        }
        config = null;
        peerConnection = null;
    }

    public class StatsReport implements RTCStatsCollectorCallback {
        private MessageHandler _handler;

        public StatsReport(MessageHandler handler) {
            _handler = handler;
        }

        @Override
        public void onStatsDelivered(RTCStatsReport rtcStatsReport) {
            StringBuilder report = new StringBuilder();
            boolean bFirst = true;

            for (Map.Entry<String, RTCStats> stat :
                    rtcStatsReport.getStatsMap().entrySet()) {
                if (!bFirst) {
                    report.append(",");
                } else {
                    bFirst = false;
                    report.append("[");
                }
//            report.append("\"").append(stat.getKey()).append("\":");
                report.append("{\"timestamp\":").append((long) stat.getValue().getTimestampUs() / 1000)
                        .append(",\"type\":\"").append(stat.getValue().getType())
                        .append("\",\"id\":\"").append(stat.getValue().getId()).append("\"");

                Iterator it = stat.getValue().getMembers().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry) it.next();
                    report.append(", \"").append((String) entry.getKey()).append("\"").append(": ");
                    appendValue(report, entry.getValue());
                }
                report.append("}");
            }
            report.append("]");

            _handler.success(report.toString());
        }

        private void appendValue(StringBuilder builder, Object value) {
            if (value instanceof Object[]) {
                Object[] arrayValue = (Object[]) value;
                builder.append('[');

                for (int i = 0; i < arrayValue.length; ++i) {
                    if (i != 0) {
                        builder.append(", ");
                    }

                    appendValue(builder, arrayValue[i]);
                }

                builder.append(']');
            } else if (value instanceof String) {
                builder.append('"').append(value).append('"');
            } else {
                builder.append(value);
            }
        }
    }


    public class RTCObserver implements SdpObserver, PeerConnection.Observer {
        private String usage;
        RTCPeerConnection pc;

        public RTCObserver(RTCPeerConnection pc, String u) {
            usage = u;
            this.pc = pc;
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.v(TAG, usage + " onSignalingChange " + signalingState.toString());
            if (supervisor != null) {
                supervisor.onObserveEvent(pc_id, Action.onSignalingStateChange, signalingState.toString().toLowerCase(), usage);
            }
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.v(TAG, usage + " onIceConnectionChange " + iceConnectionState.toString());

            if (supervisor != null) {
                supervisor.onObserveEvent(pc_id, Action.onICEConnectionStateChange, iceConnectionState.toString().toLowerCase(), usage);
            }
        }

        @Override
        public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
            Log.v(TAG, usage + " onConnectionChange " + newState.toString());
            Log.v(TAG, "DUALSTREAM pc_id" + pc_id + " " + newState.toString());
            state = newState;
            if (supervisor != null) {
                supervisor.onObserveEvent(pc_id, Action.onConnectionStateChange, newState.toString().toLowerCase(), usage);
            }
            if (newState == PeerConnection.PeerConnectionState.CLOSED ||
                    newState == PeerConnection.PeerConnectionState.FAILED) {

                if (supervisor != null) {
                    supervisor.onDisconnect(pc);
                }
                dispose();
            }
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {
            Log.v(TAG, usage + " onIceConnectionReceivingChange " + String.valueOf(b));
            if (supervisor != null) {
                supervisor.onObserveEvent(pc_id, Action.onIceConnectionReceivingChange, String.valueOf(b), usage);
            }
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

            Log.v(TAG, usage + " onIceGatheringChange " + iceGatheringState.toString());
            if (iceGatheringState == PeerConnection.IceGatheringState.COMPLETE) {
                //send empty candidate if complete
                supervisor.onObserveEvent(pc_id, Action.onIceCandidate, "", usage);
            }
            if (supervisor != null) {
                supervisor.onObserveEvent(pc_id, Action.onIceGatheringChange, iceGatheringState.toString().toLowerCase(), usage);
            }
        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            Log.v(TAG, usage + " onIceCandidate " + iceCandidate.toString());
            Log.v(TAG, usage + " onIceCandidateSDP " + iceCandidate.sdp);
            JSONObject obj = new JSONObject();
            try {
                obj.put("sdpMid", iceCandidate.sdpMid);
                obj.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
                obj.put("candidate", iceCandidate.sdp);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            supervisor.onObserveEvent(pc_id, Action.onIceCandidate, obj.toString(), usage);
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

            Log.v(TAG, usage + " onIceCandidatesRemoved ");

            JSONArray array = new JSONArray();
            for (IceCandidate iceCandidate : iceCandidates) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("sdpMid", iceCandidate.sdpMid);
                    obj.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
                    obj.put("candidate", iceCandidate.sdp);
                    array.put(obj);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }

            supervisor.onObserveEvent(pc_id, Action.onIceCandidatesRemoved, array.toString(), usage);
        }

        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
            Log.v(TAG, usage + " onAddTrack " + rtpReceiver.track().kind());

            MediaStreamTrackWrapper wrapper = MediaStreamTrackWrapper.cacheMediaStreamTrackWrapper(pc_id, rtpReceiver);

            supervisor.onObserveEvent(pc_id, Action.onAddTrack, wrapper.toString(), usage);
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {

            Log.v(TAG, usage + " onAddStream " + mediaStream.videoTracks.size());
            if (remoteStream != null) {
                peerConnection.removeStream(remoteStream);
            }
            remoteStream = mediaStream;
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {

            Log.v(TAG, usage + " onRemoveStream ");
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

            Log.v(TAG, usage + " onDataChannel ");
        }

        @Override
        public void onRenegotiationNeeded() {

            Log.v(TAG, usage + " onRenegotiationNeeded ");
            if (supervisor != null) {
                supervisor.onObserveEvent(pc_id, Action.onRenegotiationNeeded, "", usage);
            }
        }

        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {

            Log.v(TAG, usage + " onCreateSuccess" + sessionDescription.description.toString());
        }

        @Override
        public void onSetSuccess() {

            Log.v(TAG, usage + " onSetSuccess");
        }

        @Override
        public void onCreateFailure(String s) {

            Log.v(TAG, usage + " onCreateFailure" + s.toString());
        }

        @Override
        public void onSetFailure(String s) {

            Log.v(TAG, usage + " onSetFailure" + s.toString());
        }
    }
}
