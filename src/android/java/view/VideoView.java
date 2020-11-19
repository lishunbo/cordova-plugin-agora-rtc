package com.agora.cordova.plugin.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.agora.cordova.plugin.view.model.PlayConfig;
import com.agora.cordova.plugin.view.model.ProxyVideoSink;
import com.agora.cordova.plugin.webrtc.services.RTCPeerConnection;

import org.webrtc.MediaStreamTrack;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

class VideoView extends SurfaceViewRenderer implements View.OnTouchListener {
    private final static String TAG = VideoView.class.getCanonicalName();

    Supervisor supervisor;
    public final String id;
    PlayConfig config;
    WindowManager.LayoutParams params;

    int originX;
    int originY;

    int startX;
    int startY;


    VideoView(Supervisor supervisor, String id, Context context, PlayConfig config) {
        super(context);
        this.supervisor = supervisor;
        this.id = id;
        this.config = config;
        setOnTouchListener(this);
        Log.e(TAG, "TrackID 0 " + this.config.trackId);
    }

    public void updateConfig(PlayConfig config) {
        this.config = config;
        Log.e(TAG, "TrackID 1 " + this.config.trackId);
    }

    public void updateVideoTrack(String trackId) {
        this.config.trackId = trackId;
        Log.e(TAG, "TrackID 2 " + this.config.trackId);
    }

    public void setViewAttribute(int w, int h, int type, int x, int y) {
        params = new WindowManager.LayoutParams(
                w,
                h,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = x;
        params.y = y;
    }

    public void show() {

        MediaStreamTrack track = RTCPeerConnection.getMediaStreamTrack(config.trackId);
        if (track == null || !track.kind().toLowerCase().equals("video")) {
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

        this.setScalingType(type);
        this.setMirror(config.mirror);
        this.setZOrderMediaOverlay(true);

//        videoTrack.addSink(sink);
        VideoTrack videoTrack = (VideoTrack) track;
        ProxyVideoSink sink = new ProxyVideoSink();
        sink.setTarget(this);
        videoTrack.addSink(sink);
        supervisor.show(this, params);
    }

    public interface Supervisor {
        void show(VideoView view, WindowManager.LayoutParams params);

        void updateLayout(VideoView view, WindowManager.LayoutParams params);

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

                    supervisor.updateLayout(this, params);
                }

                break;
            default:
                Log.v(TAG, "not implement motion event:" + event.getAction());
        }
        return false;
    }
}
