
console.log("player.js onloading");

var { uuidv4 } = require('./util');
var Stream = require('./Stream');

let VideoPlayStatus = {
    NONE: "none",
    PLAYING: "playing",
    PAUSE: "pause",
}

var EventType = {
    onFirstFrameDecoded: "onFirstFrameDecoded",
    dispose: "dispose",
}

var PlayerService = "NativePlayerHook";

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
        this.videoTrack = null;

        this.config = config === null ? { trackId: "", mirror: false, fit: "fill" } : config;

        var self = this;
        cordova.exec(function (ev) {
            self.cordovaEventHandler(ev);
        }, function (ev) {
            console.log("Failed create VideoPlayer object");
        }, PlayerService, 'createInstance', [this.id, this.config]);
    }

    udpateConfig(config) {
        console.log("udpateConfig interface in plugin VideoControl", config)

        this.config = config === null ? this.config : config;
        cordova.exec(function (ev) {
        }, function (ev) {
            console.log("Failed VideoPlayer udpateConfig");
        }, PlayerService, 'udpateConfig', [this.id, this.config.trackId, this.config.mirror, this.config.fit]);
    }


    play() {
        return new Promise((resolve, reject) => {
            var playFunc = function (resolve, reject, id) {
                cordova.exec(function (ev) {
                    resolve(ev)
                }, function (ev) {
                    reject(ev)
                    console.log("Failed VideoPlayer play");
                }, PlayerService, 'play', [id]);
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
            // resolve(ev)
        }, function (ev) {
            // reject(ev)
            console.log("Failed VideoPlayer pause");
        }, PlayerService, 'pause', [this.id]);
    }
    destroy() {
        console.log("destory interface in plugin VideoControl")
        cordova.exec(function (ev) {
            console.log("destory interface in plugin VideoControl")
            // resolve(JSON.parse(ev))
        }, function (ev) {
            // reject(ev)
            console.log("Failed VideoPlayer destory");
        }, PlayerService, 'destroy', [this.id]);
    }

    getCurrentFrame() {
        return new Promise((resolve, reject) => {

        });
    }

    updateVideoTrack(track) {
        if (this.videoTrack) {
            this.videoTrack.stop();
        }
        this.videoTrack = track;

        cordova.exec(function (ev) {
        }, function (ev) {
            console.log("Failed VideoPlayer updateVideoTrack");
        }, PlayerService, 'updateVideoTrack', [this.id, track.id, track.kind]);
    }

    getWindowAttribute() {
        return new Promise((resolve, reject) => {
            console.log("getWindowAttribute interface in plugin VideoControl")
            cordova.exec(function (ev) {
                console.log("getWindowAttribute interface in plugin VideoControl")
                resolve(JSON.parse(ev))
            }, function (ev) {
                reject(ev)
                console.log("Failed VideoPlayer getWindowAttribute");
            }, PlayerService, 'getWindowAttribute', [this.id]);

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
            console.log("Failed VideoPlayer setViewAttribute");
        }, PlayerService, 'setViewAttribute', [this.id, width, height, x, y]);
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
            case EventType.dispose:
                this.videoTrack = null;
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