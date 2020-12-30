

console.log("mediaDevice.js onloading");

var { uuidv4 } = require('./Util');

var mediaDevice = {}

var MediaService = "Media";

class MediaStream {

    constructor() {
        this.active = true;
        this.id = uuidv4();

        this.onactive = null;
        this.oninactive = null;
        this.onaddtrack = null;
        this.onremovetrack = null;

        this.track = [];
    }

    addTrack(track) { this.track.push(track); }
    clone() { return new MediaStream() }

    getAudioTracks() {
        var audioTrack = [];

        this.track.forEach(track => {
            if (track.kind == "audio") {
                audioTrack.push(track)
            }
        })

        return audioTrack;
    }
    getTrackById(trackId) {
        this.track.forEach(track => {
            if (track.id == trackId) {
                return track;
            }
        })
        return null;
    }
    getTracks() { return this.track; }
    getVideoTracks() {
        var videoTrack = [];

        this.track.forEach(track => {
            if (track.kind == "video") {
                videoTrack.push(track)
            }
        })

        return videoTrack;
    }
    removeTrack(track) {
        this.track = this.track.filter((value, index, array) => { return value.id !== track.id })
    }
    //addEventListener
    //removeEventListener
}

class MediaStreamTrack {
    constructor(track, id) {
        this.contentHint = "";
        this.enabled = true;
        if (id !== undefined) {
            this.id = id;
        } else {
            this.id = uuidv4();
        }
        this.kind = track;
        this.label = track;
        this.muted = false;

        this.onended = null;
        this.onmute = null;
        this.onunmute = null;
        this.readyState = "live";

        console.log("new MediaStreamTrack " + this.kind + " " + this.id);
        this.eventhandle = new Map();
    }

    stop() {
        cordova.exec(function (ev) {
        }, function (ev) {
            console.log("Failed to stop MediaStreamTrack object");
        }, MediaService, 'stopMediaStreamTrack', [this.id]);
    }

    addEventListener(eventType, func) {
        var queue = this.eventhandle.get(eventType);
        if (queue !== undefined) {
            queue.push(func)
            this.eventhandle.set(eventType, queue);
        } else {
            this.eventhandle.set(eventType, [func]);
        }
    }
    removeEventListener(eventType, func) {
        var queue = this.eventhandle.get(eventType);
        if (queue !== undefined) {
            queue.splice(queue.indexOf(func), 1)
            this.eventhandle.set(eventType, queue);
        }
    }
}


// first class
mediaDevice.getUserMedia = getUserMedia
function getUserMedia(config) {
    return new Promise((resolve, reject) => {
        var args = {}
        if (config.video !== undefined) {
            if (typeof config.video === 'boolean') {
                args.video = {};
            } else if (typeof config.video === 'object' && config.video !== null) {
                args.video = config.video;
            }
        }
        if (config.audio !== undefined) {
            if (typeof config.audio === 'boolean') {
                args.audio = {};
            } else if (typeof config.audio === 'object' && config.audio !== null) {
                args.audio = config.audio;
            }
        }

        cordova.exec(function (ev) {
            if (ev === "OK") {
                return
            }
            var tracks = JSON.parse(ev);
            var stm = new MediaStream();

            tracks.forEach(track => {
                var newTrack = new MediaStreamTrack(track.kind, track.id);
                newTrack.label = "mock track"
                stm.addTrack(newTrack)
            });
            resolve(stm)
        }, function (ev) {
            console.log("Failed to getUserMedia object");
        }, MediaService, 'getUserMedia', [args]);

    })
}

class MediaDeviceInfo {
    constructor(deviceId, groupId, kind, label) {
        this.deviceId = deviceId;
        this.groupId = groupId;
        this.kind = kind;
        this.label = label;
    }
}

// first class
mediaDevice.enumerateDevices = function () {
    return new Promise((resolve, reject) => {
        cordova.exec(function (ev) {
            if (ev === "OK") {
                return
            }
            var infos = JSON.parse(ev);

            let avdevices = [];

            infos.forEach(info => {
                avdevices.push(new MediaDeviceInfo(info.deviceId, info.groupId, info.kind, info.label));
            })
            resolve(avdevices)

        }, function (ev) {
            console.log("Failed to enumerateDevices object");
        }, MediaService, 'enumerateDevices', []);
    });
}

mediaDevice.addEventListener = function (event, func) {
    console.log("mediaDevice.js addEventListener" + event);
}

mediaDevice.getSubVideoTrack = function (track, width, heigth, framerate) {
    return new Promise((resolve, reject) => {
        var args = {}
        args.video = {
            width: width,
            height: heigth,
            frameRate: framerate,
        }
        cordova.exec(function (ev) {
            var subTrack = JSON.parse(ev);
            resolve(new MediaStreamTrack(subTrack.kind, subTrack.id))
        }, function (ev) {
            if (reject) {
                reject(ev)
            }
            console.log("Failed to getSubVideoTrack", ev);
        }, MediaService, 'getSubVideoTrack', [track.id, args]);
    })
}

cordova.addConstructor(function () {
    window.MediaDeviceInfo = MediaDeviceInfo;
    window.MediaStream = MediaStream;
    window.MediaStreamTrack = MediaStreamTrack;
    mediaDevice.getUserMedia = getUserMedia
});

mediaDevice.MediaDeviceInfo = MediaDeviceInfo
mediaDevice.MediaStream = MediaStream
mediaDevice.MediaStreamTrack = MediaStreamTrack

/**
 * @module Media
 */
module.exports = mediaDevice

console.log("mediaDevice.js onloaded");
