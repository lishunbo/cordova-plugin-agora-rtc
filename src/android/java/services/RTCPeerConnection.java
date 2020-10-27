package com.agora.cordova.plugin.webrtc.services;

import android.content.Intent;
import android.util.Log;

import com.agora.cordova.plugin.webrtc.WebRTCViewActivity;
import com.agora.cordova.plugin.webrtc.models.RTCConfiguration;
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

    CordovaInterface cordova;
    String id;
    MessageBusClient client;

    CallbackContext context;
    RTCConfiguration config;

    public RTCPeerConnection(CordovaInterface cordova, String id, final CallbackContext callbackContext, RTCConfiguration config) {
        this.cordova = cordova;
        this.id = id;
        this.context = callbackContext;
        this.config = config;

        cordova.getThreadPool().execute(() -> {
            try {
                this.client = new MessageBusClient(new URI(cordova.getActivity().getString(R.string.internalws) + this.id));
            } catch (URISyntaxException e) {
                Log.e(TAG, "new MessageBusClient error:" + e.toString());
            }
        });
    }

    public String getId() {
        return id;
    }


    public void createOffer(final CallbackContext callbackContext) {
        this.cordova.getThreadPool().execute(() -> {
            Intent intent = new Intent(cordova.getActivity().getApplicationContext(), WebRTCViewActivity.class);

            intent.putExtra(cordova.getActivity().getString(R.string.hook_id), id);

            cordova.getActivity().startActivity(intent);
        });
    }


    public class MessageBusClient extends WebSocketClient {

        public MessageBusClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.v(TAG, "Plugin connected to server:" + handshakedata.getHttpStatusMessage());
        }

        @Override
        public void onMessage(String message) {
            Log.e(TAG, "onMessage:" + message);
            MessageBus.Message msg = MessageBus.Message.formString(message);
            if (!msg.Target.equals(id.toString())) {
                Log.e(TAG, "invalid message has been received");
                return;
            }
            if (msg.Action.equals("offer")) {
//                offer = msg.Payload;
//                Log.e(TAG, "return offer to js:" + offer);
////                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
////                MessageBus.Message msg = new MessageBus.Message();
////                msg.Action = "offer";
////                msg.Payload = offer;
//                callbackContext.success(offer);
////                callbackContext.sendPluginResult(pluginResult);
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.e(TAG, "Plugin implement onClose: " + reason);
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        client.reconnect();
                    } catch (Exception e) {
                        Log.e(TAG, "cannot create Client" + e.toString());
                    }
                }
            });
        }

        @Override
        public void onError(Exception ex) {
            Log.e(TAG, "Plugin not implement onError" + ex.toString());

            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    client.reconnect();
                }
            });
        }

        public void send(String type, String payload) {
//            MessageBus.Message msg = new MessageBus.Message();
//            msg.Target = webrtclocal_id;
//            msg.Action = "neecOffer";
//            client.send(msg.toString());
        }
    }
}
