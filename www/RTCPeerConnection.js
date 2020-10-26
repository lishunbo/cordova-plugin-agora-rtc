

console.log("RTCPeerConnection.js onload");

function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}


class RTCPeerConnection {
    constructor(config) {
        this.id = uuidv4();
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

        cordova.exec(function (ev) {
            console.log("Got one android object with id: " + ev);
            // pc.id = ev
            // console.log("check this.id done:" + pc.id)
        }, function (ev) {
            console.log("Failed to create RTCPeerConnection object");
        }, 'Hook', 'CreateInstance', [this.id, this.config]);

    }

    createOffer() {
        return new Promise((resolve, reject) => {
            console.log("createOffer function");
            cordova.exec(function (ev) {
                console.log("Got one offer: " + ev);
                // pc.id = ev
                // console.log("check this.id done:" + pc.id)
                resolve(ev)
            }, function (ev) {
                console.log("Failed to create offer");
                reject("failed to create offer");
            }, 'Hook', 'createOffer', [this.id]);
        })
    }

    setRemoteDescription(answer) {
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                resolve(ev);
            }, function (ev) {
                reject(ev);
            }, 'Hook', 'setRemoteDescription', [this.id, answer.type, answer.sdp]);
        })
    }

    addIceCandidate(candidate) {
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                resolve(ev);
            }, function (ev) {
                reject(ev);
            }, 'Hook', 'addIceCandidate', [this.id, candidate]);
        })
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

    addTrack() {
        cordova.exec(function (ev) {
        }, function (ev) {
        }, 'Hook', 'addTrack', [this.id]);
    }

    getStats() {
        return new Promise((resolve, reject) => {
            cordova.exec(function (ev) {
                resolve(ev);
            }, function (ev) {
                reject(ev);
            }, 'Hook', 'getStats', [this.id]);
        })
    }
}

cordova.addConstructor(function () {
    // console.log("RTCPeerConnection.js addConstructor");
    window.RTCPeerConnection = RTCPeerConnection;
    return window.RTCPeerConnection;
});
