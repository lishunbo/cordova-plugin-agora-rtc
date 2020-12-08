package com.agora.cordova.plugin.view;

import android.app.Activity;
import android.util.Log;

import com.agora.cordova.plugin.view.enums.Action;
import com.agora.cordova.plugin.view.interfaces.Player;
import com.agora.cordova.plugin.view.interfaces.Supervisor;
import com.agora.cordova.plugin.view.model.PlayConfig;
import com.agora.cordova.plugin.webrtc.models.MediaStreamTrackWrapper;
import com.agora.cordova.plugin.webrtc.services.MediaDevice;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static org.apache.cordova.PluginResult.Status.OK;

public class VideoViewService implements Supervisor {
    private final static String TAG = VideoViewService.class.getCanonicalName();


    Map<String, CallbackVVPeer> instances;

    public VideoViewService(Activity mainActivity) {
        this.instances = new HashMap<>();

        VideoView.Initialize(mainActivity);
    }

    @Override
    public void onObserveEvent(String id, Action action, String message, String usage) {

        CallbackVVPeer peer = instances.get(id);
        if (peer == null) {
            return;
        }

        if (!(action == Action.onAudioLevel)) {
            Log.v(TAG, usage + " onObserveEvent " + action + " " + message);
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("event", action.toString());
            obj.put("id", id);
            obj.put("payload", message);
        } catch (Exception e) {
            Log.e(TAG, "event exception:" + action);
        }
        PluginResult result = new PluginResult(OK, obj);
        result.setKeepCallback(true);
        peer.context.sendPluginResult(result);
    }

    static class CallbackVVPeer {
        CallbackContext context;
        Player player;

        CallbackVVPeer setCallbackContext(CallbackContext context) {
            this.context = context;
            return this;
        }

        CallbackVVPeer setPlayer(Player player) {
            this.player = player;
            return this;
        }
    }

    public boolean createInstance(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        PlayConfig cfg = null;
        if (args.length() > 1) {
            String json = args.get(1).toString();
            if (json.length() != 0) {
                cfg = PlayConfig.fromJson(json);
            }
        }
        if (cfg == null) {
            Log.e(TAG, "Invalid RTCConfiguration config, using default");
            cfg = new PlayConfig();
        }

        VideoView vp = new VideoView(id, cfg);

        //for eventCallback
        this.instances.put(id, new CallbackVVPeer().setCallbackContext(callbackContext).setPlayer(vp));

        PluginResult result = new PluginResult(OK);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
        return true;
    }

    public boolean updateConfig(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        PlayConfig cfg = null;
        String json = args.get(1).toString();
        if (json.length() != 0) {
            cfg = PlayConfig.fromJson(json);
        }
        if (cfg == null) {
            Log.e(TAG, "update Config: invalid arguments");
            callbackContext.error("invalid arguments");
        }

        CallbackVVPeer peer = this.instances.get(id);
        assert peer != null;
        ((VideoView) peer.player).updateConfig(cfg);

        callbackContext.success();
        return true;
    }

    public boolean updateVideoTrack(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        CallbackVVPeer peer = this.instances.get(id);
        assert peer != null;
        
        ((VideoView) peer.player).updateVideoTrack(args.getString(1), callbackContext);
        return true;
    }

    public boolean play(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        CallbackVVPeer peer = this.instances.get(id);
        assert peer != null;

        PluginResult result = new PluginResult(OK);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);

        // this function will do finally callback in main ui thread
        ((VideoView) peer.player).play(callbackContext);
        return true;
    }

    public boolean pause(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        callbackContext.success();
        return true;
    }

    public boolean destroy(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);


        CallbackVVPeer peer = this.instances.get(id);
        assert peer != null;

        if (peer.player instanceof VideoView) {
            ((VideoView) peer.player).destroy();
        } else {
            Log.w(TAG, "audio player should be destoried");
        }
        peer.context.success("dispose");

        this.instances.remove(id);

        callbackContext.success();
        return true;
    }

    public boolean getCurrentFrame(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        callbackContext.success();
        return true;
    }

    public boolean getWindowAttribute(JSONArray args, final CallbackContext callbackContext) throws JSONException {

        JSONObject obj = new JSONObject();
        obj.put("width", VideoView.windowWidth);
        obj.put("height", VideoView.windowHeight);

        callbackContext.success(obj.toString());
        return true;
    }

    public boolean setViewAttribute(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);
        int w = args.getInt(1);
        int h = args.getInt(2);
        int x = args.getInt(3);
        int y = args.getInt(4);

        CallbackVVPeer peer = this.instances.get(id);
        assert peer != null;
        ((VideoView) peer.player).setViewAttribute(w, h, x, y);
        callbackContext.success();
        return true;
    }

    //AudioPlayer
    public boolean createAudioPlayer(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        AudioPlayer ap = new AudioPlayer(this, id, args.getString(1));

        //for eventCallback
        this.instances.put(id, new CallbackVVPeer().setCallbackContext(callbackContext).setPlayer(ap));

        PluginResult result = new PluginResult(OK);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);

        ap.init();

        return true;
    }

    public boolean getVolumeRange(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("max", MediaDevice.getMaxVolume());
        obj.put("min", MediaDevice.getMinVolume());

        callbackContext.success(obj.toString());

        return true;
    }

    public boolean getVolume(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        callbackContext.success(MediaDevice.getVolume());
        return true;
    }

    public boolean setVolume(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        int volume = args.getInt(1);

        CallbackVVPeer peer = this.instances.get(args.getString(0));
        if (peer != null) {
            ((AudioPlayer) peer.player).setVolume(volume);
        }

        callbackContext.success();
        return true;
    }

    public boolean setSinkID(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        int deviceId = args.getInt(2);

        MediaDevice.setPlaybackDevice(deviceId);
        callbackContext.success();
        return true;
    }


    public boolean onActivityPause() {
        for (Map.Entry<String, CallbackVVPeer> peer :
                this.instances.entrySet()) {
            peer.getValue().player.onActivityPause();
        }

        return true;
    }

    public boolean onActivityResume() {
        for (Map.Entry<String, CallbackVVPeer> peer :
                this.instances.entrySet()) {
            peer.getValue().player.onActivityResume();
        }
        return true;
    }

    public void reset() {
        for (Map.Entry<String, CallbackVVPeer> peer :
                this.instances.entrySet()) {
            peer.getValue().player.dispose();
        }
        this.instances.clear();
    }
}
