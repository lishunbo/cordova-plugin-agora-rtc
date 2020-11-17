package com.agora.cordova.plugin.webrtc;

// The native Toast API

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
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
    public static final int OVERLAY_PERMISSION_CODE = 1000;
    private static final String PERMISSION_DENIED_ERROR = "Permission_Denied";

    WebRTCService _service;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.v(TAG, "Hook initializing");

        _service = new WebRTCService(cordova.getActivity());

        if (!(cordova.hasPermission(CAMERA) && cordova.hasPermission(INTERNET)
                && cordova.hasPermission(RECORD_AUDIO) && cordova.hasPermission(WAKE_LOCK) && cordova.hasPermission(ALERT_WINDOW))) {
            getNecessaryPermission();
        }

        if (Build.VERSION.SDK_INT >= 23 && (!Settings.canDrawOverlays(cordova.getActivity()))) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + cordova.getActivity().getPackageName()));

            cordova.startActivityForResult(new CordovaPlugin() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                    super.onActivityResult(requestCode, resultCode, intent);

                    Log.v(TAG, "onActivityResult " + requestCode + " " + resultCode);
                }
            }, intent, OVERLAY_PERMISSION_CODE);
        }

        Log.v(TAG, "Hook initialized");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
    }

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {
        try {
            if (Config.logInternalMessage && !action.equals("getStats")) {
                Log.e(TAG, "actioin:" + Action.valueOf(action));
            }
            switch (Action.valueOf(action)) {
                case getUserMedia:
                    return _service.getUserMedia(args, callbackContext);
                //Keep Instance context for callback
                case createInstance:
                    return _service.createInstance(args, callbackContext);
                //PeerConnection sub functions
                case addTrack:
                    return _service.addTrack(args, callbackContext);
                case getTransceivers:
                    return _service.getTransceivers(args);
                case addTransceiver:
                    return _service.addTransceiver(args);
                case createOffer:
                    return _service.createOffer(args, callbackContext);
                case setLocalDescription:
                    return _service.setLocalDescription(args, callbackContext);
                case setRemoteDescription:
                    return _service.setRemoteDescription(args, callbackContext);
                case addIceCandidate:
                    return _service.addIceCandidate(args, callbackContext);
                case close:
                    return _service.close(args);
                case removeTrack:
                    return _service.removeTrack(args);
                case getStats:
                    return _service.getStats(args, callbackContext);
                //MediaDevice functions
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


    protected void getNecessaryPermission() {
        Log.v(TAG, "getNecessaryPermission:");
        cordova.requestPermissions(this, Hook.NECESSARY_PERM_CODE,
                new String[]{CAMERA,
                        INTERNET,
                        ALERT_WINDOW,
                        RECORD_AUDIO,
                        WAKE_LOCK,
                });
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        Log.e(TAG, "Some permission checking" + requestCode);
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                Log.e(TAG, "Some permission has been denied" + requestCode);
//                return;
            } else {
                Log.e(TAG, "Some permission has been allowed" + requestCode);
            }
        }
    }

}
