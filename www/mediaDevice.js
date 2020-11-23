

console.log("mediaDevice.js onloading");

var media = require('./Media');
var stream = require('./Stream');

var mediaDevice = {}

// first class
mediaDevice.getUserMedia = function (config) {
    return new Promise((resolve, reject) => {
        var args = {}
        if (config.video !== undefined) {
            if (typeof config.video === 'boolean') {
                args.video = {};
            } else if (typeof config.video === 'object' && config.video !== null) {
                args.video = config.video;
            }
        }
        if (config.audio !== undefined) {
            if (typeof config.audio === 'boolean') {
                args.audio = {};
            } else if (typeof config.audio === 'object' && config.audio !== null) {
                args.audio = config.audio;
            }
        }

        cordova.exec(function (ev) {
            var tracks = JSON.parse(ev);
            console.log("Got one stream object with id: " + ev);
            // pc.id = ev
            // console.log("check this.id done:" + pc.id)
            var stm = new stream.MediaStream();

            tracks.forEach(track => {
                var newTrack = new stream.MediaStreamTrack(track.kind, track.id);
                newTrack.label = "mock track"
                stm.addTrack(newTrack)
            });
            resolve(stm)
        }, function (ev) {
            console.log("Failed to getUserMedia object");
        }, 'Hook', 'getUserMedia', [args]);

    })
}

class MediaDeviceInfo {
    constructor(deviceId, groupId, kind, label) {
        this.deviceId = deviceId;
        this.groupId = groupId;
        this.kind = kind;
        this.label = label;
    }
}

// first class
mediaDevice.enumerateDevices = function () {
    return new Promise((resolve, reject) => {
        cordova.exec(function (ev) {
            var infos = JSON.parse(ev);

            let avdevices = [];

            infos.forEach(info => {
                avdevices.push(new MediaDeviceInfo(info.deviceId, info.groupId, info.kind, info.label));
            })
            resolve(avdevices)

        }, function (ev) {
            console.log("Failed to enumerateDevices object");
        }, 'Hook', 'enumerateDevices', []);
    });
}

mediaDevice.addEventListener = function (event, func) {
    console.log("mediaDevice.js addEventListener" + event);
}



cordova.addConstructor(function () {
    window.MediaDeviceInfo = MediaDeviceInfo;
});

module.exports = mediaDevice;

console.log("mediaDevice.js onloaded");
