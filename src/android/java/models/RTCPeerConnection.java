package com.agora.cordova.plugin.webrtc.models;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import java.util.UUID;

import static org.apache.cordova.PluginResult.Status.NO_RESULT;


public class RTCPeerConnection {
    UUID id;
    CallbackContext context;

    public RTCPeerConnection(String id, final CallbackContext callbackContext) {
        this.id = UUID.fromString(id);
        this.context = callbackContext;

        PluginResult result = new PluginResult(NO_RESULT);
        result.setKeepCallback(true);

        this.context.sendPluginResult(result);
    }

    public UUID getId() {
        return id;
    }
}
