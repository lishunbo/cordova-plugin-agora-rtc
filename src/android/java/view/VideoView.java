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

import com.agora.cordova.plugin.view.model.PlayConfig;
import com.agora.cordova.plugin.view.model.ProxyVideoSink;
import com.agora.cordova.plugin.webrtc.models.MediaStreamTrackWrapper;
import com.agora.cordova.plugin.webrtc.services.PCFactory;

import org.apache.cordova.CallbackContext;
import org.webrtc.AudioSource;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import static android.content.Context.WINDOW_SERVICE;

class VideoView extends SurfaceViewRenderer implements View.OnTouchListener {
    private final static String TAG = VideoView.class.getCanonicalName();

    static WindowManager windowManager;
    static Activity mainActivity;
    static int LAYOUT_FLAG;
    public static int windowWidth;
    public static int windowHeight;

    public final String id;
    PlayConfig config;
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
        setOnTouchListener(this);
    }

    public void updateConfig(PlayConfig config) {
        this.config = config;
    }

    public void updateVideoTrack(String trackId) {
        this.config.trackId = trackId;
    }

    public void setViewAttribute(int w, int h, int x, int y) {
        params = new WindowManager.LayoutParams(
                w,
                h,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
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

                that.setScalingType(type);
                that.setMirror(config.mirror);
                that.setZOrderMediaOverlay(true);

                VideoTrack videoTrack = (VideoTrack) wrapper.getTrack();
                sink = new ProxyVideoSink();
                sink.setTarget(that);
                videoTrack.addSink(sink);

                try {
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

                    windowManager.addView(that, params);

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
        if (wrapper == null || wrapper.getTrack() == null) {
            return;
        }
        if (wrapper.getTrack().kind().equals("audio")&&wrapper.getRelatedObject()!=null&&
                wrapper.getRelatedObject().get(0)!=null &&wrapper.getRelatedObject().get(0) instanceof AudioSource) {
            ((AudioSource)wrapper.getRelatedObject().get(0)).dispose();
        }

        if (wrapper.getTrack().kind().equals("video")) {
            VideoTrack videoTrack = (VideoTrack) wrapper.getTrack();
            videoTrack.removeSink(sink);

            if (wrapper.getRelatedObject() != null) {
                if (wrapper.getRelatedObject().get(0) != null && wrapper.getRelatedObject().get(0) instanceof VideoCapturer) {
                    try {
                        ((VideoCapturer) wrapper.getRelatedObject().get(0)).stopCapture();
                    } catch (Exception e) {
                        Log.e(TAG, "VideoViewService.destroy.VideoCapturer.stopCapture exception: " + e.toString());
                    }
                }
                if (wrapper.getRelatedObject().get(1) != null && wrapper.getRelatedObject().get(1) instanceof SurfaceTextureHelper) {
                    try {
                        ((SurfaceTextureHelper) wrapper.getRelatedObject().get(1)).stopListening();
                        ((SurfaceTextureHelper) wrapper.getRelatedObject().get(1)).dispose();
                    } catch (Exception e) {
                        Log.e(TAG, "VideoViewService.destroy.SurfaceTextureHelper.dispose exception: " + e.toString());
                    }
                }
                if (wrapper.getRelatedObject().get(2) != null && wrapper.getRelatedObject().get(2) instanceof VideoSource) {
                    try {
                        ((VideoSource) wrapper.getRelatedObject().get(2)).dispose();
                    } catch (Exception e) {
                        Log.e(TAG, "VideoViewService.destroy.VideoSource.dispose exception: " + e.toString());
                    }
                }
                wrapper.getRelatedObject().clear();
            }
        }

        super.release();

        VideoView that = this;
        wrapper.getTrack().dispose();
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                windowManager.removeViewImmediate(that);
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
