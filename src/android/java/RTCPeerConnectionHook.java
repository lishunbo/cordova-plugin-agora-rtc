package com.agora.cordova.plugin.webrtc;

// The native Toast API

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
// Cordova-required packages
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.webrtc.PeerConnectionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class RTCPeerConnectionHook extends CordovaPlugin {
    static final String TAG = RTCPeerConnectionHook.class.getCanonicalName();

    public static final String CAMERA = Manifest.permission.CAMERA;
    public static final String INTERNET = Manifest.permission.INTERNET;
    public static final String NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
    public static final String WIFI_STATE = Manifest.permission.ACCESS_WIFI_STATE;
    public static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String MODIFY_AUDIO_SETTINGS = Manifest.permission.MODIFY_AUDIO_SETTINGS;
    public static final String WAKE_LOCK = Manifest.permission.WAKE_LOCK;

    public static final int NECESSARY_PERM_CODE = 900;
    private static final String PERMISSION_DENIED_ERROR = "Permission_Denied";

    //TODO maybe should protected by RWlock
    Map<String, RTCPeerConnection> allConnections;

    String action;
    JSONArray args;
    CallbackContext callbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        allConnections = new HashMap<>();

        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(this.cordova.getActivity().getApplicationContext())
                .createInitializationOptions());
    }


    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {
        Log.v(TAG, "action:" + action + "\t" + "arguments:" + args.toString());
        // Verify that the user sent a 'show' action
        this.action = action;
        this.args = args;
        this.callbackContext = callbackContext;

        if (!(cordova.hasPermission(CAMERA) && cordova.hasPermission(INTERNET)
                && cordova.hasPermission(RECORD_AUDIO) && cordova.hasPermission(WAKE_LOCK))) {
            getNecessaryPermission(NECESSARY_PERM_CODE);
        } else {
            return continueWithPermissions(this.action, this.args, this.callbackContext);
        }
        return true;
    }

    private boolean continueWithPermissions(String action, JSONArray args,
                                            final CallbackContext callbackContext) {
        try {
            String id = args.getString(0);
            switch (action) {
                case "CreateInstance":
                    createInstance(id);
                    break;
                case "GetInstance":
                default:
                    callbackContext.error("RTCPeerConnection not implement action:" + action);
                    return false;
            }
            // Send a positive result to the callbackContext
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
            callbackContext.success(id);
            callbackContext.sendPluginResult(pluginResult);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }
        return true;
    }

    protected void getNecessaryPermission(int requestCode) {
        cordova.requestPermissions(this, requestCode,
                new String[]{CAMERA,
                        INTERNET,
                        RECORD_AUDIO,
                        WAKE_LOCK,
                });
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, PERMISSION_DENIED_ERROR));
                return;
            }
        }
        this.continueWithPermissions(this.action, this.args, this.callbackContext);
    }

    public class RTCPeerConnection {
        UUID id;

        public RTCPeerConnection(String id) {

            this.id = UUID.fromString(id);

        }
    }

    String createInstance(String id) {
        RTCPeerConnection pc = new RTCPeerConnection(id);
        allConnections.put(pc.id.toString(), pc);


        this.cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Intent intent = new Intent(cordova.getActivity().getApplicationContext(), WebRTCLocalActivity.class);

                cordova.getActivity().startActivity(intent);
                callbackContext.success(); // Thread-safe.
            }
        });

        return pc.id.toString();
    }

}
