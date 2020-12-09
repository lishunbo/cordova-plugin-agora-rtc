package com.agora.cordova.plugin.view.model;

import android.util.Log;

import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

public class ProxyVideoSink implements VideoSink {
    private final static String TAG = ProxyVideoSink.class.getCanonicalName();
    private VideoSink target;
    public boolean shouldLog = false;

    @Override
    synchronized
    public void onFrame(VideoFrame videoFrame) {
        if (target == null) {
            Log.d(TAG, "Dropping frame in proxy because target is null.");
            return;
        }
        if (shouldLog){
            Log.w(TAG, "onFrame "+videoFrame.getRotatedWidth()+ " "+ videoFrame.getRotatedHeight()+" ");
        }
        target.onFrame(videoFrame);
    }

    synchronized
    public void setTarget(VideoSink videoSink) {
        target = videoSink;
    }

    public VideoSink getTarget() {
        return target;
    }
}
