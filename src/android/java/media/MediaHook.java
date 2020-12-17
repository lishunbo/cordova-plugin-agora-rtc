package io.agora.rtcn.media;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import io.agora.rtcn.media.enums.Action;
import io.agora.rtcn.media.enums.EMessage;
import io.agora.rtcn.media.services.MediaDevice;
import io.agora.rtcn.media.services.MediaStreamTrackWrapper;
import io.agora.rtcn.media.services.ScreenCaptureService;
import io.agora.rtcn.webrtc.models.MediaStreamConstraints;

import static android.app.Activity.RESULT_OK;
import static org.apache.cordova.PluginResult.Status.OK;

public class MediaHook extends CordovaPlugin {
    static final String TAG = "MediaHook";

    public static final String CAMERA = Manifest.permission.CAMERA;
    public static final String INTERNET = Manifest.permission.INTERNET;
    public static final String NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
    public static final String WIFI_STATE = Manifest.permission.ACCESS_WIFI_STATE;
    public static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String MODIFY_AUDIO_SETTINGS = Manifest.permission.MODIFY_AUDIO_SETTINGS;
    public static final String WAKE_LOCK = Manifest.permission.WAKE_LOCK;
    public static final String ALERT_WINDOW = Manifest.permission.SYSTEM_ALERT_WINDOW;

    public static final int NECESSARY_PERM_CODE = 900;
    public static final int SCREEN_CAPTURE_PERM_CODE = 888;

    CallbackContext eventChannel = null;
    static MediaHook that = null;

    CallbackContext screenCaptureContext = null;
    MediaStreamConstraints screenCaptureConstraints = null;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        that = this;

        if (!(cordova.hasPermission(CAMERA) && cordova.hasPermission(INTERNET)
                && cordova.hasPermission(RECORD_AUDIO) && cordova.hasPermission(WAKE_LOCK) && cordova.hasPermission(ALERT_WINDOW))) {
            getBasicPermission();
        }
        MediaDevice.initialize(cordova.getActivity(), cordova.getActivity().getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        MediaDevice.unInitialize();
    }

    @Override
    public void onReset() {
        super.onReset();
        MediaStreamTrackWrapper.reset();
        MediaDevice.SettingsContentObserver.getSettingsContentObserver().clear();
    }

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {
        try {
            if (!action.equals("enumerateDevices")) {
                Log.v(TAG, "action:" + Action.valueOf(action));
            }

            switch (Action.valueOf(action)) {
                case eventChannel:
                    return createEventChannel(args, callbackContext);
                case enumerateDevices:
                    return enumerateDevices(args, callbackContext);
                case getUserMedia:
                    return getUserMedia(args, callbackContext);
                case stopMediaStreamTrack:
                    return stopMediaStreamTrack(args, callbackContext);
                case getSubVideoTrack:
                    return getSubVideoTrack(args, callbackContext);
                default:
                    Log.e(TAG, "Not implement action: " + action);
                    callbackContext.error("Not implement action:" + action);
                    return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "execute action exception:" + action + " " + e.toString());
            e.printStackTrace();
            callbackContext.error("execute action exception: " + action + " " + e.toString());
            return false;
        }
    }

    protected void getBasicPermission() {
        cordova.requestPermissions(this, NECESSARY_PERM_CODE,
                new String[]{CAMERA,
                        INTERNET,
                        ALERT_WINDOW,
                        RECORD_AUDIO,
                        WAKE_LOCK,
                });
    }

    public static void getScreenCapturePermission() {
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) that.cordova.getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        //Returns an Intent that must passed to startActivityForResult() in order to start screen capture.
        Intent permissionIntent = mediaProjectionManager.createScreenCaptureIntent();

        that.cordova.setActivityResultCallback(that);
        that.cordova.getActivity().startActivityForResult(permissionIntent, SCREEN_CAPTURE_PERM_CODE);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (SCREEN_CAPTURE_PERM_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                MediaDevice.setScreenCaptureIntent(intent);

                //TODO: make this condition correct
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    Intent service = new Intent(cordova.getContext(), ScreenCaptureService.class);
                    service.putExtra("code", resultCode);
                    service.putExtra("data", intent);
                    cordova.getActivity().startForegroundService(service);
                } else {
                    asyncGetScreenCapture();
                }
            } else {
                Log.e(TAG, "request screen capture permission failed");
                if (screenCaptureContext != null) {
                    screenCaptureContext.error(EMessage.ENOSCREENPERMISSION.toString());
                    screenCaptureContext = null;
                    screenCaptureConstraints = null;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    boolean createEventChannel(JSONArray args, final CallbackContext callbackContext) throws Exception {
        if (eventChannel != null) {
            eventChannel.success();
        }
        eventChannel = callbackContext;

        PluginResult result = new PluginResult(OK);
        result.setKeepCallback(true);

        eventChannel.sendPluginResult(result);
        return true;
    }

    boolean enumerateDevices(JSONArray args, final CallbackContext callbackContext) throws Exception {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                callbackContext.success(MediaDevice.enumerateDevices());
            }
        });
        PluginResult result = new PluginResult(OK);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
        return true;
    }

    boolean getUserMedia(JSONArray args, final CallbackContext callbackContext) throws Exception {
        MediaStreamConstraints constraints = MediaStreamConstraints.fromJson(args.getJSONObject(0).toString());

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                assert constraints != null;
                Log.v(TAG, "getUserMedia: " + constraints.toString());
                asyncGetUserMedia(constraints, callbackContext);
            }
        });
        PluginResult result = new PluginResult(OK);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
        return true;
    }

    public static void asyncGetScreenCapture() {
        if (that.screenCaptureContext != null) {
            that.asyncGetUserMedia(that.screenCaptureConstraints, that.screenCaptureContext);
        }
    }

    void asyncGetUserMedia(MediaStreamConstraints constraints, final CallbackContext callbackContext) {
        try {
            callbackContext.success(MediaDevice.getUserMedia(constraints));
            screenCaptureContext = null;
            screenCaptureConstraints = null;
        } catch (Exception e) {
            if (e.getMessage().equals(EMessage.ENOSCREENPERMISSION.toString())) {
                if (screenCaptureContext == null) {
                    screenCaptureContext = callbackContext;
                    screenCaptureConstraints = constraints;
                    getScreenCapturePermission();
                } else {
                    // we try to require screen capture permission but failed
                    callbackContext.error(EMessage.ENOSCREENPERMISSION.toString());
                    screenCaptureContext = null;
                    screenCaptureConstraints = null;
                }
                return;
            }
            Log.e(TAG, "getUserMedia unknown exception " + e.toString());
            callbackContext.error(e.getMessage());
            if (screenCaptureContext != null) {
                screenCaptureContext.error(e.getMessage());
                screenCaptureContext = null;
                screenCaptureConstraints = null;
            }

        }
    }

    boolean stopMediaStreamTrack(JSONArray args, final CallbackContext callbackContext) throws Exception {
        String trackId = args.getString(0);
        Log.v(TAG, "stopMediaStreamTrack " + trackId);

        MediaStreamTrackWrapper wrapper = MediaStreamTrackWrapper.popMediaStreamTrackById(trackId);
        if (wrapper != null) {
            wrapper.close();
        }

        callbackContext.success();
        return true;
    }

    boolean getSubVideoTrack(JSONArray args, final CallbackContext callbackContext) throws Exception {
        String trackId = args.getString(0);

        MediaStreamTrackWrapper trackWrapper = MediaStreamTrackWrapper.getMediaStreamTrackById(trackId);

        if (trackWrapper == null) {
            callbackContext.error("cannot found target track by id: " + trackId);
            Log.e(TAG, "cannot getSubVideoTrack because not found target track by id: " + trackId);
            return false;
        }

        MediaStreamTrackWrapper subTrackWrapper = MediaStreamTrackWrapper.cacheMediaStreamTrackWrapper("",
                trackWrapper.getTrack());

        callbackContext.success(subTrackWrapper.toString());
        return true;
    }
}
