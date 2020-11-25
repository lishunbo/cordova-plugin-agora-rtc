package com.agora.cordova.plugin.view;

import com.agora.cordova.plugin.view.enums.Action;
import com.agora.cordova.plugin.view.interfaces.Player;
import com.agora.cordova.plugin.view.interfaces.Supervisor;
import com.agora.cordova.plugin.webrtc.services.MediaDevice;
import com.agora.cordova.plugin.webrtc.services.SettingsContentObserver;

public class AudioPlayer implements SettingsContentObserver.VolumeChangeListener, Player {

    Supervisor supervisor;
    String id;

    public AudioPlayer(Supervisor supervisor, String id) {
        this.supervisor = supervisor;
        this.id = id;
    }

    public void init() {
        SettingsContentObserver.getSettingsContentObserver().registerVolumeChangeListener(this);
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

    }
}
