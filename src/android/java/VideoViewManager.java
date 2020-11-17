package com.agora.cordova.plugin.webrtc;


import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.agora.cordova.plugin.webrtc.services.PCFactory;

import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import static android.content.Context.WINDOW_SERVICE;

public class VideoViewManager {
    private final static String TAG = VideoViewManager.class.getCanonicalName();

    WindowManager _windowManager;

    Activity _mainActivity;

    int LAYOUT_FLAG;
    public final int windowWidth;
    public final int windowHeight;

    public VideoViewManager(Activity mainActivity) {
        _mainActivity = mainActivity;
        _windowManager = (WindowManager) _mainActivity.getSystemService(WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        _windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        windowHeight = displayMetrics.heightPixels;
        windowWidth = displayMetrics.widthPixels;
    }

    public void show(VideoTrack videoTrack, int w, int h, int x, int y, boolean mirror) {
        final WindowManager.LayoutParams paramsF = new WindowManager.LayoutParams(
                w,
                h,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT);

        paramsF.gravity = Gravity.TOP | Gravity.LEFT;
        paramsF.x = x;
        paramsF.y = y;

        _mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    VideoView view = new VideoView(_mainActivity.getApplication(), paramsF);
                    view.init(PCFactory.eglBase(), new RendererCommon.RendererEvents() {
                        @Override
                        public void onFirstFrameRendered() {
                            Log.v(TAG, "onFirstFrameRendered");
                        }

                        @Override
                        public void onFrameResolutionChanged(int i, int i1, int i2) {
                            Log.v(TAG, "onFrameResolutionChanged");

                        }
                    });

                    view.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
                    view.setMirror(mirror);
                    view.setZOrderMediaOverlay(true);

                    ProxyVideoSink sink = new ProxyVideoSink();
                    sink.setTarget(view);

                    videoTrack.addSink(sink);

                    _windowManager.addView(view, paramsF);
                } catch (Exception e) {
                    Log.e(TAG, "maybe no permission to Draw over other apps " + e.toString());
                }
            }
        });
    }

    public class VideoView extends SurfaceViewRenderer implements View.OnTouchListener {
        int startX;
        int startY;

        int originX;
        int originY;
        WindowManager.LayoutParams _params;

        VideoView(Context context, WindowManager.LayoutParams param) {
            super(context);
            setOnTouchListener(this);

            _params = param;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();

                    originX = _params.x;
                    originY = _params.y;
                    break;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    int currentX = (int) event.getRawX();
                    int currentY = (int) event.getRawY();
                    if (startX != currentX || startY != currentY) {

                        _params.x = originX + currentX - startX;
                        _params.y = originY + currentY - startY;

                        _windowManager.updateViewLayout(v, _params);
                    }

                    break;
                default:
                    Log.v(TAG, "not implement motion event:" + event.getAction());
            }
            return false;
        }

    }
}