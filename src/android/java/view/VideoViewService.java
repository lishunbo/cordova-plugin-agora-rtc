package com.agora.cordova.plugin.view;

import android.app.Activity;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.agora.cordova.plugin.view.model.PlayConfig;
import com.agora.cordova.plugin.webrtc.services.PCFactory;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.RendererCommon;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.WINDOW_SERVICE;
import static org.apache.cordova.PluginResult.Status.OK;

public class VideoViewService {
    private final static String TAG = VideoViewService.class.getCanonicalName();


    Map<String, CallbackVVPeer> instances;

    public VideoViewService(Activity mainActivity) {
        this.instances = new HashMap<>();

        VideoView.Initialize(mainActivity);
    }

    static class CallbackVVPeer {
        CallbackContext context;
        VideoView vv;

        CallbackVVPeer setCallbackContext(CallbackContext context) {
            this.context = context;
            return this;
        }

        CallbackVVPeer setVideoView(VideoView vv) {
            this.vv = vv;
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
        this.instances.put(id, new CallbackVVPeer().setCallbackContext(callbackContext).setVideoView(vp));

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
        peer.vv.updateConfig(cfg);

        callbackContext.success();
        return true;
    }

    public boolean updateVideoTrack(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String id = args.getString(0);

        CallbackVVPeer peer = this.instances.get(id);
        assert peer != null;

        Log.v(TAG, "updateVideoTrack " + args.getString(1) + " " + args.getString(2));

        peer.vv.updateVideoTrack(args.getString(1));

        callbackContext.success();
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
        peer.vv.play(callbackContext);
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

        peer.vv.destroy();
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
        peer.vv.setViewAttribute(w, h, x, y);
        callbackContext.success();
        return true;
    }

    public boolean onActivityPause() {
        for (Map.Entry<String, CallbackVVPeer> peer :
                this.instances.entrySet()) {
            peer.getValue().vv.onActivityPause();
        }

        return true;
    }

    public boolean onActivityResume() {
        for (Map.Entry<String, CallbackVVPeer> peer :
                this.instances.entrySet()) {
            peer.getValue().vv.onActivityResume();
        }
        return true;
    }
}
