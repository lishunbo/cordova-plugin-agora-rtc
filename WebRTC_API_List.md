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
  </tbody>
</table>