package com.agora.cordova.plugin.webrtc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import com.agora.cordova.plugin.webrtc.services.MediaDevice;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

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

    WebRTCService service;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.v(TAG, "Hook initializing");

        service = new WebRTCService(cordova.getActivity(), cordova);

        if (!(cordova.hasPermission(CAMERA) && cordova.hasPermission(INTERNET)
                && cordova.hasPermission(RECORD_AUDIO) && cordova.hasPermission(WAKE_LOCK) && cordova.hasPermission(ALERT_WINDOW))) {
            getNecessaryPermission();
        }

        Log.v(TAG, "Hook initialized");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service.onDestroy();
    }

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {
        try {
            if (Config.logInternalMessage && !action.equals("getStats") && !action.equals("enumerateDevices")) {
                Log.e(TAG, "actioin:" + Action.valueOf(action));
            }
            switch (Action.valueOf(action)) {
                case enumerateDevices:
                    return service.enumerateDevices(args, callbackContext);
                case getUserMedia:
                    return service.getUserMedia(args, callbackContext);
                case stopMediaStreamTrack:
                    return service.stopMediaStreamTrack(args, callbackContext);
                //Keep Instance context for callback
                case createInstance:
                    return service.createInstance(args, callbackContext);
                //PeerConnection sub functions
                case addTrack:
                    return service.addTrack(args, callbackContext);
                case getTransceivers:
                    return service.getTransceivers(args);
                case addTransceiver:
                    return service.addTransceiver(args);
                case createOffer:
                    return service.createOffer(args, callbackContext);
                case setLocalDescription:
                    return service.setLocalDescription(args, callbackContext);
                case setRemoteDescription:
                    return service.setRemoteDescription(args, callbackContext);
                case addIceCandidate:
                    return service.addIceCandidate(args, callbackContext);
                case close:
                    return service.close(args);
                case removeTrack:
                    return service.removeTrack(args, callbackContext);
                case getStats:
                    return service.getStats(args, callbackContext);
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

    @Override
    public void onReset() {
        super.onReset();
        Log.e(TAG, "reset pages");
        service.reset();
    }
}
