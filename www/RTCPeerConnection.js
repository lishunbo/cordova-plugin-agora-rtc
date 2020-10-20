

class RTCPeerConnection {
    constructor(config) {
        if (config == undefined) {
            this.config = "default"
        } else {
            this.config = config
        }

        cordova.exec(function (ev) { }, function (ev) { },'RTCPeerConnection', 'echo', [message]);




        this.run = function (params) {
            console.log("RTCPeerConnection.run with " + params);
        }
    }

}

cordova.addConstructor(function () {
    window.RTCPeerConnection = RTCPeerConnection;
    return window.RTCPeerConnection;
});
