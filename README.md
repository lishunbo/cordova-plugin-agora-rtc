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

---

## 样例

样例参考[Agora SDK NG Demo](https://agoraio-community.github.io/AgoraWebSDK-NG/demo/)

注意页面中不要再引用"AgoraRTC_N.js"
