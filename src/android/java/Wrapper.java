package com.agora.cordova.plugin.webrtc;

import android.app.Activity;
import android.util.Log;

import com.agora.cordova.plugin.webrtc.models.MediaStreamConstraints;
import com.agora.cordova.plugin.webrtc.models.RTCConfiguration;
import com.agora.cordova.plugin.webrtc.models.SessionDescription;
import com.agora.cordova.plugin.webrtc.utils.MessageBus;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Wrapper extends WebSocketClient {

    static final String TAG = Wrapper.class.getCanonicalName();

    CordovaInterface cordova;
    String id;
    String target;

    Map<String, CallbackContext> instances;


    public Wrapper(URI uri, CordovaInterface cordova, String id, String target) {
        super(uri);

        this.cordova = cordova;
        this.id = id;
        this.target = target;

        this.instances = new HashMap<>();
    }

    public void createInstance(String id, final CallbackContext callbackContext, RTCConfiguration cfg) {
        Action action = Action.createInstance;

        this.instances.put(id + action, callbackContext);

        MessageBus.Message msg = new MessageBus.Message();
        msg.Target = this.target;
        msg.Object = id;
        msg.Action = action;
        msg.Payload = cfg.toString();
        send(msg.toString());
    }

    public void createOffer(String id, final CallbackContext callbackContext) {
        Action action = Action.createOffer;

        this.instances.put(id + action, callbackContext);

        MessageBus.Message msg = new MessageBus.Message();
        msg.Target = this.target;
        msg.Object = id;
        msg.Action = action;
        send(msg.toString());
    }

    public void setRemoteDescription(String id, final CallbackContext callbackContext, SessionDescription sdp) {
        Action action = Action.setRemoteDescription;

        this.instances.put(id + action, callbackContext);

        MessageBus.Message msg = new MessageBus.Message();
        msg.Target = this.target;
        msg.Object = id;
        msg.Action = action;
        msg.Payload = sdp.toString();
        send(msg.toString());
    }

    public void getUserMedia(String id, final  CallbackContext callbackContext, MediaStreamConstraints constraints){
        Action action = Action.getUserMedia;

        this.instances.put(id+action, callbackContext);

        MessageBus.Message msg = new MessageBus.Message();
        msg.Target = this.target;
        msg.Object = id;
        msg.Action = action;
        msg.Payload = constraints.toString();
        send(msg.toString());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.i(TAG, "Wrapper connected with internal communicate server");
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }
}
