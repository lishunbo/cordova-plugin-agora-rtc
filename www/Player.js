
console.log("player.js onloading");

const { EventTarget } = require('./EventTarget');
var { uuidv4 } = require('./Util');

var NativePlayerEventType = {
  onFirstFrameDecoded: "onFirstFrameDecoded",
  onVolumeChange: "onVolumeChange",
  onAudioLevel: "onAudioLevel",
  dispose: "dispose",
}

var PlayerService = "Player";

class PlayEvent {
  constructor(resolve, reject, call, args) {
    this.resolve = resolve;
    this.reject = reject;
    this.call = call;
    this.args = args;
  }
  play() {
    this.call(this.resolve, this.reject, this.args);
  }
}

class VideoPlayer extends EventTarget {
  constructor(config) {
    super();
    this.id = uuidv4();
    this.onFirstVideoFrameDecoded = null;
    this.isViewCreated = false;
    this.playEvent = null;
    this.videoTrack = null;

    this.config =
      config === null ? {
        trackId: "", mirror: false, fit: "fill"
      } : config;

    var thiz = this;
    cordova.exec(function (ev) {
      thiz.handleEvent(ev);
    }, function (ev) {
      throw 'createVideoPlayer exception:' + ev
    }, PlayerService, 'createVideoPlayer', [this.id, this.config]);
  }

  udpateConfig(config) {
    this.config = config === null ? this.config : config;
    cordova.exec(function (ev) {
    }, function (ev) {
      throw 'updateConfig exception:' + ev
    }, PlayerService, 'udpateConfig',
      [this.id, this.config.trackId, this.config.mirror, this.config.fit]);
  }


  play() {
    return new Promise((resolve, reject) => {
      var playFunc = function (resolve, reject, id) {
        cordova.exec(function (ev) {
          resolve(ev)
        }, function (ev) {
          reject != null && reject('playVideoPlayer exception: ' + ev)
        }, PlayerService, 'playVideoPlayer', [id]);
      }
      if (!this.isViewCreated) {
        this.playEvent = new PlayEvent(resolve, reject, playFunc, this.id)
      } else {
        playFunc(resolve, reject, this.id);
      }

    });
  }
  pause() {
    cordova.exec(function (ev) {
    }, function (ev) {
      throw 'pauseVideoPlayer exception: ' + ev
    }, PlayerService, 'pauseVideoPlayer', [this.id]);
  }
  destroy() {
    cordova.exec(function (ev) {
    }, function (ev) {
      throw 'destoryVideoPlayer exception: ' + ev
    }, PlayerService, 'destroyVideoPlayer', [this.id]);
  }

  getCurrentFrame() {
    return new Promise((resolve, reject) => {
      resolve()
    });
  }

  updateVideoTrack(track) {
    if (this.videoTrack) {
      this.videoTrack.stop();
    }
    this.videoTrack = track;

    cordova.exec(function (ev) {
    }, function (ev) {
      throw 'updateVideoTrack exception: ' + ev
    }, PlayerService, 'updateVideoTrack', [this.id, track.id, track.kind]);
  }

  getWindowAttribute() {
    return new Promise((resolve, reject) => {
      cordova.exec(function (ev) {
        resolve(JSON.parse(ev))
      }, function (ev) {
        reject != null && reject('getWindowAttr exception: ' + ev)
      }, PlayerService, 'getWindowAttribute', [this.id]);
    });
  }
  setViewAttribute(width, height, x, y) {
    console.log("setViewAttribute interface in plugin VideoControl")
    var that = this;
    cordova.exec(function (ev) {
      that.isViewCreated = true;
      if (that.playEvent) {
        that.playEvent.play();
        that.playEvent = null;
      }
    }, function (ev) {
      throw 'setViewAttr exception: ' + ev
    }, PlayerService, 'setViewAttribute', [this.id, width, height, x, y]);
  }

  handleEvent(ev) {
    switch (ev.event) {
      case NativePlayerEventType.onFirstFrameDecoded:
        if (this.onFirstFrameDecoded != null) {
          this.onFirstFrameDecoded();
        }
        break;
      case NativePlayerEventType.dispose:
        this.videoTrack = null;
      default:
        console.log("[Debug] not implement VideoPlayer handleEvent " + ev.event);
    }
  }
}

class AudioPlayer {
  constructor(track) {
    this.id = uuidv4();
    this.audioTrack = track;
    this.maxVolume = 0;
    this.minVolume = 0;
    this.volume = 0;

    this.onVolumeChange = null;
    this.onAudioLevel = null;

    var thiz = this;
    cordova.exec(function (ev) {
      thiz.handleEvent(ev);
    }, function (ev) {
      throw 'createAudioPlayer exception: ' + ev
    }, PlayerService, 'createAudioPlayer', [this.id, track.id]);
  }

  play() {
    return new Promise((resolve, reject) => {
      cordova.exec(function (ev) {
        resolve(ev)
      }, function (ev) {
        reject != null && reject('playAudioPlayer exception: ' + ev)
      }, PlayerService, 'playAudioPlayer', [this.id]);
    });
  }
  pause() {
    return new Promise((resolve, reject) => {
      cordova.exec(function (ev) {
        resolve(ev)
      }, function (ev) {
        reject != null && reject('pauseAudioPlayer exception: ' + ev)
      }, PlayerService, 'pauseAudioPlayer', [this.id]);
    });
  }
  destroy() {
    cordova.exec(function (ev) {
      resolve(ev)
    }, function (ev) {
      throw 'destroyAudioPlayer exception: ' + ev
    }, PlayerService, 'destroyAudioPlayer', [this.id]);
  }
  updateTrack(track) {
    cordova.exec(function (ev) {
      self.cordovaEventHandler(ev);
    }, function (ev) {
      throw 'updateTrack exception: ' + ev
    }, PlayerService, 'updateTrackAudioPlayer', [this.id, track.id]);
  }
  getVolumeRange() {
    return new Promise((resolve, reject) => {
      var that = this;
      cordova.exec(function (ev) {
        var range = JSON.parse(ev)
        that.minVolume = range.min;
        that.maxVolume = range.max;
        resolve(range);
      }, function (ev) {
        reject != null && reject('getVolumeRange exception:' + ev)
      }, PlayerService, 'getVolumeRange', [this.id]);
    })
  }
  getVolume() {
    return this.volume
  }
  setVolume(volume) {
    return new Promise((resolve, reject) => {
      cordova.exec(function (ev) {
        resolve(ev);
      }, function (ev) {
        reject != null && reject('setVolume exception: ' + ev)
      }, PlayerService, 'setVolume', [this.id, volume]);
    })
  }
  setSinkID(trackId, deviceId) {
    return new Promise((resolve, reject) => {
      cordova.exec(function (ev) {
        resolve(ev);
      }, function (ev) {
        console.log("Failed AudioPlayer setSinkID object");
        reject != null && reject('setSinkId exception: ' + ev)
      }, PlayerService, 'setSinkId', [this.id, trackId, deviceId]);
    })
  }

  handleEvent(ev) {
    switch (ev.event) {
      case NativePlayerEventType.onVolumeChange:
        this.volume = parseInt(ev.payload);
        if (this.onVolumeChange != null) {
          this.onVolumeChange(this.volume);
        }
        break;
      case NativePlayerEventType.onAudioLevel:
        if (this.onAudioLevel != null) {
          this.onAudioLevel(parseFloat(ev.payload))
        }
        break;
      case NativePlayerEventType.dispose:
        this.audioTrack = null;
        break;
      default:
        console.log("[Debug] not implement AudioPlayer handleEvent " + ev.event);
    }
  }
}

cordova.addConstructor(function () {
  window.VideoPlayer = VideoPlayer;
  window.AudioPlayer = AudioPlayer;
});


/**
 * @module NativePlayer
 */
module.exports = {
  VideoPlayer,
  AudioPlayer,
}
console.log("player.js onloaded");