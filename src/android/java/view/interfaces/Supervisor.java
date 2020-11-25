package com.agora.cordova.plugin.view.interfaces;

import com.agora.cordova.plugin.view.enums.Action;

public interface Supervisor {
    void onObserveEvent(String id, Action action, String message, String usage);
}
