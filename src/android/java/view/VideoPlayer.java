package com.agora.cordova.plugin.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.agora.cordova.plugin.webrtc.Config;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class VideoPlayer extends CordovaPlugin {
    static final String TAG = VideoPlayer.class.getCanonicalName();

    public static final int OVERLAY_PERMISSION_CODE = 1000;
    private VideoViewService service;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        if (Build.VERSION.SDK_INT >= 23 && (!Settings.canDrawOverlays(cordova.getActivity()))) {
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

        service = new VideoViewService(cordova.getActivity());
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        super.execute(action, args, callbackContext);

        try {
            if (Config.logInternalMessage) {
                Log.e(TAG, "action: " + Action.valueOf(action));
            }
            switch (Action.valueOf(action)) {
                case createInstance:
                    return service.createInstance(args, callbackContext);
                case updateConfig:
                    return service.updateConfig(args, callbackContext);
                case aaa:
                    return service.updateVideoTrack(args, callbackContext);
                case play:
                    return service.play(args, callbackContext);
                case pause:
                    return service.pause(args, callbackContext);
                case destroy:
                    return service.destroy(args, callbackContext);
                case getCurrentFrame:
                    return service.getCurrentFrame(args, callbackContext);
                case getWindowAttribute:
                    return service.getWindowAttribute(args, callbackContext);
                case setViewAttribute:
                    return service.setViewAttribute(args, callbackContext);
                default:
                    Log.e(TAG, "Not implement action of :" + action);
                    callbackContext.error("RTCPeerConnection not implement action:" + action);
                    return false;
            }
        } catch (Exception e) {

            Log.e(TAG, "action " + action + " exception:" + e.toString());
            callbackContext.error(e.toString());
            return false;
        }

    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
    }

}
