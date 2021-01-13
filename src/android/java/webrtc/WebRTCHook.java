package io.agora.rtcn.webrtc;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.agora.rtcn.WebsocketClient;
import io.agora.rtcn.media.services.MediaStreamTrackWrapper;
import io.agora.rtcn.webrtc.enums.Action;
import io.agora.rtcn.webrtc.models.RTCConfiguration;
import io.agora.rtcn.webrtc.models.RTCDataChannelInit;
import io.agora.rtcn.webrtc.models.RTCIceServer;
import io.agora.rtcn.webrtc.models.RTCOfferOptions;
import io.agora.rtcn.webrtc.models.RtpTransceiverInit;
import io.agora.rtcn.webrtc.services.PCFactory;
import io.agora.rtcn.webrtc.services.RTCPeerConnection;

import static org.apache.cordova.PluginResult.Status.OK;

public class WebRTCHook extends CordovaPlugin {
    static final String TAG = "WebRTCHook";

    Map<String, CallbackPCPeer> instances = new HashMap<>();

    Map<String, WebsocketClient> wsClients = new HashMap<>();

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        PCFactory.initializationOnce(cordova.getActivity().getApplicationContext());
    }

    @Override
    public void onReset() {
        super.onReset();

        for (Map.Entry<String, CallbackPCPeer> peer : instances.entrySet()) {
            peer.getValue().context.success();
            peer.getValue().pc.dispose();
        }

        this.instances.clear();
    }

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {
        try {
            if (Config.logInternalMessage && !action.equals("getStats") && !action.equals("enumerateDevices")) {
                Log.e(TAG, "action:" + Action.valueOf(action));
            }
            switch (Action.valueOf(action)) {
                case createPC:
                    return createPC(args, callbackContext);
                case setConfiguration:
                    return setConfiguration(args, callbackContext);
                case createOffer:
                    return createOffer(args, callbackContext);
                case createAnswer:
                    return createAnswer(args, callbackContext);
                case createDataChannel:
                    return createDataChannel(args, callbackContext);
                case addTrack:
                    return addTrack(args, callbackContext);
                case getTransceivers:
                    return getTransceivers(args, callbackContext);
                case addTransceiver:
                    return addTransceiver(args, callbackContext);
                case setLocalDescription:
                    return setLocalDescription(args, callbackContext);
                case setRemoteDescription:
                    return setRemoteDescription(args, callbackContext);
                case addIceCandidate:
                    return addIceCandidate(args, callbackContext);
                case close:
                    return close(args, callbackContext);
                case removeTrack:
                    return removeTrack(args, callbackContext);
                case replaceTrack:
                    return replaceTrack(args, callbackContext);
                case getStats:
                    return getStats(args, callbackContext);
                case setSenderParameter:
                    return setSenderParameter(args, callbackContext);

                case closeDC:
                    return closeDC(args, callbackContext);
                case sendDC:
                    return sendDC(args, callbackContext);

                case createWS:
                    return createWS(args, callbackContext);
                case closeWS:
                    return closeWS(args, callbackContext);
                case sendWS:
                    return sendWS(args, callbackContext);
                default:
                    Log.e(TAG, "Not implement action of: " + action);
                    callbackContext.error("Not implement action:" + action);
                    return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "execute action " + action + " exception:" + e.toString());
            callbackContext.error("execute action " + action + " exception:" + e.toString());
            return false;
        }
    }

    static class CallbackPCPeer {
        CallbackContext context;
        RTCPeerConnection pc;

        CallbackPCPeer setCallbackContext(CallbackContext context) {
            this.context = context;
            return this;
        }

        CallbackPCPeer setPeerConnection(RTCPeerConnection pc) {
            this.pc = pc;
            return this;
        }
    }

    boolean createPC(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        PluginResult result = new PluginResult(OK);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);

        String id = args.getString(0);

        RTCConfiguration cfg = RTCConfiguration.fromJson(args.getString(1));
        if (cfg == null) {
            Log.e(TAG, "Invalid RTCConfiguration, using default");
            cfg = new RTCConfiguration();
        }
        if (cfg.iceServers == null) {
            cfg.iceServers = new RTCIceServer[]{};
        }

        RTCPeerConnection pc = new RTCPeerConnection(new SupervisorImp(), id, cfg);

        this.instances.put(id, new CallbackPCPeer()
                .setCallbackContext(callbackContext).setPeerConnection(pc));

        pc.createInstance();

        return true;
    }

    boolean setConfiguration(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        RTCConfiguration config = RTCConfiguration.fromJson(args.getString(1));
        assert config != null;

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;
        peer.pc.setConfiguration(new MessageHandler() {
            @Override
            public void success(String msg) {
                callbackContext.success(msg);
            }

            @Override
            public void error(String msg) {
                callbackContext.error(msg);
            }
        }, config);

        return true;
    }

    boolean createOffer(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        RTCOfferOptions options = RTCOfferOptions.fromJson(args.getString(1));

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;
        peer.pc.createOffer(new MessageHandler() {
            @Override
            public void success(String msg) {
                callbackContext.success(msg);
            }

            @Override
            public void error(String msg) {
                callbackContext.error(msg);
            }
        }, options);

        return true;
    }

    boolean createAnswer(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        RTCOfferOptions options = RTCOfferOptions.fromJson(args.getString(1));

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;

        peer.pc.createAnswer(new MessageHandler() {
            @Override
            public void success(String msg) {
                callbackContext.success(msg);
            }

            @Override
            public void error(String msg) {
                callbackContext.error(msg);
            }
        }, options);

        return true;
    }

    boolean createDataChannel(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        String label = args.getString(1);
        RTCDataChannelInit init = (RTCDataChannelInit) args.get(2);

        Log.d(TAG, "createDataChannel arguments" + init.toString());

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;
        peer.pc.createDataChannel(new MessageHandler() {
            @Override
            public void success(String msg) {
                callbackContext.success(msg);
            }

            @Override
            public void error(String msg) {
                callbackContext.error(msg);
            }
        }, label, init);

        return true;
    }

    boolean addTrack(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        String tid = args.getString(1);
        String kind = args.getString(2);

        MediaStreamTrackWrapper wrapper = MediaStreamTrackWrapper.getMediaStreamTrackById(tid);
        if (wrapper == null) {
            String err = "Cannot found cached MediaStreamTrack by id:" + tid;
            Log.e(TAG, err);
            callbackContext.error(err);
            return false;
        }

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;
        peer.pc.addTrack(new MessageHandler() {
            @Override
            public void success(String msg) {
                callbackContext.success(msg);
            }

            @Override
            public void error(String msg) {
                callbackContext.error(msg);
            }
        }, kind, wrapper);

        return true;
    }

    boolean addTransceiver(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        boolean mediaType = args.getBoolean(1);
        RtpTransceiverInit config = RtpTransceiverInit.fromJson(args.getString(3));
        assert config != null;

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;
        MessageHandler handler = new MessageHandler() {
            @Override
            public void success(String msg) {
                callbackContext.success(msg);
            }

            @Override
            public void success() {
                callbackContext.success();
            }

            @Override
            public void error(String msg) {
                callbackContext.error(msg);
            }
        };
        if (mediaType) {
            peer.pc.addTransceiver(handler, args.getString(2), config);
        } else {
            peer.pc.addTransceiver(handler,
                    MediaStreamTrackWrapper.getMediaStreamTrackById(args.getString(2)),
                    config);
        }

        return true;
    }

    boolean getTransceivers(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;
        peer.pc.getTransceiver(new MessageHandler() {
            @Override
            public void success(String msg) {
                callbackContext.success(msg);
            }

            @Override
            public void success() {
                callbackContext.success();
            }

            @Override
            public void error(String msg) {
                callbackContext.error(msg);
            }
        });

        return true;
    }

    boolean setLocalDescription(JSONArray args, CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        CallbackPCPeer peer = instances.get(id);
        assert peer != null;
        peer.pc.setLocalDescription(new MessageHandler() {
            @Override
            public void success(String msg) {
                callbackContext.success(msg);
            }

            @Override
            public void success() {
                callbackContext.success();
            }

            @Override
            public void error(String msg) {
                callbackContext.error(msg);
            }
        }, args.getString(1), args.getString(2));

        return true;
    }

    boolean setRemoteDescription(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        CallbackPCPeer peer = instances.get(id);
        assert peer != null;
        peer.pc.setRemoteDescription(new MessageHandler() {
            @Override
            public void success() {
                callbackContext.success();
            }

            @Override
            public void error(String msg) {
                callbackContext.error(msg);
            }
        }, args.getString(1), args.getString(2));
        return true;
    }

    boolean addIceCandidate(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;
        peer.pc.addIceCandidate(new MessageHandler() {
            @Override
            public void success() {
                callbackContext.success();
            }

            @Override
            public void error(String msg) {
                callbackContext.error(msg);
            }
        }, args.getString(1));
        return true;
    }

    boolean getStats(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        String tid = args.getString(1);

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                peer.pc.getStats(new MessageHandler() {
                    @Override
                    public void success(String msg) {
                        callbackContext.success(msg);
                    }
                }, MediaStreamTrackWrapper.getMediaStreamTrackById(tid));
            }
        });

        return true;
    }

    boolean setSenderParameter(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        String track = args.getString(1);
        String degradation = args.getString(2);
        int maxBitrate = args.getInt(3);
        int minBitrate = args.getInt(4);
        double scaleDown = args.getDouble(5);

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                peer.pc.setRtpSenderParameters(track, degradation, maxBitrate, minBitrate, scaleDown);
                callbackContext.success();
            }
        });

        return true;
    }

    boolean removeTrack(JSONArray args, final CallbackContext callbackContext) throws Exception {
        String id = args.getString(0);
        String tid = args.getString(1);
        String kind = args.getString(2);

        MediaStreamTrackWrapper wrapper = MediaStreamTrackWrapper.popMediaStreamTrackById(tid);
        CallbackPCPeer peer = instances.get(id);
        assert peer != null;
        peer.pc.removeTrack(kind, wrapper);
        callbackContext.success();
        return true;
    }

    boolean replaceTrack(JSONArray args, final CallbackContext callbackContext) throws Exception {
        String id = args.getString(1);
        String tid = args.getString(2);
        String kind = args.getString(3);

        MediaStreamTrackWrapper wrapper = MediaStreamTrackWrapper.getMediaStreamTrackById(tid);
        if (wrapper == null) {
            String err = "Cannot found cached MediaStreamTrack by id:" + tid;
            Log.e(TAG, err);
            callbackContext.error(err);
            return false;
        }

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;
        peer.pc.replaceTrack(kind, wrapper.getTrack());

        callbackContext.success(wrapper.toString());
        return true;
    }

    boolean close(JSONArray args, final CallbackContext callbackContext) throws Exception {
        String id = args.getString(0);
        CallbackPCPeer peer = instances.get(id);
        assert peer != null;
        instances.remove(id);
        peer.context.success("dispose");
        peer.pc.dispose();

        callbackContext.success();
        return true;
    }

    boolean closeDC(JSONArray args, final CallbackContext callbackContext) throws Exception {
        String id = args.getString(0);
        int dcid = args.getInt(1);

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;

        peer.pc.closeDC(dcid);

        callbackContext.success();
        return true;
    }

    boolean sendDC(JSONArray args, final CallbackContext callbackContext) throws Exception {
        String id = args.getString(0);
        int dcid = args.getInt(1);
        boolean binary = args.getBoolean(2);
        String msg = args.getString(3);

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;

        peer.pc.sendDC(dcid, binary, msg);

        callbackContext.success();
        return true;
    }

    boolean createWS(JSONArray args, final CallbackContext callbackContext) throws Exception {
        String id = args.getString(0);
        String uri = args.getString(1);

        WebsocketClient client = new WebsocketClient(
                uri, cordova.getContext().getResources(), callbackContext);

        wsClients.put(id, client);

        PluginResult result = new PluginResult(OK);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
        return true;
    }

    boolean sendWS(JSONArray args, final CallbackContext callbackContext) throws Exception {
        String id = args.getString(0);
        String data = args.getString(1);

        WebsocketClient client = wsClients.get(id);
        if (client != null) {
            client.send(data);
        }

        callbackContext.success();
        return true;
    }

    boolean closeWS(JSONArray args, final CallbackContext callbackContext) throws Exception {
        String id = args.getString(0);

        WebsocketClient client = wsClients.get(id);
        wsClients.remove(id);

        if (client != null) {
            client.close();
        }

        callbackContext.success();
        return true;
    }

    class SupervisorImp implements RTCPeerConnection.Supervisor {
        @Override
        public void onDisconnect(RTCPeerConnection pc) {
            CallbackPCPeer peer = instances.get(pc.getPc_id());
            if (peer != null) {
                peer.context.success("dispose");
                instances.remove(pc.getPc_id());
            }
        }

        @Override
        public void onObserveEvent(String id, Action action, String message, String usage) {
            CallbackPCPeer peer = instances.get(id);
            if (peer == null) {
                return;
            }

            Log.v(TAG, usage + " onObserveEvent " + action + " " + message);

            JSONObject obj = new JSONObject();
            try {
                obj.put("event", action.toString());
                obj.put("id", id);
                obj.put("payload", message);
            } catch (Exception e) {
                Log.e(TAG, "event exception:" + action);
            }
            PluginResult result = new PluginResult(OK, obj);
            result.setKeepCallback(true);
            peer.context.sendPluginResult(result);
        }
    }

    static class MessageHandler implements RTCPeerConnection.MessageHandler {
        @Override
        public void success() {
        }

        @Override
        public void success(String msg) {
        }

        @Override
        public void error(String msg) {
        }
    }
}
