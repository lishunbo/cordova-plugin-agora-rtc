# WebRTC API列表

<table>
  <thead>
    <tr>
      <th>接口</th>
      <th>属性/参数</th>
      <th>属性类型</th>
      <th>可用性</th>
      <th>备注</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td rowspan=3>EventTarget</td>
      <td>addEventListener</td>
      <td>方法</td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr>
      <td>dispatchEvent</td>
      <td>方法</td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr>
      <td>removeEventListener</td>
      <td>方法</td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr>
      <td rowspan=5>MediaDevice</td>
      <td><a href='#enumerateDevices'>enumerateDevices</a></td>
      <td>方法</td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>getSupportedConstraints</td><td>方法</td><td style="color:red">×</td><td></td></tr>
    <tr><td><a href='#getUserMedia'> getUserMedia</a></td><td>方法</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>ondevicechange</td><td>回调</td><td style="color:red">×</td><td></td></tr>
    <tr><td>devicechange</td><td>事件</td><td style="color:red">×</td><td></td></tr>
    <tr>
      <td><a name='enumerateDevices'>enumerateDevices</a></td>
      <td><a href='#MediaDeviceInfo'>MediaDeviceInfo</a></td>
      <td>返回值</td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr>
      <td rowspan=2><a name='getUserMedia'>getUserMedia</a></td>
      <td><a href='#MediaStreamConstraints'>constraints</a></td>
      <td>入参</td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr>
      <td><a href='#MediaStream'>MediaStream</a></td>
      <td>返回值</td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr>
      <td rowspan=5><a name='MediaDeviceInfo'>MediaDeviceInfo</a></td>
      <td>deviceId</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>groupId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>kind</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>label</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>toJSON</td><td>方法</td><td style="color:red">×</td><td></td></tr>
    <tr>
      <td rowspan=3><a name='MediaStreamConstraints'>MediaStreamConstraints</a></td>
      <td><a href='#MediaTrackConstraints'>audio</a></td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>peerIdentity</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td><a href='#MediaTrackConstraints'>video</a></td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=1><a name='MediaTrackConstraints'>MediaTrackConstraints</a></td>
      <td><a href='#MediaTrackConstraintSet'>advanced</a></td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr>
      <td rowspan=15><a name='MediaTrackConstraintSet'>MediaTrackConstraintSet</a></td>
      <td>aspectRatio</td><td></td><td style="color:red">×</td><td></td>
    </tr>
    <tr><td>autoGainControl</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>channelCount</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>deviceId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>echoCancellation</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>facingMode</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>frameRate</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>groupId</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>height</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>latency</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>noiseSuppression</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>resizeMode</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>sampleRate</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>sampleSize</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>width</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=13><a name='MediaStream'>MediaStream</a></td>
      <td>active</td><td></td><td style="color:red">×</td><td></td>
    </tr>
    <tr><td>id</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>onaddtrack</td><td>回调</td><td style="color:red">×</td><td></td></tr>
    <tr><td>onremovetrack</td><td>回调</td><td style="color:red">×</td><td></td></tr>
    <tr><td>addTrack</td><td>方法</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>clone</td><td>方法</td><td style="color:red">×</td><td></td></tr>
    <tr><td>getAudioTracks</td><td>方法</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>getTrackById</td><td>方法</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>getTracks</td><td>方法</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>getVideoTracks</td><td>方法</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>removeTrack</td><td>方法</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>addtrack</td><td>事件</td><td style="color:red">×</td><td></td></tr>
    <tr><td>removetrack</td><td>事件</td><td style="color:red">×</td><td></td></tr>
    <tr>
      <td rowspan=21><a name='MediaStreamTrack'>MediaStreamTrack</a></td>
      <td>enabled</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>id</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>isolated</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>kind</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>label</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>muted</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>onended</td><td>回调</td><td style="color:red">×</td><td></td></tr>
    <tr><td>onisolationchange</td><td>回调</td><td style="color:red">×</td><td></td></tr>
    <tr><td>onmute</td><td>回调</td><td style="color:red">×</td><td></td></tr>
    <tr><td>onunmute</td><td>回调</td><td style="color:red">×</td><td></td></tr>
    <tr><td>readyState</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>applyConstraints</td><td>方法</td><td style="color:red">×</td><td></td></tr>
    <tr><td>clone</td><td>方法</td><td style="color:red">×</td><td></td></tr>
    <tr><td>getCapabilities</td><td>方法</td><td style="color:red">×</td><td></td></tr>
    <tr><td>getConstraints</td><td>方法</td><td style="color:red">×</td><td></td></tr>
    <tr><td>getSettings</td><td>方法</td><td style="color:red">×</td><td></td></tr>
    <tr><td>stop</td><td>方法</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>ended</td><td>事件</td><td style="color:red">×</td><td></td></tr>
    <tr><td>isolationchange</td><td>事件</td><td style="color:red">×</td><td></td></tr>
    <tr><td>mute</td><td>事件</td><td style="color:red">×</td><td></td></tr>
    <tr><td>unmute</td><td>事件</td><td style="color:red">×</td><td></td></tr>
    <tr>
      <td rowspan=53><a name='RTCPeerConnection'>RTCPeerConnection</a></td>
      <td>canTrickleIceCandidates</td><td></td><td style="color:red">×</td><td></td>
    </tr>
    <tr><td>connectionState</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>currentLocalDescription</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>currentRemoteDescription</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>iceConnectionState</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>iceGatheringState</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>idpErrorInfo</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>idpLoginUrl</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td><a href='#RTCSessionDescription'>localDescription</a></td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>onconnectionstatechange</td><td>回调</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>ondatachannel</td><td>回调</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>onicecandidate</td><td>回调</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>onicecandidateerror</td><td>回调</td><td style="color:red">×</td><td></td></tr>
    <tr><td>oniceconnectionstatechange</td><td>回调</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>onicegatheringstatechange</td><td>回调</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>onnegotiationneeded</td><td>回调</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>onsignalingstatechange</td><td>回调</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>onstatsended</td><td>回调</td><td style="color:red">×</td><td></td></tr>
    <tr><td>ontrack</td><td>回调</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>peerIdentity</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>pendingLocalDescription</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>pendingRemoteDescription</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td><a href='#RTCSessionDescription'>remoteDescription</a></td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>sctp</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td><a href='#addIceCandidate'>addIceCandidate</a></td><td>方法</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>signalingState</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>addTrack</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>addTransceiver</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>close</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td><a href='#createAnswer'>createAnswer</a></td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td><a href='#createDataChannel'>createDataChannel</a></td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>createOffer</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td><a href='#getConfiguration'>getConfiguration</a></td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>getIdentityAssertion</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td><a href='#RTCRtpReceiver'>getReceivers</a></td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td><a href='#RTCRtpSender'>getSenders</a></td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td><a href='#getStats'>getStats</a></td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td><a href='#getTransceivers'>getTransceivers</a></td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td><a href='#RTCRtpSender'>removeTrack</a></td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td><a href='#RTCConfiguration'>setConfiguration</a></td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>setIdentityProvider</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td><a href='#RTCSessionDescriptionInit'>setLocalDescription</a></td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td><a href='#RTCSessionDescriptionInit'>setRemoteDescription</a></td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>connectionstatechange</td><td>事件</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>datachannel</td><td>事件</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>icecandidate</td><td>事件</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>icecandidateerror</td><td>事件</td><td style="color:red">×</td><td></td></tr>
    <tr><td>iceconnectionstatechange</td><td>事件</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>icegatheringstatechange</td><td>事件</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>negotiationneeded</td><td>事件</td><td style="color:red">×</td><td></td></tr>
    <tr><td>signalingstatechange</td><td>事件</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>statsended</td><td>事件</td><td style="color:red">×</td><td></td></tr>
    <tr><td>track</td><td>事件</td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=3><a name='RTCSessionDescription'>RTCSessionDescription</a></td>
      <td>sdp</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>type</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>toJSON</td><td>方法</td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=2><a name='addIceCandidate'>addIceCandidate</a></td>
      <td><a href='#RTCIceCandidateInit'>candidate</a></td><td>入参</td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td><a href='#RTCIceCandidate'>candidate</a></td><td>入参</td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=4><a name='RTCIceCandidateInit'>RTCIceCandidateInit</a></td>
      <td>candidate</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>sdpMLineIndex</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>sdpMid</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>usernameFragment</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=14><a name='RTCIceCandidate'>RTCIceCandidate</a></td>
      <td>candidate</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>component</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>foundation</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>port</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>priority</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>protocol</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>relatedAddress</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>relatedPort</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>sdpMLineIndex</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>sdpMid</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>tcpType</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>type</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>usernameFragment</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td><a href='#RTCIceCandidateInit'>toJSON</a></td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=2><a name='createAnswer'>createAnswer</a></td>
      <td><a href='#RTCOfferOptions'>options</td><td>入参</td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr>
      <td><a href='#RTCSessionDescriptionInit'>RTCSessionDescriptionInit</td><td>返回值</td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr>
      <td rowspan=2><a name='createDataChannel'>createDataChannel</a></td>
      <td><a href='#RTCDataChannelInit'>dataChannelDict</a></td><td>入参</td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td><a href='#RTCDataChannel'>RTCDataChannel</a></td><td>返回值</td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=2><a name='getStats'>getStats</a></td>
      <td><a href='#MediaStreamTrack'>selector</td><td>入参</td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr>
      <td><a href='#RTCStatsReport'>RTCStatsReport</td><td>返回值</td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr>
      <td rowspan=2><a name='RTCSessionDescriptionInit'>RTCSessionDescriptionInit</a></td>
      <td>sdp</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>type</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=8><a name='RTCRtpReceiver'>RTCRtpReceiver</a></td>
      <td>rtcpTransport</td><td></td><td style="color:red">×</td><td></td>
    </tr>
    <tr><td>track</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>transport</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>getContributingSources</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>getParameters</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>getStats</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>getSynchronizationSources</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>getCapabilities</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr>
      <td rowspan=9><a name='RTCRtpSender'>RTCRtpSender</a></td>
      <td>dtmf</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>rtcpTransport</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>track</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>transport</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>getParameters</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>getStats</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>replaceTrack</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>setParameters</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>setStreams</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr>
      <td rowspan=1><a name='RTCRtpReceiveParameters'>RTCRtpReceiveParameters</a></td>
      <td>encodings</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr>
      <td rowspan=4><a name='RTCRtpSendParameters'>RTCRtpSendParameters</a></td>
      <td>degradationPreference</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>encodings</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>priority</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>transactionId</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr>
      <td rowspan=3><a name='RTCRtpParameters'>RTCRtpParameters</a></td>
      <td>codecs</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>headerExtensions</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>rtcp</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=3><a name='RTCRtpHeaderExtensionParameters'>RTCRtpHeaderExtensionParameters</a></td>
      <td>encrypted</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>id</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>uri</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=2><a name='RTCRtcpParameters'>RTCRtcpParameters</a></td>
      <td>cname</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>reducedSize</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=3><a name='RTCOfferOptions'>RTCOfferOptions</a></td>
      <td>iceRestart</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>offerToReceiveAudio</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>offerToReceiveVideo</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=7><a name='RTCDataChannelInit'>RTCDataChannelInit</a></td>
      <td>id</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>maxPacketLifeTime</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>maxRetransmits</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>negotiated</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>ordered</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>priority</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>protocol</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=24><a name='RTCDataChannel'>RTCDataChannel</a></td>
      <td>binaryType</td><td></td><td style="color:Lime">√</td><td>仅支持arraybuffer</td>
    </tr>
    <tr><td>bufferedAmount</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>bufferedAmountLowThreshold</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>id</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>label</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>maxPacketLifeTime</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>maxRetransmits</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>negotiated</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>onbufferedamountlow</td><td>回调</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>onclose</td><td>回调</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>onerror</td><td>回调</td><td style="color:red">×</td><td></td></tr>
    <tr><td>onmessage</td><td>回调</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>onopen</td><td>回调</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>ordered</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>priority</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>protocol</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>readyState</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>close</td><td>事件</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>send</td><td>事件</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>bufferedamountlow</td>事件<td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>close</td><td>事件</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>error</td><td>事件</td><td style="color:red">×</td><td></td></tr>
    <tr><td>message</td><td>事件</td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>open</td><td>事件</td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=9><a name='RTCConfiguration'>RTCConfiguration</a></td>
      <td>bundlePolicy</td><td></td><td style="color:red">×</td><td></td>
    </tr>
    <tr><td>certificates</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>encodedInsertableStreams</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>iceCandidatePoolSize</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>iceServers</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>iceTransportPolicy</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>peerIdentity</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>rtcpMuxPolicy</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>sdpSemantics</td><td></td><td style="color:red">×</td><td>仅支持plan-b</td></tr>
    <tr>
      <td rowspan=4><a name='RTCIceServer'>RTCIceServer</a></td>
      <td>credential</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>credentialType</td><td></td><td style="color:Lime">√</td><td>仅支持password</td></tr>
    <tr><td>urls</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>username</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=3><a name='RTCStatsReport'>RTCStatsReport</a></td>
      <td>id</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>timestamp</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>type</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=27><a name='RTCIceCandidatePairStats'>RTCIceCandidatePairStats</a></td>
      <td>availableIncomingBitrate</td><td></td><td style="color:red">×</td><td></td>
    </tr>
    <tr><td>availableOutgoingBitrate</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>bytesReceived</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>bytesSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>circuitBrakerTriggerCount</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>consentExpiredTimestamp</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>consentRequestsSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>currentRoundTripTime</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>firstRequestTimestamp</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>lastPacketReceivedTimestamp</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>lastPacketSentTimestamp</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>lastRequestTimestamp</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>lastResponseTimestamp</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>localCandidateId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>nominated</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>packetsReceived</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>packetsSent</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>remoteCandidateId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>requestsReceived</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>requestsSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>responsesReceived</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>responsesSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>retransmissionsReceived</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>retransmissionsSent</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>state</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalRoundTripTime</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>transportId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=3><a name='RTCCertificateStats'>RTCCertificateStats</a></td>
      <td>base64Certificate</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>fingerprint</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>fingerprintAlgorithm</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=5><a name='RTCCodecStats'>RTCCodecStats</a></td>
      <td>payloadType</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>mimeType</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>clockRate</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>channels</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>sdpFmtpLine</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=8><a name='RTCDataChannelStats'>RTCDataChannelStats</a></td>
      <td>bytesReceived</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>bytesSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>dataChannelIdentifier</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>label</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>messagesReceived</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>messagesSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>protocol</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>state</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=50><a name='RTCInboundRtpStreamStats'>RTCInboundRtpStreamStats</a></td>
      <td>audioLevel</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>averageRtcpInterval</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>bytesReceived</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>codecId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>concealmentEvents</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>concealedSamples</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>decoderImplementation</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>estimatedPlayoutTimestamp</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>fecPacketsDiscarded</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>fecPacketsReceived</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>firCount</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>framesDropped</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>frameHeight</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>framesDecoded</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>framesDropped</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>framesPerSecond</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>framesReceived</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>frameWidth</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>headerBytesReceived</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>insertedSamplesForDeceleration</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>isRemote</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>jitter</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>jitterBufferDelay</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>jitterBufferEmittedCount</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>keyFramesDecoded</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>kind</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>lastPacketReceivedTimestamp</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>mediaType</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>nackCount</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>packetsDuplicated</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>packetsFailedDecryption</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>packetsLost</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>packetsReceived</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>perDscpPacketsReceived</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>pliCount</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>qpSum</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>receiverId</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>remotedId</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>removedSamplesForAcceleration</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>slientConcealedSamples</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>sliCount</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>ssrc</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalAudioEnergy</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalDecodedTime</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totaInterFrameDelay</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalSamplesDuration</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalSamplesReceived</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalSquaredInterFrameDelay</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>trackId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>transportId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=10><a name='RTCIceCandidateStats'>RTCIceCandidateStats</a></td>
      <td>ip</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>candidateType</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>deleted</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>networkType</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>port</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>priority</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>protocol</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>relayProtocol</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>transportId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>url</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr>
      <td rowspan=38><a name='RTCOutboundRtpStreamStats'>RTCOutboundRtpStreamStats</a></td>
      <td>averageRtcpInterval</td><td></td><td style="color:red">×</td><td></td>
    </tr>
    <tr><td>bytesSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>codecId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>encoderImplementation</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>firCount</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>framesEncoded</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>frameHeight</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>framesPerSecond</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>framesSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>frameWidth</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>headerBytesSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>hugeFramesSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>isRemote</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>keyFramesEncoded</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>kind</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>lastPacketSentTimestamp</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>mediaType</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>mediaSourceId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>nackCount</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>packetsSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>perDscpPacketsSent</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>pliCount</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>qpSum</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>qualityLimitationDurations</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>qualityLimitationReason</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>qualityLimitationResolutionChanges</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>remoteId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>retransmittedBytesSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>retransmittedPacketsSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>senderId</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>sliCount</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>ssrc</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>targetBitrate</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>totalEncodedBytesTarget</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalEncodedTime</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalPacketSendDelay</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>trackId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>transportId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=22><a name='RTCAudioReceiverStats'>RTCAudioReceiverStats</a></td>
      <td>audioLevel</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>concealedSamples</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>concealmentEvents</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>delayedPacketOutageSamples</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>detached</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>ended</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>insertedSamplesForDeceleration</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>interruptionCount</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>jitterBufferDelay</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>jitterBufferEmittedCount</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>jitterBufferFlushes</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>jitterBufferTargetDelay</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>kind</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>relativePacketArrivalDelay</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>remoteSource</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>removedSamplesForAcceleration</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>slientConcealedSamples</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>trackIdentifier</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalAudioEnergy</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalSamplesReceived</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalSamplesDuration</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalInterruptionDuration</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=18><a name='RTCVideoReceiverStats'>RTCVideoReceiverStats</a></td>
      <td>detached</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>detached</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>frameHeight</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>framesDecoded</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>framesDropped</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>framesReceived</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>frameWidth</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>freezeCount</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>jitterBufferDelay</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>jitterBufferEmittedCount</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>kind</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>pauseCount</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>remoteSource</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>sumOfSquaredFramesDuration</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>trackIdentifier</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalFramesDuration</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalFreezesDuration</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalPausesDuration</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=8><a name='RTCRemoteInboundRtpStreamStats'>RTCRemoteInboundRtpStreamStats</a></td>
      <td>ssrc</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>kind</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>transportId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>codecId</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>packetsLost</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>jitter</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>localId</td><td></td><td style="color:red">×</td><td></td></tr>
    <tr><td>roundTripTime</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=5><a name='RTCAudioSoureStats'>RTCAudioSoureStats</a></td>
      <td>audioLevel</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>kind</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>trackIdentifier</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalAudioEnergy</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>totalSamplesDuration</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=5><a name='RTCVideoSourceStats'>RTCVideoSourceStats</a></td>
      <td>framesPerSecond</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>height</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>kind</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>trackIdentifier</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>width</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=6><a name='RTCAudioSenderStats'>RTCAudioSenderStats</a></td>
      <td>trackIdentifier</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>mediaSourceId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>remoteSource</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>kind</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>ended</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>detached</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=10><a name='RTCVideoSenderStats'>RTCVideoSenderStats</a></td>
      <td>trackIdentifier</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>mediaSourceId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>remoteSource</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>kind</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>ended</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>detached</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>frameWidth</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>frameHeight</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>framesSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>hugeFramesSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr>
      <td rowspan=12><a name='RTCTransportStats'>RTCTransportStats</a></td>
      <td>bytesSent</td><td></td><td style="color:Lime">√</td><td></td>
    </tr>
    <tr><td>bytesReceived</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>dtlsCipher</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>dtlsState</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>localCertificateId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>packetsSent</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>packetsReceived</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>remoteCertificateId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>selectedCandidatePairChanges</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>selectedCandidatePairId</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>srtpCipher</td><td></td><td style="color:Lime">√</td><td></td></tr>
    <tr><td>tlsVersion</td><td></td><td style="color:Lime">√</td><td></td></tr>
  </tbody>
</table>