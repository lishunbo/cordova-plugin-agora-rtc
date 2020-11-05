
var { uuidv4 } = require('./util');


class MediaStream {

    constructor() {
        this.active = true;
        this.id = uuidv4();
        console.log("new MediaStreamTrack:" + this.id);

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
    removeTrack() { }
    //addEventListener
    //removeEventListener
}

class MediaStreamTrack {
    constructor(track) {
        console.log("new MediaStreamTrack");
        this.contentHint = "";
        this.enabled = true;
        this.id = uuidv4();
        this.kind = track;
        this.label = track;
        this.muted = false;

        this.onended = null;
        this.onmute = null;
        this.onunmute = null;
        this.readyState = "live";

        this.eventhandle = new Map();
        // this.enabled = false;
        // this.id = "";
        // this.isolated = false;
        // this.kind = "";
        // this.label = "";
        // this.muted = "";
        // this.onended = null;// ((this: MediaStreamTrack, ev: Event) => any) | null;
        // this.onisolationchange = null;// ((this: MediaStreamTrack, ev: Event) => any) | null;
        // this.onmute = null;// ((this: MediaStreamTrack, ev: Event) => any) | null;
        // this.onunmute = null;// ((this: MediaStreamTrack, ev: Event) => any) | null;
        // this.readyState = ""// MediaStreamTrackState;
        // this.applyConstraints = function (constraints) { return new Promise(() => { }); }
        // this.clone() = function () { return new MediaStreamTrack(); }
        // this.getCapabilities() = function () { }
        // this.getConstraints() = function () { }
        // this.getSettings() = function () { }
        // this.stop() = function () { }
        // addEventListener<K extends keyof MediaStreamTrackEventMap>(type: K, listener: (this: MediaStreamTrack, ev: MediaStreamTrackEventMap[K]) => any, options?: boolean | AddEventListenerOptions): void;
        // addEventListener(type: string, listener: EventListenerOrEventListenerObject, options?: boolean | AddEventListenerOptions): void;
        // removeEventListener<K extends keyof MediaStreamTrackEventMap>(type: K, listener: (this: MediaStreamTrack, ev: MediaStreamTrackEventMap[K]) => any, options?: boolean | EventListenerOptions): void;
        // removeEventListener(type: string, listener: EventListenerOrEventListenerObject, options?: boolean | EventListenerOptions): void;
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

class MediaStreamAudioSourceNode {
    constructor(stream) {
    }
    connect() { }
    disconnect() { }
}

class AudioContext {
    constructor() {
        this.destination = null;
        this.state = "running";
        this.currentTime = null;
    }
    createMediaStreamSource(stream) { return new MediaStreamAudioSourceNode(stream); }
    createGain() { return new MediaStreamAudioSourceNode(); }
    createAnalyser() { }
    createScriptProcessor() { }
    createMediaStreamDestination() { }
    resume() { }
}

class AudioTrackSource {
    constructor() {
        console.log("++++++++++++++++++++ create plugin AudioTrackSource")
        this.outputTrack = null;
        this.outputNode = new MediaStreamAudioSourceNode();
    }
    setVolume() { }
    createOutputTrack() { return null; }
    getAudioLeve() { return 0 }
    removeAllListeners() { }
    stopGetAudioBuffer() { }
    startGetAudioBuffer() { }
    on() { }
    play() { }
    stop() { }
    destory() { }
    updateTrack() { }
}

cordova.addConstructor(function () {
    window.MediaStream = MediaStream;
    window.MediaStreamTrack = MediaStreamTrack;
    window.AudioContext = AudioContext;
    AgoraRTC.AudioTrackSource = AudioTrackSource;
    window.MediaStreamAudioSourceNode = MediaStreamAudioSourceNode
    return window.MediaStreamTrack;
});

// class MediaDeviceInfo {
//     constructor() {
//         this.deviceId = "";
//         this.groupId = "";
//         this.Kind = Media.audioinput;
//         this.label = "";
//     }
//     toJson() {
//         return JSON.stringify(this);
//     }
// }

/**
 * @module Stream
 */
module.exports = {
    MediaStream,
    MediaStreamTrack,
    // MediaDeviceInfo,
}