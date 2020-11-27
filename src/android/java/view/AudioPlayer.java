package com.agora.cordova.plugin.view;

import android.util.Log;

import com.agora.cordova.plugin.view.enums.Action;
import com.agora.cordova.plugin.view.interfaces.Player;
import com.agora.cordova.plugin.view.interfaces.Supervisor;
import com.agora.cordova.plugin.webrtc.models.MediaStreamTrackWrapper;
import com.agora.cordova.plugin.webrtc.services.MediaDevice;
import com.agora.cordova.plugin.webrtc.services.SettingsContentObserver;

import org.webrtc.AudioTrack;
import org.webrtc.voiceengine.WebRtcAudioRecord;
import org.webrtc.voiceengine.WebRtcAudioTrack;
import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioPlayer implements SettingsContentObserver.VolumeChangeListener, Player, MediaDevice.LocalAudioSampleSupervisor.LocalAudioSampleListener {
    private final static String TAG = AudioPlayer.class.getCanonicalName();

    Supervisor supervisor;
    String id;
    MediaStreamTrackWrapper wrapper;

    public AudioPlayer(Supervisor supervisor, String id, String trackId) {
        this.supervisor = supervisor;
        this.id = id;
        Log.e(TAG, "VOLUME: player trackid" + trackId);

        this.wrapper = MediaStreamTrackWrapper.getMediaStreamTrackById(trackId);

        AudioPlayer that = this;

        if (wrapper.getRelatedObject().size() > 0) {//we have AudioSource object
            MediaDevice.LocalAudioSampleSupervisor.supervisor.addListener(this);
        }
    }

    public void init() {
        SettingsContentObserver.getSettingsContentObserver().registerVolumeChangeListener(this);
    }

    @Override
    public void onAudioLevel(double level) {
        supervisor.onObserveEvent(id, Action.onAudioLevel, String.valueOf(level), "");
    }

    public void setVolume(double volume) {
        if (wrapper != null) {
            Log.e(TAG, "setVolume " + volume);
            if (volume == 0) {
                wrapper.getTrack().setEnabled(false);
                return;
            } else {
                wrapper.getTrack().setEnabled(true);
            }

            ((AudioTrack) wrapper.getTrack()).setVolume(volume);
        }
    }

    @Override
    public void onChange(int volume) {
        supervisor.onObserveEvent(id, Action.onVolumeChange, String.valueOf(volume), "");
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public void dispose() {
        MediaDevice.LocalAudioSampleSupervisor.supervisor.removeListener(this);
    }

}
