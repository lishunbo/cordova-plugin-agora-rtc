package io.agora.rtcn.player.services;

import android.util.Log;

import org.webrtc.AudioTrack;

import io.agora.rtcn.player.enums.Action;
import io.agora.rtcn.player.interfaces.Player;
import io.agora.rtcn.player.interfaces.Supervisor;
import io.agora.rtcn.media.services.MediaStreamTrackWrapper;
import io.agora.rtcn.media.services.MediaDevice;
import io.agora.rtcn.webrtc.services.PCFactory;

public class AudioPlayer implements MediaDevice.SettingsContentObserver.VolumeChangeListener, Player, MediaDevice.LocalAudioSampleSupervisor.LocalAudioSampleListener {
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
        MediaDevice.SettingsContentObserver.getSettingsContentObserver().registerVolumeChangeListener(this);
    }

    @Override
    public void onAudioLevel(double level) {
        supervisor.onObserveEvent(id, Action.onAudioLevel, String.valueOf(level), "");
    }

    public void setVolume(double volume) {
        volume /= 10.0;
        if (wrapper != null) {
            Log.v(TAG, "setVolume" + volume);
            if (wrapper.isLocal()) {
                Log.v(TAG, "setVolume set local");
                if (PCFactory.audioDeviceModule() != null) {
                    PCFactory.audioDeviceModule().setMicrophoneMute(volume == 0);
                }
            } else {
                Log.v(TAG, "setVolume set remote");
                wrapper.getTrack().setEnabled(volume != 0);
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
