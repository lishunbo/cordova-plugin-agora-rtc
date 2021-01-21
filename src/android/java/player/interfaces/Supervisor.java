package io.agora.rtc.player.interfaces;

import io.agora.rtc.player.enums.Action;

public interface Supervisor {
    void onObserveEvent(String id, Action action, String message, String usage);
}
