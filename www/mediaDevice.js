

console.log("mediaDevice.js onloading");

var Media = require('./Media');

class MediaStreamTrack {
    constructor() {
        this.enabled = false;
        this.id = "";
        this.isolated = false;
        this.kind = "";
        this.label = "";
        this.muted = "";
        this.onended = null;// ((this: MediaStreamTrack, ev: Event) => any) | null;
        this.onisolationchange = null;// ((this: MediaStreamTrack, ev: Event) => any) | null;
        this.onmute = null;// ((this: MediaStreamTrack, ev: Event) => any) | null;
        this.onunmute = null;// ((this: MediaStreamTrack, ev: Event) => any) | null;
        this.readyState = ""// MediaStreamTrackState;
        this.applyConstraints = function (constraints) { return new Promise(() => { }); }
        this.clone() = function () { return new MediaStreamTrack(); }
        this.getCapabilities() = function () { }
        this.getConstraints() = function () { }
        this.getSettings() = function () { }
        this.stop() = function () { }
        // addEventListener<K extends keyof MediaStreamTrackEventMap>(type: K, listener: (this: MediaStreamTrack, ev: MediaStreamTrackEventMap[K]) => any, options?: boolean | AddEventListenerOptions): void;
        // addEventListener(type: string, listener: EventListenerOrEventListenerObject, options?: boolean | AddEventListenerOptions): void;
        // removeEventListener<K extends keyof MediaStreamTrackEventMap>(type: K, listener: (this: MediaStreamTrack, ev: MediaStreamTrackEventMap[K]) => any, options?: boolean | EventListenerOptions): void;
        // removeEventListener(type: string, listener: EventListenerOrEventListenerObject, options?: boolean | EventListenerOptions): void;
    }
}

class MediaStream {

    constructor() {
        this.active = false;
        this.id = "";

        this.onaddtrack = null;
        this.onremovetrack = null;

        this.track = null;
    }

    addTrack(track) { this.track.push(track); }
    clone() { return new MediaStream() }

    getAudioTracks() { }
    getTrackById(trackId) { }
    getTracks() { }
    getVideoTracks() { }
    removeTrack() { }
    //addEventListener
    //removeEventListener
}

class MediaDeviceInfo {
    constructor() {
        this.deviceId = "";
        this.groupId = "";
        this.Kind = Media.audioinput;
        this.label = "";
    }
    toJson() {
        return JSON.stringify(this);
    }
}


var mediaDevice = {}

// first class
mediaDevice.getUserMedia = function (config) {
    return new Promise((resolve, reject) => {
        var args = {}
        if (config.video !== undefined) {
            if (typeof config.video === 'boolean') {
                args.video = config.video.toString();
            }
        }
        if (config.audio !== undefined) {
            if (typeof config.audio === 'boolean') {
                args.audio = config.video.toString();
            }
        }

        cordova.exec(function (ev) {
            console.log("Got one stream object with id: " + ev);
            // pc.id = ev
            // console.log("check this.id done:" + pc.id)
            var stream = new MediaStream();
            var track = new MediaStreamTrack();
            stream.addTrack(track);
            resolve(stream)
        }, function (ev) {
            console.log("Failed to create RTCPeerConnection object");
        }, 'Hook', 'getUserMedia', [this.id, args]);

    })
}

// first class
mediaDevice.enumerateDevices = function () {
    return new Promise((resolve, reject) => {
        resolve(new MediaDeviceInfo());
    });
}

module.exports = mediaDevice;

console.log("mediaDevice.js onloaded");
