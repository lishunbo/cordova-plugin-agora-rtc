package io.agora.rtcn.player.interfaces;

import io.agora.rtcn.player.enums.Action;

public interface Supervisor {
    void onObserveEvent(String id, Action action, String message, String usage);
}
