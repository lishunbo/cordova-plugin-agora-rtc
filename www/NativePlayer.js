
console.log("player.js onloading");

var { uuidv4 } = require('./util');

let VideoPlayStatus = {
    NONE: "none",
    PLAYING: "playing",
    PAUSE: "pause",
}

var EventType = {
    onFirstFrameDecoded: "onFirstFrameDecoded",
}

var VideoPlayerService = "VideoPlayer";

class PlayEvent {
    constructor(resolve, reject, call, args) {
        this.resolve = resolve;
        this.reject = reject;
        this.call = call;
        this.args = args;
    }
    play() {
        this.call(this.resolve, this.reject, this.args);
    }
}

class VideoPlayer {
    constructor(config) {
        this.id = uuidv4();
        console.log("construct interface in plugin VideoControl", config)
        this.onFirstVideoFrameDecoded = null;
        this.isViewCreated = false;
        this.playEvent = null;


        this.config = config === null ? { trackId: "", mirror: false, fit: "fill" } : config;

        var self = this;
        cordova.exec(function (ev) {
            self.cordovaEventHandler(ev);
        }, function (ev) {
            console.log("Failed create VideoPlayer object");
        }, VideoPlayerService, 'createInstance', [this.id, this.config]);
    }

    udpateConfig(config) {
        console.log("udpateConfig interface in plugin VideoControl", config)

        this.config = config === null ? this.config : config;
        cordova.exec(function (ev) {
        }, function (ev) {
            console.log("Failed VideoPlayer udpateConfig");
        }, VideoPlayerService, 'udpateConfig', [this.id, this.config.trackId, this.config.mirror, this.config.fit]);
    }


    play() {
        return new Promise((resolve, reject) => {
            var playFunc = function (resolve, reject, id) {
                cordova.exec(function (ev) {
                    resolve(ev)
                }, function (ev) {
                    reject(ev)
                    console.log("Failed VideoPlayer play");
                }, VideoPlayerService, 'play', [id]);
            }
            if (!this.isViewCreated) {
                this.playEvent = new PlayEvent(resolve, reject, playFunc, this.id)
            } else {
                playFunc(resolve, reject, this.id);
            }

        });
    }
    pause() {
        cordova.exec(function (ev) {
            resolve(ev)
        }, function (ev) {
            reject(ev)
            console.log("Failed VideoPlayer pause");
        }, VideoPlayerService, 'pause', [this.id]);
    }
    destroy() {

    }

    getCurrentFrame() {
        return new Promise((resolve, reject) => {

        });
    }

    updateVideoTrack(track) {
        console.log("updateVideoTrack interface in plugin VideoControl", track)
        cordova.exec(function (ev) {
        }, function (ev) {
            console.log("Failed VideoPlayer pause");
        }, VideoPlayerService, 'aaa', [this.id, track.id, track.kind]);
    }

    getWindowAttribute() {
        return new Promise((resolve, reject) => {
            console.log("getWindowAttribute interface in plugin VideoControl")
            cordova.exec(function (ev) {
                console.log("getWindowAttribute interface in plugin VideoControl")
                resolve(JSON.parse(ev))
            }, function (ev) {
                reject(ev)
                console.log("Failed VideoPlayer pause");
            }, VideoPlayerService, 'getWindowAttribute', [this.id]);

        });
    }
    setViewAttribute(width, height, x, y) {
        console.log("setViewAttribute interface in plugin VideoControl")
        var that = this;
        cordova.exec(function (ev) {
            that.isViewCreated = true;
            if (that.playEvent) {
                that.playEvent.play();
                that.playEvent = null;
            }
        }, function (ev) {
            console.log("Failed VideoPlayer pause");
        }, VideoPlayerService, 'setViewAttribute', [this.id, width, height, x, y]);
    }

    cordovaEventHandler(ev) {
        console.log("PeerConnection Event: " + JSON.stringify(ev));
        switch (ev.event) {
            case EventType.onFirstFrameDecoded:
                // console.log("got event " + EventType.onIceCandidate);
                if (this.onFirstFrameDecoded != null) {
                    this.onFirstFrameDecoded();
                } else {
                    console.log("not found VideoPlayer.onFirstFrameDecoded function");
                }
                break;
            default:
                console.log("not implement VideoPlayer eventhandler function " + ev.event);
        }
    }
}

/**
 * @module NativeVideoPlayer
 */
module.exports = {
    VideoPlayStatus,
    VideoPlayer,
}
console.log("player.js onloaded");