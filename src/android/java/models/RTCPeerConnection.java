package com.agora.cordova.plugin.webrtc.models;

import android.content.Intent;
import android.util.Log;

import com.agora.cordova.plugin.webrtc.Action;
import com.agora.cordova.plugin.webrtc.WebRTCViewActivity;
import com.agora.cordova.plugin.webrtc.utils.MessageBus;
import com.agora.demo.four.R;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;


public class RTCPeerConnection {
    static final String TAG = RTCPeerConnection.class.getCanonicalName();

    String hook_id;
    String id;
    MessageBusClient client;

    CallbackContext context;
    RTCConfiguration config;
    PCViewer pcViewer;

    public interface PCViewer {

    }

    public RTCPeerConnection(PCViewer pcviewer, String hook_id, String id, String internalws, RTCConfiguration config) {
        this.pcViewer = pcviewer;
        this.hook_id = hook_id;
        this.id = id;
        this.config = config;

        try {
            this.client = new MessageBusClient(new URI(internalws + this.id));
            this.client.connect();
        } catch (URISyntaxException e) {
            Log.e(TAG, "new MessageBusClient error:" + e.toString());
        }
    }

    public String getId() {
        return id;
    }

    public class MessageBusClient extends WebSocketClient {

        public MessageBusClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.v(TAG, "Plugin connected to server:" + handshakedata.getHttpStatusMessage());
            createInstanceResp();
        }

        @Override
        public void onMessage(String message) {
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.e(TAG, "onClose: " + reason);
        }

        @Override
        public void onError(Exception ex) {
            Log.e(TAG, "onError" + ex.toString());
        }

        void createInstanceResp(){
            MessageBus.Message msg = new MessageBus.Message();
            msg.target = hook_id;
            msg.object = id;
            msg.action = Action.createInstance;
//            msg.payload = "{}";
            send(msg.toString());
        }
    }
}
