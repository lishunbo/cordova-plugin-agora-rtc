package com.agora.cordova.plugin.view;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.agora.cordova.plugin.view.interfaces.Player;
import com.agora.cordova.plugin.view.model.PlayConfig;
import com.agora.cordova.plugin.view.model.ProxyVideoSink;
import com.agora.cordova.plugin.webrtc.models.MediaStreamTrackWrapper;
import com.agora.cordova.plugin.webrtc.services.MediaDevice;
import com.agora.cordova.plugin.webrtc.services.PCFactory;

import org.apache.cordova.CallbackContext;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import java.util.LinkedList;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

public class VideoView extends SurfaceViewRenderer implements View.OnTouchListener, Player {
    private final static String TAG = VideoView.class.getCanonicalName();

    static WindowManager windowManager;
    static Activity mainActivity;
    static int LAYOUT_FLAG;
    public static int windowWidth;
    public static int windowHeight;

    public String id;
    PlayConfig config;
    boolean isPlayed;
    WindowManager.LayoutParams params;
    ProxyVideoSink sink;

    int originX;
    int originY;

    int startX;
    int startY;

    public static void Initialize(Activity activity) {
        mainActivity = activity;
        windowManager = (WindowManager) mainActivity.getSystemService(WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        windowHeight = displayMetrics.heightPixels;
        windowWidth = displayMetrics.widthPixels;
    }

    VideoView(String id, PlayConfig config) {
        super(mainActivity.getApplicationContext());
        this.id = id;
        this.config = config;
        this.isPlayed = false;
        setOnTouchListener(this);
    }

    public ProxyVideoSink getSink() {
        return sink;
    }

    public void updateConfig(PlayConfig config) {
        this.config = config;
    }

    public void updateVideoTrack(String trackId, final CallbackContext callbackContext) {
        this.config.trackId = trackId;
        if (this.isPlayed) {
            play(callbackContext);
        } else {
            callbackContext.success();
        }
    }

    public void setViewAttribute(int w, int h, int x, int y) {
        params = new WindowManager.LayoutParams(
                w,
                h,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = x;
        params.y = y;
    }

    public void play(CallbackContext context) {
        VideoView that = this;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MediaStreamTrackWrapper wrapper = MediaStreamTrackWrapper.getMediaStreamTrackById(config.trackId);
                if (wrapper == null || wrapper.getTrack() == null || !wrapper.getTrack().kind().toLowerCase().equals("video")) {
                    Log.e(TAG, "cannot show VideoTrack because not found valid VideoTrack " + config.trackId);
                    return;
                }
                wrapper.addVideoView(that);

                RendererCommon.ScalingType type = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
                switch (config.fit) {
                    case cover:
                        type = RendererCommon.ScalingType.SCALE_ASPECT_BALANCED;
                        break;
                    case contain:
                        type = RendererCommon.ScalingType.SCALE_ASPECT_FIT;
                        break;
                    case fill:
                }
//                mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                that.setScalingType(type);
                boolean shouldMirror = false;
                if (wrapper.getRelatedObject().size() >= 4) {
                    Object obj = wrapper.getRelatedObject().get(3);
                    shouldMirror = obj.toString().equals("true");
                }
                that.setMirror(shouldMirror);
                that.setZOrderMediaOverlay(true);

                VideoTrack videoTrack = (VideoTrack) wrapper.getTrack();
                sink = new ProxyVideoSink();
                sink.setTarget(that);
                videoTrack.addSink(sink);

                try {
                    if (!that.isPlayed) {
                        that.init(PCFactory.eglBase(), new RendererCommon.RendererEvents() {
                            @Override
                            public void onFirstFrameRendered() {
                                Log.v(TAG, "onFirstFrameRendered");
                            }

                            @Override
                            public void onFrameResolutionChanged(int i, int i1, int i2) {
                                Log.v(TAG, "onFrameResolutionChanged");
                            }
                        });
                    }
                    windowManager.addView(that, params);
                    that.isPlayed = true;

                    context.success();
                } catch (Exception e) {
                    Log.e(TAG, "show video view failed: " + e.toString());
                    context.error(e.toString());
                }
            }
        });
    }

    public void pause() {
        MediaStreamTrackWrapper wrapper = MediaStreamTrackWrapper.getMediaStreamTrackById(config.trackId);
        if (wrapper == null || wrapper.getTrack() == null) {
            return;
        }

//        VideoTrack videoTrack = (VideoTrack) wrapper.getTrack();
        wrapper.getTrack().setEnabled(false);
    }

    public void destroy() {
        MediaStreamTrackWrapper wrapper = MediaStreamTrackWrapper.popMediaStreamTrackById(config.trackId);
        if (wrapper != null) {
            wrapper.close();
        }
        dispose();
    }

    public void close() {
        VideoView that = this;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                windowManager.removeViewImmediate(that);
            }
        });
    }

    @Override
    public void dispose() {
        if (id == null) {
            return;
        }
        close();

        super.release();

        id = null;
        config = null;
        params = null;
        sink = null;
    }

    @Override
    public void onActivityPause() {

        VideoView that = this;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                windowManager.removeViewImmediate(that);
            }
        });
    }

    @Override
    public void onActivityResume() {
        VideoView that = this;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                windowManager.addView(that, params);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();

                originX = params.x;
                originY = params.y;
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int currentX = (int) event.getRawX();
                int currentY = (int) event.getRawY();
                if (startX != currentX || startY != currentY) {

                    params.x = originX + currentX - startX;
                    params.y = originY + currentY - startY;

                    windowManager.updateViewLayout(this, params);
                }

                break;
            default:
                Log.v(TAG, "not implement motion event:" + event.getAction());
        }
        return false;
    }

}
