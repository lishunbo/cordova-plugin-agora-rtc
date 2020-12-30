# cordova-plugin-agora-rtcn

采用Cordova混合开发框架，为webview提供一系列webrtc相关的Web API，以便于在移动端
使用标准的WebRTC接口开发应用，具体API支持力度见：
同时内置一份Agora SDK RTCN的定制版，方便终端用户采用Agora API开发基于Agora的应用

## 目前已支持的接口详见demo

## 暂不支持功能

- arm64设备
- 视频窗口大小及位置调整
- 其它未知问题

## 使用时的注意事项

- 插件平台正常工作之后才能调用WebRTC相关功能之前，即收到onDeviceReady事件
- JavaScript部分事件生成的Event对象或子类对象仅包含核心数据
- MediaStream简化为一个MediaStreamTrack的数组
