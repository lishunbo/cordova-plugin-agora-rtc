# cordova-plugin-agora-android-rtc

采用Cordova混合开发框架，为安卓平台提供一份Agora SDK NG 4.1.0的移动端定制版，以便于用户基于Agora API开发移动应用。

同时为WebView提供一系列webrtc相关的API和视频相关的控制接口，以便于用户基于WebView开发具有原生能力的P2P应用。

Cordova安卓平台依赖见[英文网站](https://cordova.apache.org/docs/en/latest/guide/platforms/android/)
Cordova安卓平台依赖[伪中方网站](https://cordova.apache.org/docs/zh-cn/latest/guide/platforms/android/)

``` shell
# 默认开发本地有完整的Cordova安卓平台开发环境
# 拉取插件代码(由于当前仓库不支持http/https下载，只能先clone到本地再添加到项目)
git clone ssh://git@git.agoralab.co/webrtc/io-agora-rtcn-native.git somedir
# 切换到Cordova工程目录
cordova plugin add /path/to/somedir
# 本地工程支持安卓平台（如果没有安卓平台需要执行）
cordova platform add android
# 编译工程
cordova build android
# 模拟器中运行，如需
cordova emulate android
```

---

## 当前版本已支持的Agora功能

- 一对一/一对多音视频互动
- 视频编码支持VP8、H.264
- 对本地/远端音频静音/取消静音
- 设置本地视频参数
- 开启/关闭上传视频
- 切换本地相机
- 本地视频开启大小流
- 本地手机桌面共享

支持的Agora SDK NG的API见[详细文档](./Supported_Agora_API.md)

支持的WebRTC API见[详细文档](./Supported_WebRTC_API.md)

---

## 暂不支持功能

- API接口不支持的功能
- x64设备
- 一些已知bug
- 其它未知问题

---

## 使用时的注意事项

- 插件平台正常工作之后才能调用WebRTC相关功能之前，即收到onDeviceReady事件
- WebRTC API中列出支持的类只能插件内部使用，不能与webview互通
- 不建议使用MediaStream对象，目前只作为填充接口使用，仅用于传递MediaStreamTrack
- 谨慎使用同步返回的接口，大多数接口属于异步接口，直接使用返回值可能得到空值
- 有些接口是会Exception的，需要用户catch之后正确处理，RTC类型的异常未返回
- 枚举类型入参未校验，使用时需要注意
- SDP格式仅支持Plan B，不支持Unified Plan
- 如果WebView中需要使用http/websocket，请在AndroidManifest. xml中application段中添加android:usesCleartextTraffic="true"
- DataChannel不支持直接发送Blob，需要获取内容后发送(如String， ArrayBuffer)，注意单次发送数据大小不要超过max-message-size(默认256KB, 以SDP中指定大小为准)，实测ordered情况下1400较快

---

## 样例

样例参考[Agora SDK NG Demo](https://agoraio-community.github.io/AgoraWebSDK-NG/demo/)

注意页面中不要再引用"AgoraRTC_N.js"
