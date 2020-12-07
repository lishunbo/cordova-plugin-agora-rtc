

console.log("RTCPeerConnection.js onloading");

var { uuidv4 } = require('./util');
var Stream = require('./Stream');

const EventType = {
    onIceCandidate: "onIceCandidate",
    onICEConnectionStateChange: "onICEConnectionStateChange",
    onConnectionStateChange: "onConnectionStateChange",
    onSignalingStateChange: "onSignalingStateChange",
    onAddTrack: "onAddTrack",
}

class RTCStatsReport {
    constructor(json) {
        this.stats = JSON.parse(json);
    }
    forEach(callbackfn) {
        this.stats.forEach((v, i, a) => {
            callbackfn(v, i, a);
        })
    }
    get(id) {
        return this.stats.find(x => x.id === id);
    }
    values() {
        return this.stats;
    }
}

var MediaService = "Hook"

class RTCRtpSendParameters {
    constructor() {
        this.degradationPreference = null;
        this.encodings = null;
        this.priority = null;
        this.transactionId = null;
    }
}

class RTCRtpSender {
    constructor(parameter, track, pcid, id) {
        if (id) {
            console.log("create by id " + id);
            this.id = id;
        } else {
            this.id = uuidv4();
        }
        this.pcid = pcid;
        if (parameter) {
            this.parameter = parameter
        } else {
            this.parameter = new RTCRtpSendParameters()
        }
        this.track = new Stream.MediaStreamTrack(track)
        this.modified = false
    }
    replaceTrack(track) {
        cordova.exec(function (ev) {
        }, function (ev) {
        }, MediaService, 'replaceTrack', [this.id, this.pcid, track.id, track.kind]);
    }
    getParameters() {
        return this.parameter;
    }
    setParameters(parameters) {
        this.parameter = parameters
        this.modified = true
    }
}

class RTCPeerConnection {
    constructor(config) {
        this.id = uuidv4();
        this.config = config;

        this.localStream = null;
        this.remoteStream = null;

        this.senders = [];

        this._connectionState = "";
        this.iceConnectionState = "";
        this.signalingState = "";
        this.jsEventHandle = new Map();

        this.oniceconnectionstatechange = null;
        this.onconnectionstatechange = null;
        this.onsignalingstatechange = null;
        this.onicecandidate = null;
        this.ontrack = null;

        //for original test only
        this.run = function (params) {
            console.log("RTCPeerConnection.run with " + params);
        }

        // this.eventHandler = cordovaEventHandler
        var self = this;
        cordova.exec(function (ev) {
            self.cordovaEventHandler(ev);
        }, function (ev) {
            console.log("Failed to create RTCPeerConnection object");
        }, MediaService, 'createInstance', [this.id, this.config]);

    }
    getConfiguration() {
        return this.config;
    }

    cordovaEventHandler(ev) {
        console.log("PeerConnection Event: " + JSON.stringify(ev));
        switch (ev.event) {
            case EventType.onIceCandidate:
                // console.log("got event " + EventType.onIceCandidate);
                if (this.onicecandidate != null) {
                    // this.onicecandidate(new RTCPeerConnectionIceEvent("icecandidate", { candidate: JSON.parse(ev.payload)}));
                    if (ev.payload != "") {
                        this.onicecandidate({ type: "icecandidate", candidate: JSON.parse(ev.payload) });
                    } else {
                        this.onicecandidate({ type: "icecandidate", candidate: null });
                    }
                } else {
                    console.log("not found RTCPeerConnection.onicecandidate function");
                }
                break;
            case EventType.onICEConnectionStateChange:
                // console.log("got event " + EventType.onICEConnectionStateChange, ev);
                this.iceConnectionState = ev.payload;
                if (this.oniceconnectionstatechange != null) {
                    // this.onicecandidate(new RTCPeerConnectionIceEvent("icecandidate", { candidate: JSON.parse(ev.payload)}));
                    this.oniceconnectionstatechange();
                } else {
                    console.log("not found RTCPeerConnection.onICEConnectionStateChange function");
                }
                break;
            case EventType.onConnectionStateChange:
                // console.log("got event " + EventType.onConnectionStateChange);
                this._connectionState = ev.payload;
                if (this.onConnectionStateChange != null) {
                    // this.onicecandidate(new RTCPeerConnectionIceEvent("icecandidate", { candidate: JSON.parse(ev.payload)}));
                    this.onicecanonConnectionStateChangedidate();
                } else {
                    console.log("not found RTCPeerConnection.onConnectionStateChange function");
                }
                break;
            case EventType.onSignalingStateChange:
                // console.log("got event " + EventType.onSignalingStateChange);
                this.signalingState = ev.payload;
                if (this.onsignalingstatechange != null) {
                    // this.onicecandidate(new RTCPeerConnectionIceEvent("icecandidate", { candidate: JSON.parse(ev.payload)}));
                    this.onsignalingstatechange();
                } else {
                    console.log("not found RTCPeerConnection.onConnectionStateChange function");
                }
                break;
            case EventType.onAddTrack:
                console.log("got event " + EventType.onAddTrack);
                if (!this.remoteStream) {
                    this.remoteStream = new Stream.MediaStream();
                }
                if (this.ontrack != null) {
                    // this.onicecandidate(new RTCPeerConnectionIceEvent("icecandidate", { candidate: JSON.parse(ev.payload)}));
                    var summary = JSON.parse(ev.payload);
                    var track = new Stream.MediaStreamTrack(summary.kind, summary.id);
                    this.remoteStream.addTrack(track)
                    this.ontrack({ track: track, streams: [this.stream] });
                } else {
                    console.log("not found RTCPeerConnection.onConnectionStateChange function");
                }
                break;
            default:
                console.log("not implement RTCPeerConnection eventhandler function " + ev.event);
        }
    }

    //first class
    createOffer(config) {
        return new Promise((resolve, reject) => {
            console.log("createOffer function  ", config);
            cordova.exec(function (ev) {
                console.log("Got one offer: " + ev);
                // pc.id = ev
                // console.log("check this.id done:" + pc.id)
                resolve(JSON.parse(ev))
            }, function (ev) {
                console.log("Failed to create offer");
                reject("failed to create offer");
            }, MediaService, 'createOffer', [this.id, config]);
        })
    }

    //first class
    setLocalDescription(offer) {
        var that = this;
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                var senders = JSON.parse(ev)
                senders.forEach(sender => {
                    if (sender && sender.parameter) {
                        var parameter = JSON.parse(sender.parameter)
                        var idx = that.senders.findIndex(sdr => sdr.track.kind === sender.track)
                        if (idx == -1) {
                            that.senders.push(new RTCRtpSender(parameter, sender.track, that.id))
                        } else {
                            console.log("setRtpSenderParameters x", that.senders[idx].modified)
                            if (that.senders[idx].modified) {
                                var degradationPreference = null;
                                if (that.senders[idx].parameter) {
                                    degradationPreference = that.senders[idx].parameter.degradationPreference
                                }
                                console.log("setRtpSenderParameters x1", that.senders[idx].modified)
                                var maxBitrate = 0;
                                var minBitrate = 0;
                                if (that.senders[idx].parameter && that.senders[idx].parameter.encodings && that.senders[idx].parameter.encodings[0]) {
                                    maxBitrate = that.senders[idx].parameter.encodings[0].maxBitrate;
                                    minBitrate = that.senders[idx].parameter.encodings[0].minBitrate;
                                }
                                console.log("setRtpSenderParameters x2", that.senders[idx].modified)
                                cordova.exec(function (ev) {
                                    resolve(ev);
                                }, function (ev) {
                                    reject(ev);
                                }, MediaService, 'setSenderParameter', [that.id, that.senders[idx].track.kind, degradationPreference, maxBitrate == null ? 0 : maxBitrate, minBitrate == null ? 0 : minBitrate]);
                            }
                            that.senders[idx].parameter = parameter
                            that.senders[idx].modified = false
                        }
                    }
                })
                console.log("peerconnection senders after setLocalDescription", that.senders)
                resolve();
            }, function (ev) {
                reject(ev);
            }, MediaService, 'setLocalDescription', [this.id, offer.type, offer.sdp]);
        })
    }

    //first class
    setRemoteDescription(answer) {
        console.log("Got one answer ", answer)
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                resolve(ev);
            }, function (ev) {
                reject(ev);
            }, MediaService, 'setRemoteDescription', [this.id, answer.type, answer.sdp]);
        })
    }

    //first class
    addIceCandidate(candidate) {
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                resolve(ev);
            }, function (ev) {
                reject(ev);
            }, MediaService, 'addIceCandidate', [this.id, candidate]);
        })
    }

    //first class
    addTrack(track) {
        if (!this.localStream) {
            this.localStream = new Stream.MediaStream();
        }
        this.localStream.addTrack(track);
        var sender = new RTCRtpSender(null, track.kind, this.id)
        this.senders.push(sender)
        console.log("peerconnection senders after addTrack", this.senders)
        cordova.exec(function (ev) {
        }, function (ev) {
        }, MediaService, 'addTrack', [this.id, track.id, track.kind]);
        return sender;
    }

    close() {
        cordova.exec(function (ev) {
        }, function (ev) {
        }, MediaService, 'close', [this.id]);
    }

    removeTrack(track) {
        cordova.exec(function (ev) {
        }, function (ev) {
        }, MediaService, 'removeTrack', [this.id, track.id, track.kind]);
    }

    getTransceivers() {
        cordova.exec(function (ev) {
        }, function (ev) {
        }, MediaService, 'getTransceivers', [this.id]);
    }

    addTransceiver() {

        cordova.exec(function (ev) {
        }, function (ev) {
        }, MediaService, 'addTransceiver', [this.id]);
    }

    getSenders() {
        return this.senders
    }

    getStats(selector) {
        // return new Promise((resolve, reject) => { })
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                resolve(new RTCStatsReport(ev));
            }, function (ev) {
                reject(ev);
            }, MediaService, 'getStats', [this.id]);
        })
    }

    /**
     * Deprecated
     * This feature is no longer recommended. 
     * Though some browsers might still support it, it may have already been removed from the relevant web standards,
     *  may be in the process of being dropped, or may only be kept for compatibility purposes. 
     * Avoid using it, and update existing code if possible;
     *  see the compatibility table at the bottom of this page to guide your decision. 
     * Be aware that this feature may cease to work at any time.
     */
    addStream(stream) {
        console.log("peerconnection addStream:", stream)
        this.stream = stream
        stream.getTracks().forEach(track => {
            this.addTrack(track)
        })
    }


    addEventListener(eventType, func) {
        var queue = this.jsEventHandle.get(eventType);
        if (queue !== undefined) {
            queue.push(func)
            this.jsEventHandle.set(eventType, queue);
        } else {
            this.jsEventHandle.set(eventType, [func]);
        }
    }
    removeEventListener(eventType, func) {
        var queue = this.jsEventHandle.get(eventType);
        if (queue !== undefined) {
            queue.splice(queue.indexOf(func), 1)
            this.jsEventHandle.set(eventType, queue);
        }
    }
}

cordova.addConstructor(function () {
    // console.log("RTCPeerConnection.js addConstructor");
    window.RTCRtpSender = RTCRtpSender;
    window.RTCPeerConnection = RTCPeerConnection;
    window.webkitRTCPeerConnection = RTCPeerConnection;
    return window.RTCPeerConnection;
});

console.log("RTCPeerConnection.js onloaded");