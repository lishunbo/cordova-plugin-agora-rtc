package com.agora.cordova.plugin.webrtc.services;

import android.util.Log;

import com.agora.cordova.plugin.webrtc.Action;
import com.agora.cordova.plugin.webrtc.models.RTCConfiguration;
import com.agora.cordova.plugin.webrtc.models.RTCIceServer;
import com.agora.cordova.plugin.webrtc.models.enums.RTCIceCredentialType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RTCStats;
import org.webrtc.RTCStatsCollectorCallback;
import org.webrtc.RTCStatsReport;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoTrack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


public class RTCPeerConnection {
    static final String TAG = RTCPeerConnection.class.getCanonicalName();

    Supervisor supervisor;
    String pc_id;

    RTCConfiguration config;
    PeerConnection peerConnection;

    public interface Supervisor {
        void onAddTrack(String id, RtpReceiver rtpReceiver, MediaStream[] mediaStreams, String usage);

        void onAddStream(String id, MediaStream stream, String usage);

        void onRemoveStream(String id, MediaStream mediaStream, String usage);

        void onDataChannel(String id, DataChannel dataChannel, String usage);

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
        this.config = config;
    }

    public String getPc_id() {
        return pc_id;
    }

    public void createInstance(MessageHandler handler) {
        LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
        for (RTCIceServer iceServer : config.iceServers) {
            if (iceServer.urls == null || iceServer.urls.length == 0){
                continue;
            }
            PeerConnection.IceServer.Builder builder = PeerConnection.IceServer.builder(Arrays.asList(iceServer.urls));
            if (iceServer.username != null ){
                builder.setUsername(iceServer.username);
            }
            if (iceServer.credential!= null && (iceServer.credentialType == null || iceServer.credentialType == RTCIceCredentialType.password)){
                builder.setPassword(((RTCIceServer.CredentialDetailStringImp) iceServer.credential).toString());
            }

            iceServers.add(builder.createIceServer());
        }

        //TODO
        peerConnection = PCFactory.factory().createPeerConnection(iceServers, new RTCObserver("createPeerConnection:" + pc_id) {

        });

//        RTCPeerConnection that = this;
//
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                that.getStats();
//            }
//        };
//        timer.scheduleAtFixedRate(task, 1000L, 5000L);
    }

    public void addTrack(VideoTrack videoTrack) {

        MediaStream mediaStream = PCFactory.factory().createLocalMediaStream("localMediaStream");
        mediaStream.addTrack(videoTrack);

        peerConnection.addStream(mediaStream);
    }

    public void createOffer(MessageHandler handler) {
        MediaConstraints mediaConstraints = new MediaConstraints();
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        peerConnection.createOffer(new RTCObserver("createOffer:" + pc_id) {
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
        peerConnection.setLocalDescription(new RTCObserver("setLocalDescription" + pc_id) {
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

    public void setRemoteDescription(MessageHandler handler, String type, String description) {
        peerConnection.setRemoteDescription(new RTCObserver("setRemoteDescription" + pc_id) {
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

    public void getStats(MessageHandler handler) {
        peerConnection.getStats(new StatsReport(handler));
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

        public RTCObserver(String u) {
            usage = u;
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.v(TAG, usage + " onSignalingChange " + signalingState.toString());
            supervisor.onObserveEvent(pc_id, Action.onSignalingStateChange, signalingState.toString().toLowerCase(), usage);
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.v(TAG, usage + " onIceConnectionChange " + iceConnectionState.toString());

            supervisor.onObserveEvent(pc_id, Action.onICEConnectionStateChange, iceConnectionState.toString().toLowerCase(), usage);
        }

        @Override
        public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
            Log.v(TAG, usage + " onConnectionChange " + newState.toString());

            supervisor.onObserveEvent(pc_id, Action.onConnectionStateChange, newState.toString().toLowerCase(), usage);
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {
            Log.v(TAG, usage + " onIceConnectionReceivingChange " + String.valueOf(b));
            supervisor.onObserveEvent(pc_id, Action.onIceConnectionReceivingChange, String.valueOf(b), usage);
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

            Log.v(TAG, usage + " onIceGatheringChange " + iceGatheringState.toString());
            if (iceGatheringState == PeerConnection.IceGatheringState.COMPLETE) {
                //send empty candidate if complete
                supervisor.onObserveEvent(pc_id, Action.onIceCandidate, "", usage);
            }
            supervisor.onObserveEvent(pc_id, Action.onIceGatheringChange, iceGatheringState.toString().toLowerCase(), usage);
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
//            client.onAddTrack(rtpReceiver.track().kind().toLowerCase());
            supervisor.onAddTrack(pc_id, rtpReceiver, mediaStreams, usage);
        }


        @Override
        public void onAddStream(MediaStream mediaStream) {

            Log.v(TAG, usage + " onAddStream " + mediaStream.videoTracks.size());

            supervisor.onAddStream(pc_id, mediaStream, usage);
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {

            Log.v(TAG, usage + " onRemoveStream ");
            supervisor.onRemoveStream(pc_id, mediaStream, usage);
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

            Log.v(TAG, usage + " onDataChannel ");
            supervisor.onDataChannel(pc_id, dataChannel, usage);
        }

        @Override
        public void onRenegotiationNeeded() {

            Log.v(TAG, usage + " onRenegotiationNeeded ");
            supervisor.onObserveEvent(pc_id, Action.onRenegotiationNeeded, "", usage);
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
