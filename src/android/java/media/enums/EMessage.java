package io.agora.rtcn.media.enums;

public enum EMessage {
    ENOSCREENPERMISSION("ENOSCREENPERMISSION"),
    EMAX("EMAX");

    private String name;

    EMessage(String val) {
        this.name = val;
    }

    public String toString() {
        return this.name;
    }
}
