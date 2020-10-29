package com.agora.cordova.plugin.webrtc;

import android.util.JsonReader;
import android.util.Log;

import com.agora.cordova.plugin.webrtc.models.MediaStreamConstraints;
import com.agora.cordova.plugin.webrtc.models.RTCConfiguration;
import com.agora.cordova.plugin.webrtc.models.SessionDescription;
import com.agora.cordova.plugin.webrtc.utils.MessageBus;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.apache.cordova.PluginResult.Status.OK;

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

    // to view activity
    public void createInstance(String id, final CallbackContext callbackContext, RTCConfiguration cfg) {

        Action action = Action.createInstance;

        this.instances.put(id + action, callbackContext);
        this.instances.put(id, callbackContext);

        MessageBus.Message msg = new MessageBus.Message();
        msg.target = this.target;
        msg.object = id;
        msg.action = action;
        msg.payload = cfg.toString();
        send(msg.toString());
    }

    // to view activity
    public void getUserMedia(String id, final CallbackContext callbackContext, MediaStreamConstraints constraints) {
        Action action = Action.getUserMedia;

        this.instances.put(id + action, callbackContext);

        MessageBus.Message msg = new MessageBus.Message();
        msg.target = this.target;
        msg.object = id;
        msg.action = action;
        msg.payload = constraints.toString();
        send(msg.toString());
    }

    //to PeerConnection
    public void addTrack(String id, CallbackContext callbackContext) {
        Action action = Action.addTrack;

        this.instances.put(id + action, callbackContext);

        MessageBus.Message msg = new MessageBus.Message();
        msg.target = id;
        msg.object = id;
        msg.action = action;
//        msg.payload = constraints.toString();
        send(msg.toString());
    }

    //to PeerConnection
    public void createOffer(String id, final CallbackContext callbackContext) {
        Action action = Action.createOffer;

        this.instances.put(id + action, callbackContext);

        MessageBus.Message msg = new MessageBus.Message();
        msg.target = id;
        msg.object = id;
        msg.action = action;
        send(msg.toString());
    }

    //to PeerConnection
    public void setLocalDescription(String id, String sdp, CallbackContext callbackContext) {
        Action action = Action.setLocalDescription;

        this.instances.put(id + action, callbackContext);

        MessageBus.Message msg = new MessageBus.Message();
        msg.target = id;
        msg.object = id;
        msg.action = action;
        msg.payload = sdp;
        send(msg.toString());
    }

    //to PeerConnection
    public void setRemoteDescription(String id, final CallbackContext callbackContext, String sdp) {
        Action action = Action.setRemoteDescription;

        this.instances.put(id + action, callbackContext);

        MessageBus.Message msg = new MessageBus.Message();
        msg.target = id;
        msg.object = id;
        msg.action = action;
        msg.payload = sdp;
        send(msg.toString());
    }

    //to PeerConnection
    public void addIceCandidate(String id, String candidata, CallbackContext callbackContext) {
        Action action = Action.addIceCandidate;

        this.instances.put(id + action, callbackContext);

        MessageBus.Message msg = new MessageBus.Message();
        msg.target = id;
        msg.object = id;
        msg.action = action;
        msg.payload = candidata;
        send(msg.toString());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e(TAG, "Wrapper onOpen: connected with internal communicate server");
    }

    @Override
    public void onMessage(String message) {
        Log.e(TAG, "Wrapper onMessage:" + message);
        MessageBus.Message msg = MessageBus.Message.formString(message);
        assert msg != null;
        CallbackContext callbackContext = this.instances.get(msg.object + msg.action);
        this.instances.remove(msg.object + msg.action);
        if (msg.action == Action.onIceCandidate) {
            callbackContext = this.instances.get(msg.object);
        }
        assert callbackContext != null;
        switch (msg.action) {
            case createInstance: {
                PluginResult result = new PluginResult(OK, msg.object);
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
                break;
            }
            case getUserMedia:
                callbackContext.success();
//                PluginResult result = new PluginResult(OK);
//                result.setKeepCallback(true);
//                callbackContext.sendPluginResult(result);
                break;
            case addTrack:
                callbackContext.success();
//                PluginResult result = new PluginResult(OK);
//                result.setKeepCallback(true);
//                callbackContext.sendPluginResult(result);
                break;
            case createOffer:
                callbackContext.success(msg.payload);
//                PluginResult result = new PluginResult(OK);
//                result.setKeepCallback(true);
//                callbackContext.sendPluginResult(result);
                break;
            case setLocalDescription:
            case addIceCandidate:
                callbackContext.success();
//                PluginResult result = new PluginResult(OK);
//                result.setKeepCallback(true);
//                callbackContext.sendPluginResult(result);
                break;

            case onIceCandidate: {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("event", msg.action.toString());
                    obj.put("id", msg.object);
                    obj.put("payload", msg.payload);
                } catch (Exception e) {
                    Log.e(TAG, "event exception:" + msg.action);
                }
                PluginResult result = new PluginResult(OK, obj);
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
                break;
            }
            default:
                Log.e(TAG, "unknown action message internal");
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e(TAG, "Wrapper onClose: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.e(TAG, "Wrapper onError: " + ex.toString());
    }
}
