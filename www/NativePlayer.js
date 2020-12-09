
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
    onVolumeChange: "onVolumeChange",
    onAudioLevel: "onAudioLevel",
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
        console.log("VideoPlayer Event: " + JSON.stringify(ev));
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

class AudioPlayer {
    constructor(track) {
        this.id = uuidv4();
        console.log("construct interface in plugin AudioPlayer", track)
        this.audioTrack = track;
        this.maxVolume = 0;
        this.minVolume = 0;
        this.volume = 0;

        this.onVolumeChange = null;
        this.onAudioLevel = null;

        var self = this;
        cordova.exec(function (ev) {
            self.cordovaEventHandler(ev);
        }, function (ev) {
            console.log("Failed create AudioPlayer object");
        }, PlayerService, 'createAudioPlayer', [this.id, track.id]);
    }

    play() {
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                resolve(ev)
            }, function (ev) {
                reject(ev)
                console.log("Failed AudioPlayer play");
            }, PlayerService, 'playAudioPlayer', [this.id]);
        });
    }
    pause() {
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                resolve(ev)
            }, function (ev) {
                reject(ev)
                console.log("Failed AudioPlayer pause");
            }, PlayerService, 'pauseAudioPlayer', [this.id]);
        });
    }

    destroy() {
        cordova.exec(function (ev) {
            resolve(ev)
        }, function (ev) {
            reject(ev)
            console.log("Failed AudioPlayer destroy");
        }, PlayerService, 'destroyAudioPlayer', [this.id]);
    }

    updateTrack(track) {
        cordova.exec(function (ev) {
            self.cordovaEventHandler(ev);
        }, function (ev) {
            console.log("Failed create AudioPlayer object");
        }, PlayerService, 'updateTrackAudioPlayer', [this.id, track.id]);
    }

    getVolumeRange() {
        return new Promise((resolve, reject) => {
            var that = this;
            cordova.exec(function (ev) {
                var range = JSON.parse(ev)
                that.minVolume = range.min;
                that.maxVolume = range.max;
                resolve(range);
            }, function (ev) {
                console.log("Failed AudioPlayer getVolumeRange object");
                if (reject != null) {
                    reject(ev)
                }
            }, PlayerService, 'getVolumeRange', [this.id]);
        })
    }
    getVolume() {
        return this.volume
    }
    setVolume(volume) {
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                resolve(ev);
            }, function (ev) {
                console.log("Failed AudioPlayer setVolume object");
                if (reject != null) {
                    reject(ev)
                }
            }, PlayerService, 'AudioPlayer_setVolume', [this.id, volume]);
        })
    }
    setSinkID(trackId, deviceId) {
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                resolve(ev);
            }, function (ev) {
                console.log("Failed AudioPlayer setSinkID object");
                if (reject != null) {
                    reject(ev)
                }
            }, PlayerService, 'AudioPlayer_setSinkId', [this.id, trackId, deviceId]);
        })
    }

    cordovaEventHandler(ev) {
        // console.log("AudioPlayer Event: " + JSON.stringify(ev));
        switch (ev.event) {
            case EventType.onVolumeChange:
                this.volume = parseInt(ev.payload);
                if (this.onVolumeChange != null) {
                    this.onVolumeChange(this.volume);
                }
                break;
            case EventType.onAudioLevel:
                // console.log("audio level: " + ev.payload)
                if (this.onAudioLevel != null) {
                    this.onAudioLevel(parseFloat(ev.payload))
                }
                break;
            case EventType.dispose:
                this.audioTrack = null;
                break;
            default:
                console.log("not implement AudioPlayer eventhandler function " + ev.event);
        }
    }
}

function getNativeLowResolutionVideoTrack(track, width, heigth, framerate) {
    return new Promise((resolve, reject) => {
        var args = {}
        args.video = {
            width: width,
            height: heigth,
            frameRate: framerate,
        }
        cordova.exec(function (ev) {
            var tracks = JSON.parse(ev);
            resolve(new Stream.MediaStreamTrack(tracks.kind, tracks.id))
        }, function (ev) {
            console.log("Failed to getNativeLowResolutionVideoTrack");
        }, 'Hook', 'getNativeLowResolutionVideoTrack', [args, track.id]);
    })
}

/**
 * @module NativeVideoPlayer
 */
module.exports = {
    VideoPlayStatus,
    VideoPlayer,
    AudioPlayer,
    getNativeLowResolutionVideoTrack,
}
console.log("player.js onloaded");