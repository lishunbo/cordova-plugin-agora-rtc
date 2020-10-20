


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

}

cordova.addConstructor(function () {
    window.RTCPeerConnection = RTCPeerConnection;
    return window.RTCPeerConnection;
});
