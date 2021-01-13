package io.agora.rtcn;

import android.content.res.Resources;
import android.util.Log;

import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;
import com.webrtc.demo.R;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import static org.apache.cordova.PluginResult.Status.OK;

public class WebsocketClient implements WebSocketListener {
    static final String TAG = "WebsocketClient";

    WebSocket ws;
    final CallbackContext callbackContext;

    public WebsocketClient(String uri, Resources resources, CallbackContext callbackContext) {
        WebSocketFactory factory = new WebSocketFactory();
        this.callbackContext = callbackContext;

        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            InputStream caInput = resources.openRawResource(R.raw.ca);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                Log.e(TAG, "ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream certInput12 = resources.openRawResource(R.raw.client);
            keyStore.load(certInput12, "123.com".toCharArray());
            keyStore.setCertificateEntry("ca", ca);
            String algorithm0 = KeyManagerFactory.getDefaultAlgorithm();
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm0);
            kmf.init(keyStore, null);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            factory.setSSLContext(context);

            factory.setVerifyHostname(false);

            ws = factory.createSocket(new URI(uri));
        } catch (Exception e) {
            Log.e(TAG, "create websocket exception: " + e.toString());
        }
        ws.addListener(this);
        try {
            ws.connect();
        } catch (Exception e) {
            Log.e(TAG, "connect failed " + e.toString());
        }
    }

    public void send(String data) {
        ws.sendText(data);
    }

    public void close() {
        ws.removeListener(this);
        ws.sendClose();
        ws.disconnect();
        ws = null;
    }

    enum WSEvent {
        open("open"),
        message("message"),
        close("close"),
        MAX("max");

        private String name;

        WSEvent(String val) {
            this.name = val;
        }

        public String toString() {
            return this.name;
        }
    }

    void onEvent(WSEvent event, String payload) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("event", event);
            obj.put("payload", payload);
        } catch (Exception e) {
            Log.e(TAG, "onEvent exception:" + e.toString());
        }

        PluginResult result = new PluginResult(OK, obj);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    @Override
    public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
        Log.e(TAG, "onStateChanged " + newState);
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        Log.e(TAG, "onConnected");
        onEvent(WSEvent.open, "");
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
        Log.e(TAG, "onConnectError " + cause.toString());

    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
        Log.e(TAG, "onDisconnected " + closedByServer);
        onEvent(WSEvent.close, "");
    }

    @Override
    public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Log.e(TAG, "onFrame");

    }

    @Override
    public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Log.e(TAG, "onContinuationFrame");

    }

    @Override
    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Log.e(TAG, "onTextFrame");

    }

    @Override
    public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Log.e(TAG, "onBinaryFrame");

    }

    @Override
    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Log.e(TAG, "onCloseFrame");

    }

    @Override
    public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Log.e(TAG, "onPingFrame");

    }

    @Override
    public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Log.e(TAG, "onPongFrame");

    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        Log.e(TAG, "onTextMessage " + text);
        onEvent(WSEvent.message, text);
    }

    @Override
    public void onTextMessage(WebSocket websocket, byte[] data) throws Exception {
        Log.e(TAG, "onTextMessage " + new String(data));
        onEvent(WSEvent.message, new String(data));
    }

    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
        Log.e(TAG, "onBinaryMessage");

    }

    @Override
    public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Log.e(TAG, "onSendingFrame");

    }

    @Override
    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Log.e(TAG, "onFrameSent");

    }

    @Override
    public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Log.e(TAG, "onFrameUnsent");

    }

    @Override
    public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
        Log.e(TAG, "onThreadCreated");

    }

    @Override
    public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
        Log.e(TAG, "onThreadStarted");

    }

    @Override
    public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
        Log.e(TAG, "onThreadStopping");

    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        Log.e(TAG, "onError " + cause.toString());

    }

    @Override
    public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
        Log.e(TAG, "onFrameError");

    }

    @Override
    public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {

        Log.e(TAG, "onMessageError");
    }

    @Override
    public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {
        Log.e(TAG, "onMessageDecompressionError");

    }

    @Override
    public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
        Log.e(TAG, "onTextMessageError");

    }

    @Override
    public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
        Log.e(TAG, "onSendError");

    }

    @Override
    public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
        Log.e(TAG, "onUnexpectedError");

    }

    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
        Log.e(TAG, "handleCallbackError");

    }

    @Override
    public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {
        Log.e(TAG, "onSendingHandshake");

    }
}
