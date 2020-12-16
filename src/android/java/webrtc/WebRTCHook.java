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

import io.agora.rtcn.media.services.MediaStreamTrackWrapper;
import io.agora.rtcn.webrtc.enums.Action;
import io.agora.rtcn.webrtc.models.RTCConfiguration;
import io.agora.rtcn.webrtc.models.RTCOfferOptions;
import io.agora.rtcn.webrtc.services.PCFactory;
import io.agora.rtcn.webrtc.services.RTCPeerConnection;

import static org.apache.cordova.PluginResult.Status.OK;

public class WebRTCHook extends CordovaPlugin {
    static final String TAG = "WebRTCHook";

    Map<String, CallbackPCPeer> instances = new HashMap<>();

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
                case addTrack:
                    return addTrack(args, callbackContext);
                case getTransceivers:
                    return getTransceivers(args);
                case addTransceiver:
                    return addTransceiver(args);
                case createOffer:
                    return createOffer(args, callbackContext);
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
        String id = args.getString(0);

        RTCConfiguration cfg = null;
        if (args.length() > 1) {
            String json = args.get(1).toString();
            if (json.length() != 0) {
                cfg = RTCConfiguration.fromJson(json);
            }
        }
        if (cfg == null) {
            Log.e(TAG, "Invalid RTCConfiguration, using default");
            cfg = new RTCConfiguration();
        }

        RTCPeerConnection pc = new RTCPeerConnection(new SupervisorImp(), id, cfg);

        pc.createInstance(new MessageHandler());

        this.instances.put(id, new CallbackPCPeer().setCallbackContext(callbackContext).setPeerConnection(pc));

        PluginResult result = new PluginResult(OK);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
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
        peer.pc.addTrack(kind, wrapper);

        callbackContext.success(wrapper.toString());
        return true;
    }

    boolean addTransceiver(JSONArray args) {
        return false;
    }

    boolean getTransceivers(JSONArray args) {
        return false;
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
                });
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
        String id = args.getString(1);
        CallbackPCPeer peer = instances.get(id);
        assert peer != null;
        instances.remove(id);
        peer.context.success("dispose");
        peer.pc.dispose();

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
