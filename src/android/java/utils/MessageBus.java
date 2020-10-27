package com.agora.cordova.plugin.webrtc.utils;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MessageBus extends WebSocketServer {
    static final String TAG = MessageBus.class.getCanonicalName();

    Map<String, List<WebSocket>> targets = new HashMap<>();

    public static class Message {
        public String Target;
        public String Object;
        public com.agora.cordova.plugin.webrtc.Action Action;
        public String Payload;

        public static Message formString(String json) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(json, Message.class);
            } catch (JsonProcessingException e) {
                System.out.println(e.toString());
            }
            return null;
        }

        public String toString() {

            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            return "{}";
        }
    }

    public MessageBus(InetSocketAddress addr) {
        super(addr);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String id = handshake.getResourceDescriptor();
        id = id.substring(1);
        Log.v(TAG, "MessageBus: onOpen:" + id);
        List<WebSocket> t = targets.get(id);
        if (t == null) {
            t = new LinkedList<>();
        }
        t.add(conn);
        targets.put(id, t);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String id = conn.getResourceDescriptor().substring(1);
        Log.v(TAG, "MessageBus: onClose:" + id);

        List<WebSocket> t = targets.get(id);
        if (t != null) {
            for (WebSocket c : t) {
                c.close();
            }
            t.clear();
            targets.remove(id);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Message msg = Message.formString(message);
        if (msg == null || msg.Target == null || msg.Target.equals("")) {
            Log.e(TAG, "Invalid message received:" + message);
            return;
        }

        List<WebSocket> cs = targets.get(msg.Target);
        for (WebSocket c : cs) {
            c.send(message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        String id = conn.getResourceDescriptor().substring(1);
        Log.v(TAG, "MessageBus: onError:" + id + "\t" + ex.toString());

        List<WebSocket> t = targets.get(id);
        if (t != null) {
            for (WebSocket c : t) {
                c.close();
            }
            t.clear();
            targets.remove(id);
        }

    }

    @Override
    public void onStart() {
        Log.d(TAG, "MessageBus onStart");
    }
}
