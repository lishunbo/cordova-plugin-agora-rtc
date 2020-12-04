package com.agora.cordova.plugin.webrtc.services;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;

import com.agora.cordova.plugin.view.enums.Action;

import java.util.LinkedList;
import java.util.List;

public class SettingsContentObserver extends ContentObserver {
    int volume;
    int preVolume;
    Context context;
    AudioManager audio;

    List<VolumeChangeListener> listeners;

    static Activity activity;
    static SettingsContentObserver _this = null;
    static final int STREAM_TYPE = AudioManager.STREAM_VOICE_CALL;

    SettingsContentObserver(Context c, Handler handler) {
        super(handler);
        context = c;

        audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        volume = audio.getStreamVolume(STREAM_TYPE);
        preVolume = volume;

        listeners = new LinkedList<>();
    }

    public static void initialize(Activity activity, Handler handler) {
        if (_this == null) {
            SettingsContentObserver.activity = activity;
            activity.setVolumeControlStream(STREAM_TYPE);
            _this = new SettingsContentObserver(activity.getApplication(), handler);
            activity.getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI,
                    true, _this);
        }
    }

    public static void unInitialize() {
        activity.getContentResolver().unregisterContentObserver(_this);
        _this = null;
    }

    public static SettingsContentObserver getSettingsContentObserver() {
        return _this;
    }

    public int getVolume() {
        return volume;
    }

    public int getStreamMaxVolume() {
        return audio.getStreamMaxVolume(STREAM_TYPE);
    }

    @TargetApi(Build.VERSION_CODES.P)
    public int getStreamMinVolume() {
        return audio.getStreamMinVolume(STREAM_TYPE);
    }

    public void setVolume(int volume) {
        audio.setStreamVolume(STREAM_TYPE, volume, AudioManager.ADJUST_SAME);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        volume = audio.getStreamVolume(STREAM_TYPE);
        if (volume != preVolume) {
            preVolume = volume;
            for (VolumeChangeListener listener :
                    listeners) {
                listener.onChange(volume);
            }
        }
    }

    public void registerVolumeChangeListener(VolumeChangeListener listener) {
        listeners.add(listener);
        listener.onChange(volume);
    }

    public void unregisterVolumeChangeListener(VolumeChangeListener listener) {
        listeners.remove(listener);
    }

    public void clear() {
        listeners.clear();
    }

    public interface VolumeChangeListener {
        void onChange(int volume);
    }
}