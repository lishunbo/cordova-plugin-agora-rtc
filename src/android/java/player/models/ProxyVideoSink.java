package io.agora.rtc.player.models;

import android.util.Log;

import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

public class ProxyVideoSink implements VideoSink {
    private final static String TAG = ProxyVideoSink.class.getCanonicalName();
    private VideoSink target;

    @Override
    synchronized
    public void onFrame(VideoFrame videoFrame) {
        if (target == null) {
            Log.d(TAG, "Dropping frame in proxy because target is null.");
            return;
        }
        target.onFrame(videoFrame);
    }

    synchronized
    public void setTarget(VideoSink videoSink) {
        target = videoSink;
    }
}
