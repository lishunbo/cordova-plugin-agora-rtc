package io.agora.rtcn.player;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.agora.rtcn.media.services.MediaDevice;
import io.agora.rtcn.player.enums.Action;
import io.agora.rtcn.player.interfaces.Player;
import io.agora.rtcn.player.interfaces.Supervisor;
import io.agora.rtcn.player.models.PlayConfig;
import io.agora.rtcn.player.services.AudioPlayer;
import io.agora.rtcn.player.services.VideoView;

import static org.apache.cordova.PluginResult.Status.OK;

public class PlayerHook extends CordovaPlugin implements Supervisor {
    static final String TAG = "PlayerHook";
    static final int OVERLAY_PERMISSION_CODE = 1000;

    Map<String, CallbackVVPeer> instances = new HashMap<>();

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(cordova.getActivity())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + cordova.getActivity().getPackageName()));

            cordova.startActivityForResult(new CordovaPlugin() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                    super.onActivityResult(requestCode, resultCode, intent);

                    Log.v(TAG, "onActivityResult " + requestCode + " " + resultCode);
                }
            }, intent, OVERLAY_PERMISSION_CODE);
        }
        VideoView.Initialize(cordova.getActivity());
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        for (Map.Entry<String, CallbackVVPeer> peer :
                this.instances.entrySet()) {
            peer.getValue().player.onActivityPause();
        }
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        for (Map.Entry<String, CallbackVVPeer> peer :
                this.instances.entrySet()) {
            peer.getValue().player.onActivityResume();
        }
    }

    @Override
    public void onReset() {
        super.onReset();
        for (Map.Entry<String, CallbackVVPeer> peer :
                this.instances.entrySet()) {
            peer.getValue().context.success();
            peer.getValue().player.dispose();
        }
        this.instances.clear();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        super.execute(action, args, callbackContext);

        try {
            Log.v(TAG, "action: " + Action.valueOf(action));
            switch (Action.valueOf(action)) {
                //VideoView
                case createVideoPlayer:
                    return createVideoPlayer(args, callbackContext);
                case updateConfig:
                    return updateConfig(args, callbackContext);
                case updateVideoTrack:
                    return updateVideoTrack(args, callbackContext);
                case playVideoPlayer:
                    return play(args, callbackContext);
                case pauseVideoPlayer:
                    return pause(args, callbackContext);
                case destroyVideoPlayer:
                    return destroy(args, callbackContext);
                case getCurrentFrame:
                    return getCurrentFrame(args, callbackContext);
                case getWindowAttribute:
                    return getWindowAttribute(args, callbackContext);
                case setViewAttribute:
                    return setViewAttribute(args, callbackContext);
                //AudioControl

                case getVolumeRange:
                    return getVolumeRange(args, callbackContext);
                case getVolume:
                    return getVolume(args, callbackContext);
                case setVolume:
                    return setVolume(args, callbackContext);
                case setSinkId:
                    return setSinkID(args, callbackContext);
                case createAudioPlayer:
                    return createAudioPlayer(args, callbackContext);
                case playAudioPlayer:
                case pauseAudioPlayer:
                case destroyAudioPlayer:
                case updateTrackAudioPlayer:
                    callbackContext.success();
                    return true;

                default:
                    Log.e(TAG, "Not implement action of: " + action);
                    callbackContext.error("not implement action:" + action);
                    return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "execute action " + action + " exception:" + e.toString());
            callbackContext.error("execute action " + action + " exception:" + e.toString());
            return false;
        }
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

    boolean createVideoPlayer(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        PlayConfig cfg = PlayConfig.fromJson(args.getString(1));
        if (cfg == null) {
            Log.e(TAG, "Invalid PlayConfig, using default");
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

    boolean updateConfig(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        PlayConfig cfg = null;
        String json = args.getString(1);
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

    boolean updateVideoTrack(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        CallbackVVPeer peer = this.instances.get(id);
        assert peer != null;

        ((VideoView) peer.player).updateVideoTrack(args.getString(1), callbackContext);
        return true;
    }

    boolean play(JSONArray args, final CallbackContext callbackContext) throws JSONException {
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

    boolean pause(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        callbackContext.success();
        return true;
    }

    boolean destroy(JSONArray args, final CallbackContext callbackContext) throws JSONException {
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

    boolean getCurrentFrame(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        callbackContext.success();
        return true;
    }

    boolean getWindowAttribute(JSONArray args, final CallbackContext callbackContext) throws JSONException {

        JSONObject obj = new JSONObject();
        obj.put("width", VideoView.windowWidth);
        obj.put("height", VideoView.windowHeight);

        callbackContext.success(obj.toString());
        return true;
    }

    boolean setViewAttribute(JSONArray args, final CallbackContext callbackContext) throws JSONException {
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
    boolean createAudioPlayer(JSONArray args, final CallbackContext callbackContext) throws JSONException {
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

    boolean getVolumeRange(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("max", MediaDevice.getMaxVolume());
        obj.put("min", MediaDevice.getMinVolume());

        callbackContext.success(obj.toString());

        return true;
    }

    boolean getVolume(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        callbackContext.success(MediaDevice.getVolume());
        return true;
    }

    boolean setVolume(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        int volume = args.getInt(1);

        CallbackVVPeer peer = this.instances.get(args.getString(0));
        if (peer != null) {
            ((AudioPlayer) peer.player).setVolume(volume);
        }

        callbackContext.success();
        return true;
    }

    boolean setSinkID(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        int deviceId = args.getInt(2);

        MediaDevice.setPlaybackDevice(deviceId);
        callbackContext.success();
        return true;
    }
}
