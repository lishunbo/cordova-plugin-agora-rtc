package com.agora.cordova.plugin.webrtc;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.agora.cordova.plugin.webrtc.models.MediaStreamConstraints;
import com.agora.cordova.plugin.webrtc.models.MediaStreamTrackWrapper;
import com.agora.cordova.plugin.webrtc.models.RTCConfiguration;
import com.agora.cordova.plugin.webrtc.models.RTCOfferOptions;
import com.agora.cordova.plugin.webrtc.services.MediaDevice;
import com.agora.cordova.plugin.webrtc.services.PCFactory;
import com.agora.cordova.plugin.webrtc.services.RTCPeerConnection;
import com.agora.cordova.plugin.webrtc.services.SettingsContentObserver;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.apache.cordova.PluginResult.Status.OK;

public class WebRTCService {
    private final static String TAG = WebRTCService.class.getCanonicalName();

    Activity mainActivity;
    CordovaInterface cordova;

    Map<String, CallbackPCPeer> instances;
    List<RTCPeerConnection> allPC = new LinkedList<>();

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

    public WebRTCService(Activity mainActivity, CordovaInterface cordova) {
        this.mainActivity = mainActivity;
        this.cordova = cordova;

        instances = new HashMap<>();

        MediaDevice.initialize(this.mainActivity, this.mainActivity.getApplicationContext());
        PCFactory.initializationOnce(this.mainActivity.getApplicationContext());
    }

    public boolean enumerateDevices(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                callbackContext.success(MediaDevice.enumerateDevices());
            }
        });
        return true;
    }

    public boolean getUserMedia(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        MediaStreamConstraints constraints = MediaStreamConstraints.fromJson(args.getJSONObject(0).toString());

        assert constraints != null;
        Log.v(TAG, "getUserMedia: " + constraints.toString());

        String summary = MediaDevice.getUserMedia(constraints);

        callbackContext.success(summary);
        return true;
    }

    public boolean stopMediaStreamTrack(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String trackId = args.getString(0);

        MediaStreamTrackWrapper wrapper = MediaStreamTrackWrapper.popMediaStreamTrackById(trackId);
        if (wrapper == null) {
            callbackContext.error("not found track");
            return false;
        }
        wrapper.close();

        callbackContext.success();
        return true;
    }

    public boolean createInstance(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        RTCConfiguration cfg = null;
        if (args.length() > 1) {
            String json = args.get(1).toString();
            if (json.length() != 0) {
                cfg = RTCConfiguration.fromJson(json);
            }
        }
        if (cfg == null) {
            Log.e(TAG, "Invalid RTCConfiguration config, using default");
            cfg = new RTCConfiguration();
        }

        RTCPeerConnection pc = new RTCPeerConnection(new SupervisorImp(), id, cfg);
        allPC.add(pc);

        pc.createInstance(new MessageHandler());

        //for eventCallback
        this.instances.put(id, new CallbackPCPeer().setCallbackContext(callbackContext).setPeerConnection(pc));

        PluginResult result = new PluginResult(OK);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
        return true;
    }

    public boolean addTrack(JSONArray args, final CallbackContext callbackContext) throws JSONException {
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
        peer.pc.addTrack(kind, wrapper.getTrack());

        callbackContext.success(wrapper.toString());
        return true;
    }

    public boolean addTransceiver(JSONArray args) {
        return false;
    }

    public boolean getTransceivers(JSONArray args) {
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

    public boolean setLocalDescription(JSONArray args, CallbackContext callbackContext) throws JSONException {
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

    public boolean setRemoteDescription(JSONArray args, final CallbackContext callbackContext) throws JSONException {
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

    public boolean addIceCandidate(JSONArray args, final CallbackContext callbackContext) throws JSONException {
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

    public boolean getStats(JSONArray args, final CallbackContext callbackContext) throws JSONException {
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

    public boolean setSenderParameter(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        String track = args.getString(1);
        String degradation = args.getString(2);
        int maxBitrate = args.getInt(3);
        int minBitrate = args.getInt(4);

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                peer.pc.setRtpSenderParameters(track, degradation, maxBitrate, minBitrate);
                callbackContext.success();
            }
        });

        return true;
    }

    public boolean removeTrack(JSONArray args, final CallbackContext callbackContext) throws Exception {
        String id = args.getString(0);
        String tid = args.getString(1);
        String kind = args.getString(2);

        MediaStreamTrackWrapper wrapper = MediaStreamTrackWrapper.popMediaStreamTrackById(tid);
        if (wrapper == null) {
            String err = "Cannot found cached MediaStreamTrack by id:" + tid;
            Log.e(TAG, err);
            callbackContext.error(err);
            return false;
        }

        CallbackPCPeer peer = instances.get(id);
        assert peer != null;
        peer.pc.removeTrack(kind, wrapper.getTrack());

        callbackContext.success(wrapper.toString());
        return true;
    }

    public boolean close(JSONArray args) {
        return false;
    }

    public void reset() {
        this.instances.clear();

        for (RTCPeerConnection pc :
                this.allPC) {
            pc.dispose();
        }

        this.allPC.clear();
        MediaDevice.reset();
    }

    public void onDestroy() {
        MediaDevice.unInitialize();
    }

    class SupervisorImp implements RTCPeerConnection.Supervisor {
        @Override
        public void onDisconnect(RTCPeerConnection pc) {
            CallbackPCPeer peer = instances.get(pc.getPc_id());
            if (peer != null) {
                peer.context.success("dispose");
                instances.remove(pc.getPc_id());
            }

            allPC.remove(pc);
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


    class MessageHandler implements RTCPeerConnection.MessageHandler {

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
