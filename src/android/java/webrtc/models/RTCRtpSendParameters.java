package io.agora.rtcn.webrtc.models;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.webrtc.MediaStreamTrack;
import org.webrtc.RtpParameters;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RTCRtpSendParameters extends RTCRtpParameters {
    public String degradationPreference;
    public List<RTCRtpEncodingParameters> encodings;
    public String priority;
    public String transactionId;

    public static RTCRtpSendParameters fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, RTCRtpSendParameters.class);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "RTCRtpSendParameters parseJson failed: " + e.toString());
        }
        return null;
    }

    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            Log.e(TAG, e.toString());
        }

        return "{}";
    }

    public static JSONObject RtpParametersToString(RtpParameters parameters) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("degradationPreference", parameters.degradationPreference);
            JSONArray codecs = new JSONArray();
            for (RtpParameters.Codec codec : parameters.codecs) {
                JSONObject o = new JSONObject();
                o.put("payloadType", codec.payloadType);
                o.put("name", codec.name);
                o.put("clockRate", codec.clockRate);
                o.put("numChannels", codec.numChannels);
                o.put("payloadType", codec.payloadType);
                for (Map.Entry<String, String> kv : codec.parameters.entrySet()) {
                    o.put(kv.getKey(), kv.getValue());
                }
                codecs.put(o);
            }
            obj.put("codecs", codecs);

            JSONArray encodings = new JSONArray();
            for (RtpParameters.Encoding encoding : parameters.encodings) {
                JSONObject o = new JSONObject();
                o.put("rid", encoding.rid);
                o.put("scaleResolutionDownBy", encoding.scaleResolutionDownBy);
                o.put("maxFramerate", encoding.maxFramerate);
                o.put("maxBitrateBps", encoding.maxBitrateBps);
                o.put("active", encoding.active);
                o.put("minBitrateBps", encoding.minBitrateBps);
                o.put("priority", encoding.bitratePriority);
                encodings.put(o);
            }
            obj.put("encodings", encodings);

            JSONArray extensions = new JSONArray();
            for (RtpParameters.HeaderExtension extension : parameters.getHeaderExtensions()) {
                JSONObject o = new JSONObject();
                o.put("encrypted", extension.getEncrypted());
                o.put("id", extension.getId());
                o.put("uri", extension.getUri());
                extensions.put(o);
            }
            obj.put("headerExtensions", extensions);

            JSONObject rtcp = new JSONObject();
            rtcp.put("cname", parameters.getRtcp().getCname());
            rtcp.put("reducedSize", parameters.getRtcp().getReducedSize());

            obj.put("rtcp", rtcp);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
