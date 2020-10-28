

console.log("mediaDevice.js onloading");

var Media = require('./Media');
var stream = require('./Stream');

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
            console.log("after create  stream object");
            var track = new MediaStreamTrack();
            console.log("Ready to return stream object");
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
