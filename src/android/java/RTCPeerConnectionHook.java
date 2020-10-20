package com.agora.cordova.plugin.webrtc;

// The native Toast API

import android.widget.Toast;
// Cordova-required packages
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RTCPeerConnectionHook extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {
        // Verify that the user sent a 'show' action
        switch (action) {
            case "CreateInstance":

                break;

            default:
                callbackContext.error("RTCPeerConnection not implement action:" + action);
                return false;
        }
        Log.v(TAG, "action:" + action + "\t" + "arguments:" + args.toString());
        try {
            message = args.getString(0);
        } catch (JSONException e) {
            callbackContext.error("=== Error encountered: " + e.getMessage());
            return false;
        }
        // Create the toast
        // Toast toast = Toast.makeText(cordova.getActivity(), message,         Toast.LENGTH_LONG );
        // // Display toast
        // toast.show();
        // Send a positive result to the callbackContext
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
        callbackContext.success(message);
        callbackContext.sendPluginResult(pluginResult);
        System.out.println("=== out rtcplugin");
        return true;
    }

    public class RTCPeerConnection {
        public RTCPeerConnection() {

        }
    }

    //TODO maybe should protected by RWlock
    static Map<String, RTCPeerConnection> allConnections;
}
