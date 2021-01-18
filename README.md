# cordova-plugin-agora-android-rtc

本项目旨在为Web开发人员提供一套插件，使其安卓平台的WebView应用具有基于WebRTC API的多媒体互动能力。

本插件基于[Apache Cordova](https://cordova.apache.org/)（简称cordova）混合开发框架实现，该框架提供了一套代码运行在多个平台的能力（windows、安卓、iOS平台），通过该框架可以使Web侧通过事件异步驱动平台侧代码，本插件在此框架基础上，为安卓平台的WebView提供WebRTC API接口（默认情况下WebView不具有WebRTC API接口），使安卓端的WebView应用具有多媒体处理能力，并能够充分利用系统原生、硬件资源，最终开发出高性能、低功耗的音视频互动应用。

目前安卓不支持WebView的video标签直接在原生侧直接渲染，所以当前视频渲染采用悬浮窗的方式，可以通过插件提供的自定义接口设置悬浮窗的位置和大小

---

## 开发环境依赖

在使用本插件之前，需要安装cordova框架的运行环境和满足安卓平台的一些系统依赖，以下为必需依赖，具体见其[官方安卓平台开发指导网站](https://cordova.apache.org/docs/en/latest/guide/platforms/android/)

- [JDK 8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
- [Gradle 6.4.0](https://gradle.org/install/)
- [Android SDK](https://developer.android.com/studio/index.html)

---

## 开发工作流

在选择安卓平台开发流程时，可以直接使用cordova cli工具链，也可以直接使用cordova-android+plugman的方式

### Cordova CLI + Android studio

以cordova cli创建工程，用户的Web源码目录在<工程目录>/wwww中，
cordova build之后会将源码是放到<工程目录>/platforms/android/app/src/main/assets/www，最终加载在应用里的Web代码是这里的

在使用Android Studio调试时，需要打开目录<工程目录>/patforms/android，等待gradle sync完成即可

注意每次执行cordova build都会覆盖assets目录，在使用AS调试assets/www目录时，一定要做好与源码目录的同步和备份

``` shell
# 安装 cordova cli工具
npm install -g cordova
# 创建一个安卓app工程
cordova create hello com.agora.demo.hello Hello
# 切换到cordova工程目录
cd hello
# 添加插件代码到工程
cordova plugin add https://github.com/lishunbo/cordova-plugin-agora-rtc.git
# 工程支持安卓平台
cordova platform add android
# 编译工程（如果工程中只有一个安卓平台，直接cordova build即可）
cordova build android
# 模拟器中运行，如需（如果工程中只有一个安卓平台，直接cordova emulate即可）
cordova emulate android
```

### Cordova-android + plugman + Android Studio

以cordova-android create的方式，创建的工程目录就是android studio的工程目录，直接用AS打开这里即可

用户的Web源码在app/src/main/assets/www目录下，直接使用AS调试即可

``` shell
# 安装 cordova-android 工具
npm install -g cordova-android  plugman
# 创建一个安卓app，以cordova-android为例
create world com.agora.demo.world World
# 添加插件代码到工程
plugman install --platform android --project . --plugin https://github.com/lishunbo/cordova-plugin-agora-rtc.git
```

---

## 当前版本插件已支持的功能

WebRTC API列表摘自MDN和lib.dom.d.ts，支持的WebRTC API见[详细文档](./Supported_WebRTC_API.md)

支持的视频编码格式（不支持x64类型cpu）

- VP8
- VP9
- H264

支持的音频编码格式

- OPUS

---

### 视频窗口设置样例

``` js
    navigator.mediaDevices.getUserMedia({ video: true })
        .then(stream => {
            stream.getTracks().forEach(track => {
                if (track.kind === "video") {
                    // 使用插件提供的自定义API指定渲染窗口大小及位置
                    var player = new VideoPlayer()
                    player.updateVideoTrack(track)
                    player.getWindowAttribute().then(attr => {
                        log("获取桌面大小", attr)
                        player.setViewAttribute(400, 300, 0, 0)
                    })
                    player.play().then(() => {
                    }).catch(e => {
                    })
                }
            })

```

## 使用时的注意事项

- 插件平台正常工作之后才能调用WebRTC相关功能之前，即收到onDeviceReady事件
- WebRTC API中列出支持的类只能插件内部使用，不能与webview互通
- 悬浮窗只支持拖动，其它自定义交互功能暂无
- 不建议使用MediaStream对象，目前只作为填充接口使用，仅用于传递MediaStreamTrack
- 谨慎使用同步返回的接口，大多数接口属于异步接口，直接使用返回值可能得到空值
- 有些接口是会Exception的，需要用户catch之后正确处理，返回的异常类型目前不包含RTCError
- 枚举类型入参未校验，使用时需要注意
- SDP格式仅支持Plan B，不支持Unified Plan
- 如果WebView中需要使用http/websocket，请在AndroidManifest.xml application段中添加android:usesCleartextTraffic="true"
- DataChannel不支持直接发送Blob，需要获取内容后发送(如String， ArrayBuffer)
- DataChannel注意单次发送数据大小不要超过max-message-size(默认256KB, 以SDP中指定大小为准)，实测ordered情况下1400左右较快，为方便接收端ArrayBuffer转Data URI建议采用3的整数倍，以便多次拼接
- DataChannel异步发送数据，即浏览器会缓存部分数据，如果调用发送接口太快，会导致DataChannel关闭
- 其它bug

---

## 样例

参考[WebRTC 样例](https://webrtc.github.io/samples/)
