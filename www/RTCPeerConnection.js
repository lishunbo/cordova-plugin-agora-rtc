

console.log("RTCPeerConnection.js onloading");

function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}


const EventType = {
    onIceCandidate: "onIceCandidate",
}

class RTCPeerConnection {
    constructor(config) {
        this.id = uuidv4();
        this.config = config;

        this.stream = null;

        this.oniceconnectionstatechange = null;
        this.onICEConnectionStateChange = null;
        this.onconnectionstatechange = null;
        this.onConnectionStateChange = null;
        this.onsignalingstatechange = null;
        this.onicecandidate = null;
        this.ontrack = null;

        //for original test only
        this.run = function (params) {
            console.log("RTCPeerConnection.run with " + params);
        }

        this.eventHandler = function (ev) {
            console.log("createInstance done id: " + JSON.stringify(ev));
            // console.log("createInstance done id: " + ev.event);
            // console.log("createInstance done id: " + ev.payload);
            // console.log("createInstance done id: " + ev.id);
            // console.log("createInstance done id: " + this.id);
            // console.log("createInstance done id: " + this.onicecandidate);
            switch (ev.event) {
                case EventType.onIceCandidate:
                    console.log("got event " + EventType.onIceCandidate);
                    if (this.onicecandidate != null) {
                        // this.onicecandidate(new RTCPeerConnectionIceEvent("icecandidate", { candidate: JSON.parse(ev.payload)}));
                        this.onicecandidate({ type: "icecandidate", candidate: JSON.parse(ev.payload) });
                    } else {
                        console.log("not found RTCPeerConnection.onicecandidate function");
                    }
            }
        }
        var self = this;
        cordova.exec(function (ev) {
            self.eventHandler(ev);
        },
            function (ev) {
                console.log("Failed to create RTCPeerConnection object");
            }, 'Hook', 'createInstance', [this.id, this.config]);

    }

    //first class
    createOffer(config) {
        return new Promise((resolve, reject) => {
            console.log("createOffer function");
            cordova.exec(function (ev) {
                console.log("Got one offer: " + ev);
                // pc.id = ev
                // console.log("check this.id done:" + pc.id)
                resolve(JSON.parse(ev))
            }, function (ev) {
                console.log("Failed to create offer");
                reject("failed to create offer");
            }, 'Hook', 'createOffer', [this.id]);
        })
    }

    //first class
    setLocalDescription(offer) {
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                resolve(ev);
            }, function (ev) {
                reject(ev);
            }, 'Hook', 'setLocalDescription', [this.id, offer.type, offer.sdp]);
        })
    }

    //first class
    setRemoteDescription(answer) {
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                resolve(ev);
            }, function (ev) {
                reject(ev);
            }, 'Hook', 'setRemoteDescription', [this.id, answer.type, answer.sdp]);
        })
    }

    //first class
    addIceCandidate(candidate) {
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                resolve(ev);
            }, function (ev) {
                reject(ev);
            }, 'Hook', 'addIceCandidate', [this.id, candidate]);
        })
    }

    //first class
    addTrack(track) {
        cordova.exec(function (ev) {
        }, function (ev) {
        }, 'Hook', 'addTrack', [this.id, track]);
    }

    close() {
        cordova.exec(function (ev) {
        }, function (ev) {
        }, 'Hook', 'close', [this.id]);
    }

    removeTrack() {
        cordova.exec(function (ev) {
        }, function (ev) {
        }, 'Hook', 'removeTrack', [this.id]);
    }

    getTransceivers() {

        cordova.exec(function (ev) {
        }, function (ev) {
        }, 'Hook', 'getTransceivers', [this.id]);
    }

    addTransceiver() {

        cordova.exec(function (ev) {
        }, function (ev) {
        }, 'Hook', 'addTransceiver', [this.id]);
    }

    getStats(slector) {
        return new Promise((resolve, reject) => {})
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                resolve(ev);
            }, function (ev) {
                reject(ev);
            }, 'Hook', 'getStats', [this.id]);
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
}

cordova.addConstructor(function () {
    // console.log("RTCPeerConnection.js addConstructor");
    window.RTCPeerConnection = RTCPeerConnection;
    window.webkitRTCPeerConnection = RTCPeerConnection;
    return window.RTCPeerConnection;
});

console.log("RTCPeerConnection.js onloaded");