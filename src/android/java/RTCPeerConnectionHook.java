package com.agora.cordova.plugin.webrtc;

// The native Toast API

import android.util.Log;
import android.widget.Toast;
// Cordova-required packages
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.PeerConnectionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class RTCPeerConnectionHook extends CordovaPlugin {
    static final String TAG = RTCPeerConnectionHook.class.getCanonicalName();
    //TODO maybe should protected by RWlock
    Map<String, RTCPeerConnection> allConnections;

    PeerConnectionFactory factory;
    EglBase eglBse;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        allConnections = new HashMap<>();

        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(this.cordova.getActivity())
                .createInitializationOptions());

        eglBse = EglBase.create();

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        DefaultVideoEncoderFactory defaultVideoEncoderFactory =
                new DefaultVideoEncoderFactory(eglBse.getEglBaseContext(), true, true);
        DefaultVideoDecoderFactory defaultVideoDecoderFactory =
                new DefaultVideoDecoderFactory(eglBse.getEglBaseContext());
        factory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory();
    }

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {
        Log.v(TAG, "action:" + action + "\t" + "arguments:" + args.toString());
        // Verify that the user sent a 'show' action
        String id = "";
        switch (action) {
            case "CreateInstance":
                id = createInstance();
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
        return true;
    }

    public class RTCPeerConnection {
        UUID id;

        public RTCPeerConnection() {
            this.id = UUID.randomUUID();
        }
    }

    String createInstance() {
        RTCPeerConnection pc = new RTCPeerConnection();
        allConnections.put(pc.id.toString(), pc);
        return pc.id.toString();
    }

}
