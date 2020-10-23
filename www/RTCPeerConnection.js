

console.log("RTCPeerConnection.js onload");

function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}


class RTCPeerConnection {
    constructor(config) {
        if (config == undefined) {
            this.config = "default"
        } else {
            this.config = config
        }

        this.id = uuidv4();


        this.run = function (params) {
            console.log("RTCPeerConnection.run with " + params);
        }

        console.log("check pc.id " + this.id)

        cordova.exec(function (ev) {
            console.log("Got one android object with id: " + ev);
            // pc.id = ev
            // console.log("check this.id done:" + pc.id)
        }, function (ev) {
            console.log("Failed to create RTCPeerConnection object");
        }, 'RTCPeerConnectionHook', 'CreateInstance', [this.id, this.config]);

    }

    createOffer(cb) {
        console.log("createOffer function");
        cordova.exec(function (ev) {
            console.log("Got one offer: " + ev);
            // pc.id = ev
            // console.log("check this.id done:" + pc.id)
            cb(ev)
        }, function (ev) {
            console.log("Failed to create offer");
        }, 'RTCPeerConnectionHook', 'createOffer', []);
        return ""
    }

    setRemoteDescription(sdp){
        console.log("setRemoteDescription "+sdp.type);
        console.log("setRemoteDescription "+sdp.sdp);

        cordova.exec(function (ev) {
            console.log("Got one setRemoteDescription response: " + ev);
        }, function (ev) {
            console.log("Failed to setRemoteDescription");
        }, 'RTCPeerConnectionHook', 'setRemoteDescription', [sdp.type,sdp.sdp]);
    }

}

cordova.addConstructor(function () {
    // console.log("RTCPeerConnection.js addConstructor");
    window.RTCPeerConnection = RTCPeerConnection;
    return window.RTCPeerConnection;
});
