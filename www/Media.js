

const { EventTarget } = require('./EventTarget');
var { uuidv4 } = require('./Util');

var MediaService = "Media";

const TrackKind = {
  audio: 'audio',
  video: 'video'
}

class MediaStream extends EventTarget {
  constructor() {
    super();
    this.active = true;
    this.id = uuidv4();

    this.onactive = null;
    this.onaddtrack = null;
    this.oninactive = null;
    this.onremovetrack = null;

    this.track = [];
  }

  addTrack(track) { this.track.push(track); }
  clone() { return new MediaStream() }

  getAudioTracks() {
    var audioTrack = [];

    this.track.forEach(track => {
      if (track.kind == TrackKind.audio) {
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
      if (track.kind == TrackKind.video) {
        videoTrack.push(track)
      }
    })

    return videoTrack;
  }
  removeTrack(track) {
    this.track = this.track.filter(
      (value, index, array) => { return value.id !== track.id })
  }
}

class MediaStreamTrack extends EventTarget {
  constructor(kind, id) {
    super();
    this.contentHint = "";
    this.enabled = true;
    if (id !== undefined) {
      this.id = id;
    } else {
      this.id = uuidv4();
    }
    this.kind = kind;
    this.label = kind;
    this.muted = false;
    this.readyState = "live";

    this.onended = null;
    this.onmute = null;
    this.onunmute = null;
  }

  applyConstraints() {
    return new Promise((resolve, reject) => {
      resolve()
    })
  }
  clone() { throw 'not implement MediaStreamTrack.clone()' }
  getCapabilities() { throw 'not implement MediaStreamTrack.getCapabilities()' }
  getConstraints() { throw 'not implement MediaStreamTrack.getConstraints()' }
  getSettings() { throw 'not implement MediaStreamTrack.getSettings()' }
  stop() {
    cordova.exec(function (ev) {
    }, function (ev) {
      throw 'stopMediaStreamTrack exception: ' + ev
    }, MediaService, 'stopMediaStreamTrack', [this.id]);
  }
}

class MediaDeviceInfo {
  constructor(deviceId, groupId, kind, label) {
    this.deviceId = deviceId;
    this.groupId = groupId;
    this.kind = kind;
    this.label = label;
  }
}

class MediaDevices extends EventTarget {
  constructor() {
    super();
    this.ondevicechange = null;
  }

  enumerateDevices() {
    return new Promise((resolve, reject) => {
      cordova.exec(function (ev) {
        if (ev === 'OK') {
          return
        }
        var infos = JSON.parse(ev);

        let avdevices = [];

        infos.forEach(info => {
          avdevices.push(new MediaDeviceInfo(
            info.deviceId, info.groupId, info.kind, info.label));
        })
        resolve(avdevices)
      }, function (ev) {
        reject != null && reject("enumerateDevices exception: " + ev)
      }, MediaService, 'enumerateDevices', []);
    });
  }
  getSupportedConstraints() {
    throw 'not implement MediaDevices.getSupportedConstraints()'
  }
  getUserMedia(config) {
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
        if (ev === "OK") {
          return
        }
        var tracks = JSON.parse(ev);
        var stm = new MediaStream();

        tracks.forEach(track => {
          var newTrack = new MediaStreamTrack(track.kind, track.id);
          newTrack.label = "mock track"
          stm.addTrack(newTrack)
        });
        resolve(stm)
      }, function (ev) {
        reject != null && reject("getUserMedia exception: " + ev)
      }, MediaService, 'getUserMedia', [args]);
    })
  }
  // only for dualStream
  getSubVideoTrack(track, width, heigth, framerate) {
    return new Promise((resolve, reject) => {
      var args = {}
      args.video = {
        width: width,
        height: heigth,
        frameRate: framerate,
      }
      cordova.exec(function (ev) {
        var subTrack = JSON.parse(ev);
        resolve(new MediaStreamTrack(subTrack.kind, subTrack.id))
      }, function (ev) {
        reject != null && reject('getSubVideoTrack exception: ' + ev)
      }, MediaService, 'getSubVideoTrack', [track.id, args]);
    })
  }
}

cordova.addConstructor(function () {
  window.MediaDeviceInfo = MediaDeviceInfo;
  window.MediaStream = MediaStream;
  window.MediaStreamTrack = MediaStreamTrack;
});


/**
 * @module Media
 */
module.exports = new MediaDevices()

console.log("mediaDevice.js onloaded");
