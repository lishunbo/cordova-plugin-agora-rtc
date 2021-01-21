package io.agora.rtc.webrtc.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

public class MediaTrackConstraintSet {
    public static class ParamDoubleRange {
        public Double mean;
        public Double exact;
        public Double ideal;
        public Double max;
        public Double min;
    }

    public static class ParamULongRange {
        public Long mean;
        public Long exact;
        public Long ideal;
        public Long max;
        public Long min;
    }

    public static class ParamBooleanSet {
        public Boolean mean;
        public Boolean exact;
        public Boolean ideal;
    }

    public static class ParamStringSet {
        public String mean;
        public String exact;
        public String ideal;
    }

    @JsonDeserialize(using = DoubleDeserializer.class)
    public ParamDoubleRange aspectRatio;
    @JsonDeserialize(using = BooleanDeserializer.class)
    public ParamBooleanSet autoGainControl;
    @JsonDeserialize(using = LongDeserializer.class)
    public ParamULongRange channelCount;
    @JsonDeserialize(using = StringSetDeserializer.class)
    public ParamStringSet deviceId;
    @JsonDeserialize(using = BooleanDeserializer.class)
    public ParamBooleanSet echoCancellation;
    @JsonDeserialize(using = StringSetDeserializer.class)
    public ParamStringSet facingMode;
    @JsonDeserialize(using = DoubleDeserializer.class)
    public ParamDoubleRange frameRate;
    @JsonDeserialize(using = StringSetDeserializer.class)
    public ParamStringSet groupId;
    @JsonDeserialize(using = LongDeserializer.class)
    public ParamULongRange height;
    @JsonDeserialize(using = DoubleDeserializer.class)
    public ParamDoubleRange latency;
    @JsonDeserialize(using = BooleanDeserializer.class)
    public ParamBooleanSet noiseSuppression;
    @JsonDeserialize(using = StringSetDeserializer.class)
    public ParamStringSet resizeMode;
    @JsonDeserialize(using = LongDeserializer.class)
    public ParamULongRange sampleRate;
    @JsonDeserialize(using = LongDeserializer.class)
    public ParamULongRange sampleSize;
    @JsonDeserialize(using = LongDeserializer.class)
    public ParamULongRange width;

    public boolean googAutoGainControl;
    public boolean googAutoGainControl2;
    public boolean googNoiseSuppression;

    public static class DoubleDeserializer extends JsonDeserializer<ParamDoubleRange> {
        @Override
        public ParamDoubleRange deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken jsonToken = p.getCurrentToken();
            if (jsonToken == JsonToken.VALUE_NUMBER_FLOAT) {
                ParamDoubleRange d = new ParamDoubleRange();
                d.mean = p.getValueAsDouble();
                return d;
            } else if (jsonToken == JsonToken.VALUE_NUMBER_INT) {
                ParamDoubleRange d = new ParamDoubleRange();
                d.mean = (double) p.getValueAsLong();
                return d;
            } else if (jsonToken == JsonToken.START_OBJECT) {
                return p.readValueAs(ParamDoubleRange.class);
            }

            return null;
        }
    }

    public static class LongDeserializer extends JsonDeserializer<ParamULongRange> {
        @Override
        public ParamULongRange deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken jsonToken = p.getCurrentToken();
            if (jsonToken == JsonToken.VALUE_NUMBER_INT) {
                ParamULongRange d = new ParamULongRange();
                d.mean = p.getValueAsLong();
                return d;
            } else if (jsonToken == JsonToken.START_OBJECT) {
                return p.readValueAs(ParamULongRange.class);
            }

            return null;
        }
    }

    public static class BooleanDeserializer extends JsonDeserializer<ParamBooleanSet> {
        @Override
        public ParamBooleanSet deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken jsonToken = p.getCurrentToken();
            if (jsonToken == JsonToken.VALUE_FALSE) {
                ParamBooleanSet d = new ParamBooleanSet();
                d.mean = false;
                return d;
            } else if (jsonToken == JsonToken.VALUE_TRUE) {
                ParamBooleanSet d = new ParamBooleanSet();
                d.mean = true;
                return d;
            } else if (jsonToken == JsonToken.START_OBJECT) {
                return p.readValueAs(ParamBooleanSet.class);
            }

            return null;
        }
    }

    public static class StringSetDeserializer extends JsonDeserializer<ParamStringSet> {
        @Override
        public ParamStringSet deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken jsonToken = p.getCurrentToken();
            if (jsonToken == JsonToken.VALUE_STRING) {
                ParamStringSet d = new ParamStringSet();
                d.mean = p.getValueAsString();
                return d;
            } else if (jsonToken == JsonToken.START_OBJECT) {
                return p.readValueAs(ParamStringSet.class);
            } else if (jsonToken == JsonToken.START_ARRAY) {
                return p.readValueAs(ParamStringSet.class);
            }

            return null;
        }
    }
}