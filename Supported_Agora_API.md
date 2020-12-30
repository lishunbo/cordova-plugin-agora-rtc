# 插件支持的Agora SDK NG接口列表

[Ago SDK NG API文档链接](https://agoraio-community.github.io/AgoraWebSDK-NG/api/cn/)

插件支持的API列表如下表：

<table>
  <thead>
    <tr>
      <th>命名空间/对象</th>
      <th>接口</th>
      <th>可用性</th>
      <th>备注</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td rowspan=29>AgoraRTC</td>
      <td>VERSION</td>
      <td style="color:Lime">√</td>
    </tr>
    <tr><td>BUILD</td><td style="color:Lime">√</td></tr>
    <tr><td>setParameter</td><td style="color:Lime">√</td></tr>
    <tr><td>setArea</td><td style="color:Lime">√</td></tr>
    <tr><td>setLogLevel</td><td style="color:Lime">√</td></tr>    <tr><td>enableLogUpload</td><td style="color:Lime">√</td></tr>
    <tr><td>disableLogUpload</td><td style="color:Lime">√</td></tr>
    <tr><td>getSupportedCodec</td><td style="color:Lime">√</td></tr>
    <tr><td>checkSystemRequirements</td><td style="color:Lime">√</td></tr>
    <tr><td>getDevices</td><td style="color:Lime">√</td></tr>
    <tr><td>getMicrophones</td><td style="color:Lime">√</td></tr>
    <tr><td>getCameras</td><td style="color:Lime">√</td></tr>
    <tr><td>getElectronScreenSources</td><td style="color:red">×</td></tr>
    <tr><td>getPlaybackDevices</td><td style="color:Lime">√</td></tr>
    <tr><td>createClient</td><td style="color:Lime">√</td></tr>
    <tr><td>createCameraVideoTrack</td><td style="color:Lime">√</td></tr>
    <tr><td>createCustomVideoTrack</td><td style="color:red">×</td></tr>
    <tr><td>createScreenVideoTrack</td><td style="color:Lime">√</td></tr>
    <tr><td>createMicrophoneAndCameraTracks</td><td style="color:Lime">√</td></tr>
    <tr><td>createMicrophoneAudioTrack</td><td style="color:Lime">√</td></tr>
    <tr><td>createCustomAudioTrack</td><td style="color:red">×</td></tr>
    <tr><td>createBufferSourceAudioTrack</td><td style="color:red">×</td></tr>
    <tr><td>createChannelMediaRelayConfiguration</td><td style="color:red">×</td></tr>
    <tr><td>checkAudioTrackIsActive</td><td style="color:red">×</td></tr>
    <tr><td>checkVideoTrackIsActive</td><td style="color:red">×</td></tr>
    <tr><td>onCameraChanged</td><td style="color:red">×</td></tr>
    <tr><td>onMicrophoneChanged</td><td style="color:red">×</td></tr>
    <tr><td>onPlaybackDeviceChanged</td><td style="color:red">×</td></tr>
    <tr><td>onAudioAutoplayFailed</td><td style="color:red">×</td></tr>
    <tr><td rowspan=59>IAgoraRTCClient</td>
      <td>connectionState</td><td style="color:Lime">√</td></tr>
    <tr><td>remoteUsers</td><td style="color:Lime">√</td></tr>
    <tr><td>localTracks</td><td style="color:Lime">√</td></tr>
    <tr><td>uid</td><td style="color:Lime">√</td></tr>
    <tr><td>channelName</td><td style="color:Lime">√</td></tr>
    <tr><td>on("connection-state-change", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("user-joined", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("user-left", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("user-published", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("user-unpublished", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("user-info-updated", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("media-reconnect-start", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("media-reconnect-end", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("stream-type-changed", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("stream-fallback", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("channel-media-relay-state", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("channel-media-relay-event", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("volume-indicator", function(ev){})</td><td style="color:red">×</td></tr>
    <tr><td>on("crypt-error", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("token-privilege-will-expire", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("token-privilege-did-expire", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("network-quality", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("live-streaming-error", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("live-streaming-warning", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("stream-inject-status", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>on("exception", function(ev){})</td><td style="color:Lime">√</td></tr>
    <tr><td>join</td><td style="color:Lime">√</td></tr>
    <tr><td>leave</td><td style="color:Lime">√</td></tr>
    <tr><td>publish</td><td style="color:Lime">√</td></tr>
    <tr><td>unpublish</td><td style="color:Lime">√</td></tr>
    <tr><td>subscribe</td><td style="color:Lime">√</td></tr>
    <tr><td>setLowStreamParameter</td><td style="color:Lime">√</td></tr>
    <tr><td>enableDualStream</td><td style="color:Lime">√</td></tr>
    <tr><td>disableDualStream</td><td style="color:Lime">√</td></tr>
    <tr><td>setClientRole</td><td style="color:Lime">√</td></tr>
    <tr><td>setProxyServer</td><td style="color:Lime">√</td></tr>
    <tr><td>setTurnServer</td><td style="color:Lime">√</td></tr>
    <tr><td>startProxyServer</td><td style="color:Lime">√</td></tr>
    <tr><td>stopProxyServer</td><td style="color:Lime">√</td></tr>
    <tr><td>setRemoteVideoStreamType</td><td style="color:Lime">√</td></tr>
    <tr><td>setStreamFallbackOption</td><td style="color:Lime">√</td></tr>
    <tr><td>setEncryptionConfig</td><td style="color:Lime">√</td></tr>
    <tr><td>renewToken</td><td style="color:Lime">√</td></tr>
    <tr><td>enableAudioVolumeIndicator</td><td style="color:Lime">√</td></tr>
    <tr><td>getRTCStats</td><td style="color:Lime">√</td></tr>
    <tr><td>setLiveTranscoding</td><td style="color:Lime">√</td></tr>
    <tr><td>startLiveStreaming</td><td style="color:Lime">√</td></tr>
    <tr><td>stopLiveStreaming</td><td style="color:Lime">√</td></tr>
    <tr><td>addInjectStreamUrl</td><td style="color:Lime">√</td></tr>
    <tr><td>removeInjectStreamUrl</td><td style="color:Lime">√</td></tr>
    <tr><td>startChannelMediaRelay</td><td style="color:Lime">√</td></tr>
    <tr><td>updateChannelMediaRelay</td><td style="color:Lime">√</td></tr>
    <tr><td>stopChannelMediaRelay</td><td style="color:Lime">√</td></tr>
    <tr><td>sendCustomReportMessage</td><td style="color:Lime">√</td></tr>
    <tr><td>getLocalAudioStats</td><td style="color:Lime">√</td></tr>
    <tr><td>getRemoteAudioStats</td><td style="color:Lime">√</td></tr>
    <tr><td>getRemoteNetworkQuality</td><td style="color:Lime">√</td></tr>
    <tr><td>getLocalVideoStats</td><td style="color:Lime">√</td></tr>
    <tr><td>getRemoteVideoStats</td><td style="color:Lime">√</td></tr>
    <tr><td rowspan=6>ILocalVideoTrack</td>
      <td>on("beauty-effect-overload", function(ev){})</td><td style="color:red">×</td></tr>
    <tr><td>on("track-ended", function(ev){})</td><td style="color:red">×</td></tr>
    <tr><td>play</td><td style="color:Lime">√</td></tr>
    <tr><td>getStats</td><td style="color:Lime">√</td></tr>
    <tr><td>setBeautyEffect</td><td style="color:Lime">√</td></tr>
    <tr><td>getCurrentFrameData</td><td style="color:Lime">√</td></tr>
    <tr><td rowspan=3>ICameraVideoTrack</td>
      <td>setDevice</td><td style="color:Lime">√</td></tr>
    <tr><td>setEnabled</td><td style="color:Lime">√</td></tr>
    <tr><td>setEncoderConfiguration</td><td style="color:Lime">√</td></tr>
    <tr><td rowspan=6>ILocalAudioTrack</td>
      <td>setVolume</td><td style="color:Lime">√</td></tr>
    <tr><td>getVolumeLevel</td><td style="color:red">×</td></tr>
    <tr><td>setAudioFrameCallback</td><td style="color:red">×</td></tr>
    <tr><td>play</td><td style="color:Lime">√</td></tr>
    <tr><td>setPlaybackDevice</td><td style="color:Lime">√</td></tr>
    <tr><td>getStats</td><td style="color:Lime">√</td></tr>
    <tr><td rowspan=2>IMicrophoneAudioTrack</td>
      <td>setDevice</td><td style="color:red">×</td></tr>
    <tr><td>setEnabled</td><td style="color:Lime">√</td></tr>
    <tr><td rowspan=10>IBufferSourceAudioTrack</td>
      <td>source</td><td style="color:red">×</td></tr>
    <tr><td>currentState</td><td style="color:red">×</td></tr>
    <tr><td>duration</td><td style="color:red">×</td></tr>
    <tr><td>on("source-state-change", function(ev){})</td><td style="color:red">×</td></tr>
    <tr><td>getCurrentTime</td><td style="color:red">×</td></tr>
    <tr><td>startProcessAudioBuffer</td><td style="color:red">×</td></tr>
    <tr><td>pauseProcessAudioBuffer</td><td style="color:red">×</td></tr>
    <tr><td>seekAudioBuffer</td><td style="color:red">×</td></tr>
    <tr><td>resumeProcessAudioBuffer</td><td style="color:red">×</td></tr>
    <tr><td>stopProcessAudioBuffer</td><td style="color:red">×</td></tr>
    <tr><td rowspan=3>IRemoteTrack</td>
      <td>on("first-frame-decoded", function(ev){})</td><td style="color:red">×</td></tr>
    <tr><td>getUserId</td><td style="color:Lime">√</td></tr>
    <tr><td>getStats</td><td style="color:Lime">√</td></tr>
    <tr><td rowspan=3>IRemoteVideoTrack</td>
      <td>getStats</td><td style="color:Lime">√</td></tr>
    <tr><td>play</td><td style="color:Lime">√</td></tr>
    <tr><td>getCurrentFrameData</td><td style="color:red">×</td></tr>
    <tr><td rowspan=6>IRemoteAudioTrack</td>
      <td>getStats</td><td style="color:Lime">√</td></tr>
    <tr><td>play</td><td style="color:Lime">√</td></tr>
    <tr><td>setPlaybackDevice</td><td style="color:Lime">√</td></tr>
    <tr><td>setAudioFrameCallback</td><td style="color:red">×</td></tr>
    <tr><td>setVolume</td><td style="color:Lime">√</td></tr>
    <tr><td>getVolumeLevel</td><td style="color:red">×</td></tr>
    <tr><td rowspan=6>ITrack</td>
      <td>trackMediaType</td><td style="color:Lime">√</td></tr>
    <tr><td>isPlaying</td><td style="color:red">×</td></tr>
    <tr><td>getTrackId</td><td style="color:Lime">√</td></tr>
    <tr><td>getMediaStreamTrack</td><td style="color:Lime">√</td></tr>
    <tr><td>play</td><td style="color:Lime">√</td></tr>
    <tr><td>stop</td><td style="color:Lime">√</td></tr>
    <tr><td rowspan=5>IAgoraRTCRemoteUser</td>
      <td>uid</td><td style="color:Lime">√</td></tr>
    <tr><td>audioTrack</td><td style="color:Lime">√</td></tr>
    <tr><td>videoTrack</td><td style="color:Lime">√</td></tr>
    <tr><td>hasAudio</td><td style="color:Lime">√</td></tr>
    <tr><td>hasVideo</td><td style="color:Lime">√</td></tr>
    <tr><td rowspan=3>IChannelMediaRelayConfiguration</td>
      <td>setSrcChannelInfo</td><td style="color:Lime">√</td></tr>
    <tr><td>addDestChannelInfo</td><td style="color:Lime">√</td></tr>
    <tr><td>removeDestChannelInfo</td><td style="color:Lime">√</td></tr>
  </tbody>
</table>