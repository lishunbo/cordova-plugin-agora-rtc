# cordova-plugin-agora-android-rtc

采用Cordova混合开发框架，为安卓平台提供一份Agora SDK NG 4.1.0的移动端定制版，以便于用户基于Agora API开发移动应用。

同时为WebView提供一系列webrtc相关的API和视频相关的控制接口，以便于用户基于WebView开发具有原生能力的P2P应用。

``` shell
# 默认开发本地有完整的Cordova&AndroidStudio开发环境
# 切换到Cordova工程目录
# 拉取插件代码
cordova plugin add http://github.com/xxx/cordova-plugin-agora-android-rtc
# 本地工程支持安卓平台（如果没有安卓平台需要执行）
cordova platform add android
# 编译工程
cordova build
```

---

## 当前版本已支持的Agora功能

- 一对一/一对多音视频互动
- 对本地/远端音频静音/取消静音
- 开启/关闭上传视频
- 切换本地相机
- 本地视频开启大小流
- 本地手机桌面共享

支持的Agora SDK NG的API见[详细文档](./Supported_Agora_API.md)

支持的WebRTC API见[详细文档](./Supported_WebRTC_API.md)

---

## 暂不支持功能

- API接口不支持的功能
- arm64设备
- 一些已知bug
- 其它未知问题

---

## 使用时的注意事项

- 插件平台正常工作之后才能调用WebRTC相关功能之前，即收到onDeviceReady事件
- WebRTC API中列出支持的类只能插件内部使用，不能与webview互通

## 样例

样例见其它仓库
