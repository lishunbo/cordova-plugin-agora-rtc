package io.agora.rtcn.player.interfaces;

public interface Player {
    void onActivityPause();

    void onActivityResume();

    void dispose();
}
