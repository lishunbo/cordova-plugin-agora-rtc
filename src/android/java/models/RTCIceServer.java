package com.agora.cordova.plugin.webrtc.model;

import com.agora.cordova.plugin.webrtc.enums.RTCIceCredentialType;
import com.agora.cordova.plugin.webrtc.interfaces.CredentialDetail;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class RTCIceServer {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "credentialType")
    CredentialDetail credential;

    RTCIceCredentialType credentialType;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    String[] urls;
    String username;

}
