package com.agora.cordova.plugin.webrtc;

// The native Toast API

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.agora.cordova.plugin.webrtc.models.MediaStreamConstraints;
import com.agora.cordova.plugin.webrtc.models.RTCConfiguration;
import com.agora.cordova.plugin.webrtc.models.SessionDescription;
import com.agora.cordova.plugin.webrtc.services.PCFactory;
import com.agora.cordova.plugin.webrtc.services.RTCPeerConnection;
import com.agora.cordova.plugin.webrtc.utils.MessageBus;
import com.agora.demo.four.R;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static org.apache.cordova.PluginResult.Status.NO_RESULT;
import static org.apache.cordova.PluginResult.Status.OK;

// Cordova-required packages


public class Hook extends CordovaPlugin {
    static final String TAG = Hook.class.getCanonicalName();

    public static final String CAMERA = Manifest.permission.CAMERA;
    public static final String INTERNET = Manifest.permission.INTERNET;
    public static final String NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
    public static final String WIFI_STATE = Manifest.permission.ACCESS_WIFI_STATE;
    public static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String MODIFY_AUDIO_SETTINGS = Manifest.permission.MODIFY_AUDIO_SETTINGS;
    public static final String WAKE_LOCK = Manifest.permission.WAKE_LOCK;
    public static final String ALERT_WINDOW = Manifest.permission.SYSTEM_ALERT_WINDOW;

    public static final int NECESSARY_PERM_CODE = 900;
    private static final String PERMISSION_DENIED_ERROR = "Permission_Denied";

    //TODO maybe should protected by RWlock
    Map<String, RTCPeerConnection> allConnections;

    MessageBus server;
    String hook_id = UUID.randomUUID().toString();
    String webrtc_view_id;

    Wrapper wrapper = null;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.e(TAG, "=== Realy in initialize");

        allConnections = new HashMap<>();


//        cordova.getThreadPool().execute(new Runnable() {
//            @Override
//            public void run() {
        server = new MessageBus(new InetSocketAddress("127.0.0.1", 9999));
        server.setReuseAddr(true);
        server.setTcpNoDelay(true);
        server.start();
//            }
//        });

//        cordova.getActivity().runOnUiThread(()->{
        if (!(cordova.hasPermission(CAMERA) && cordova.hasPermission(INTERNET)
                && cordova.hasPermission(RECORD_AUDIO) && cordova.hasPermission(WAKE_LOCK) && cordova.hasPermission(ALERT_WINDOW))) {
            getNecessaryPermission();
        }


        webrtc_view_id = cordova.getActivity().getString(R.string.webrtc_view_id);

//        this.cordova.getThreadPool().execute(() -> {
        Intent intent = new Intent(cordova.getActivity().getApplicationContext(), WebRTCViewActivity.class);

        intent.putExtra(cordova.getActivity().getString(R.string.hook_id), hook_id);

        cordova.getActivity().startActivity(intent);
//        });

//        });

        URI uri = null;
        try {
            uri = new URI(cordova.getActivity().getString(R.string.internalws) + hook_id);
        } catch (Exception e) {
            Log.e(TAG, "Panic error, cannot parser internal communicate ws url:" + e.toString());

        }
        wrapper = new Wrapper(uri, cordova, hook_id, webrtc_view_id);
        try {
            wrapper.connectBlocking();
        } catch (Exception e) {
            Log.e(TAG, "Panic error, cannot connect internal communicate ws server:" + e.toString());
        }
        Log.e(TAG, "connected internal communicate ws server:");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            server.stop(300);
        } catch (Exception e) {
            Log.e(TAG, "MessageBus stop exception:" + e.toString());
        }
    }

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {


        return continueWithPermissions(action, args, callbackContext);
    }

    //Main hook point for Hook
    private boolean continueWithPermissions(String action, JSONArray args,
                                            final CallbackContext callbackContext) {
        if (wrapper != null && !wrapper.isOpen()) {
            Log.e(TAG, "Panic error, not connected to internal communicate ws server");

            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION));
            return false;
        }

        try {
            Log.e(TAG, "actioin:" + Action.valueOf(action));
            switch (Action.valueOf(action)) {
                //Keep Instance context for callback
                case createInstance:
                    return createInstance(args, callbackContext);
                //PeerConnection sub functions
                case createOffer:
                    return createOffer(args, callbackContext);
                case addTrack:
                    return addTrack(args, callbackContext);
                case setLocalDescription:
                    return setLocalDescription(args, callbackContext);
                case setRemoteDescription:
                    return setRemoteDescription(args, callbackContext);
                case addIceCandidate:
                    return addIceCandidate(args, callbackContext);
                case close:
                    return close(args);
                case removeTrack:
                    return removeTrack(args);
                case getTransceivers:
                    return getTransceivers(args);
                case addTransceiver:
                    return addTransceiver(args);
                case getStats:
                    return getStats(args, callbackContext);
                //MediaDevice functions
                case getUserMedia:
                    return getUserMedia(args, callbackContext);
                default:
                    Log.e(TAG, "Not implement action of :" + action);
                    callbackContext.error("RTCPeerConnection not implement action:" + action);
                    return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "continueWithPermissions exception:" + e.toString());
            return false;
        }
    }

    private boolean getUserMedia(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        MediaStreamConstraints constraints = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            HashMap result = mapper.readValue(args.getJSONObject(1).toString(), HashMap.class);
            constraints = new MediaStreamConstraints(result);
        } catch (Exception e) {
            Log.e(TAG, "fault, cannot unmarshal MediaStreamConstraints:" + e.toString() + args.toString());
        }
        if (constraints == null) {
            Log.e(TAG, "fault, getUserMedia no sdp data");
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION));
            return false;
        }

        wrapper.getUserMedia(id, callbackContext, constraints);

        return true;
    }

    private boolean getStats(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        wrapper.getStats(id, callbackContext);
        return true;
    }

    private boolean addTrack(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        wrapper.addTrack(id, callbackContext);
        return true;
    }

    private boolean setLocalDescription(JSONArray args, CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        JSONObject sdp = new JSONObject();
        sdp.put("type", args.getString(1));
        sdp.put("description", args.getString(2));
        wrapper.setLocalDescription(id, sdp.toString(), callbackContext);
        return true;
    }

    private boolean addTransceiver(JSONArray args) {
        return false;
    }

    private boolean getTransceivers(JSONArray args) {
        return false;
    }

    private boolean removeTrack(JSONArray args) {
        return false;
    }

    private boolean close(JSONArray args) {
        return false;
    }

    boolean createInstance(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
//        Log.d(TAG, args.toString());

        Log.e(TAG, "log for createInstance object:" + wrapper.toString());
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

        wrapper.createInstance(id, callbackContext, cfg);

//        Log.e(TAG, "log for wrapper object:" + wrapper.toString());

        PluginResult result = new PluginResult(OK);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
        return true;
    }

    boolean setRemoteDescription(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        JSONObject sdp = new JSONObject();
        try {
            sdp.put("type", args.getString(1));
            sdp.put("description", args.getString(2));
        } catch (Exception e) {
            Log.e(TAG, "fault, cannot unmarshal SessionDescription:" + e.toString() + args.toString());
        }
        if (sdp == null) {
            Log.e(TAG, "fault, setRemoteDescription no sdp data");
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION));
            return false;
        }
        wrapper.setRemoteDescription(id, callbackContext, sdp.toString());

        PluginResult result = new PluginResult(NO_RESULT);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);

        return true;
    }

    private boolean addIceCandidate(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        wrapper.addIceCandidate(id, args.getJSONObject(1).toString(), callbackContext);

        PluginResult result = new PluginResult(NO_RESULT);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
        return true;
    }

    boolean createOffer(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        wrapper.createOffer(id, callbackContext);

        PluginResult result = new PluginResult(NO_RESULT);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);

        return true;
    }

    protected void getNecessaryPermission() {
        Log.v(TAG, "getNecessaryPermission:");
        cordova.requestPermissions(this, Hook.NECESSARY_PERM_CODE,
                new String[]{CAMERA,
                        INTERNET,
                        RECORD_AUDIO,
                        WAKE_LOCK,
                        ALERT_WINDOW,
                });
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                Log.e(TAG, "Some permission has been denied");
                return;
            }
        }
    }

}