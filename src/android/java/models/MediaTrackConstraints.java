package com.agora.cordova.plugin.webrtc.models;

public class MediaTrackConstraints extends MediaTrackConstraintSet {

    public MediaTrackConstraintSet[] advanced;
    public Mandatory mandatory;
    public Optional[] optional;

    public static class Mandatory {
        public String deviceId;
    }

    public static class Optional {
        public boolean facingMode;
        public int minWidth;
        public int maxWidth;
        public int minHeight;
        public int maxHeight;
        public int minFrameRate;
        public int maxFrameRate;

        public int minChannelCount;
        public int maxChannelCount;
        public int minSampleRate;
        public int maxSampleRate;
        public boolean googAutoGainControl;
        public boolean googAutoGainControl2;
        public boolean echoCancellation;
        public boolean googNoiseSuppression;
        public String sourceId;
    }
}
