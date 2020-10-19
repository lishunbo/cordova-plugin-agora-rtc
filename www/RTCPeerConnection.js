

class RTCPeerConnection {
    constructor(name) {
        if (name == undefined) {
            this.name = "default"
        } else {
            this.name = name
        }
        console.log("class RTCPeerConnection has been created with name:" + this.name);

        this.run = function (params) {
            console.log("RTCPeerConnection.run with " + params);
        }
    }
}

cordova.addConstructor(function () {
    window.RTCPeerConnection = RTCPeerConnection;
    return window.RTCPeerConnection;
});
