package com.agora.cordova.plugin.webrtc.services;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
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

    SettingsContentObserver(Context c, Handler handler) {
        super(handler);
        context = c;

        audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        preVolume = volume;

        listeners = new LinkedList<>();
    }

    public static void initialize(Activity activity, Handler handler) {
        if (_this == null) {
            SettingsContentObserver.activity = activity;
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
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

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
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