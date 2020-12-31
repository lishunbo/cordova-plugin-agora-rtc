//TODO: copyright

var { EventTarget } = require('./EventTarget');
var { uuidv4 } = require('./Util');

var WebRTCService = "WebRTC"

const NativeRTCEventType = {
  connectionstatechange: 'connectionstatechange',
  datachannel: 'datachannel',
  icecandidate: 'icecandidate',
  icecandidateerror: 'icecandidateerror',
  iceconnectionstatechange: 'iceconnectionstatechange',
  icegatheringstatechange: 'icegatheringstatechange',
  negotiationneeded: 'negotiationneeded',
  signalingstatechange: 'signalingstatechange',
  statsended: 'statsended',
  track: 'track'
}

class RTCStatsReport {
  constructor(json) {
    this.stats = JSON.parse(json);
  }
  forEach(callbackfn) {
    this.stats.forEach((v, i, a) => {
      callbackfn(v, i, a);
    })
  }
  get(id) {
    return this.stats.find(x => x.id === id);
  }
  values() {
    return this.stats;
  }
}

class RTCRtpSendParameters {
  constructor() {
    this.degradationPreference = null;
    this.encodings = null;
    this.priority = null;
    this.transactionId = null;
  }
}

class RTCRtpSender {
  constructor(parameter, kind, pcid, id) {
    if (id) {
      console.log("create by id " + id);
      this.id = id;
    } else {
      this.id = uuidv4();
    }
    this.pcid = pcid;
    if (parameter) {
      this.parameter = parameter
    } else {
      this.parameter = new RTCRtpSendParameters()
    }
    this.track = new MediaStreamTrack(kind)
    //internal usage for call setParameters() before setLocalDescription()
    //TODO: change this logic later
    this._modified = false
  }
  replaceTrack(track) {
    cordova.exec(function (ev) {
    }, function (ev) {
      throw 'replaceTrack exception: ' + ev
    }, WebRTCService, 'replaceTrack',
      [this.id, this.pcid, track.id, track.kind]);
  }
  getParameters() {
    return this.parameter;
  }
  setParameters(parameters) {
    this.parameter = parameters
    this._modified = true
  }
}

class RTCPeerConnection extends EventTarget {
  constructor(config) {
    super();
    this.id = uuidv4();
    this.config = config;

    this.localStream = null;
    this.remoteStream = null;

    this.senders = [];

    this.canTrickleIceCandidates = false;
    this.connectionState = "";
    this.currentLocalDescription = null;
    this.currentRemoteDescription = null;
    this.iceConnectionState = "";
    this.iceGatheringState = "";
    this.localDescription = "";
    this.signalingState = "";

    //Deprecated
    //this.onaddstream = null;
    this.onconnectionstatechange = null;
    this.ondatachannel = null;
    this.onicecandidate = null;
    this.onicecandidateerror = null;
    this.oniceconnectionstatechange = null;
    this.onicegatheringstatechange = null;
    this.onnegotiationneeded = null;
    //Deprecated
    //this.onremovestream = null;
    this.onsignalingstatechange = null;
    this.onstatsended = null;
    this.ontrack = null;
    this.pendingLocalDescription = null;
    this.pendingRemoteDescription = null;
    this.remoteDescription = null;
    this.sctp = null;
    this.signalingState = null;

    var thiz = this;
    cordova.exec(function (ev) {
      thiz.handleEvent(ev);
    }, function (ev) {
      throw 'createPC exception: ' + ev
    }, WebRTCService, 'createPC', [this.id, this.config]);
  }

  getConfiguration() {
    return this.config;
  }

  handleEvent(ev) {
    switch (ev.event) {
      case NativeRTCEventType.icecandidate:
        var candidate = null
        if (ev.payload !== "") {
          candidate = JSON.parse(ev.payload)
        }
        this.dispatchEvent({ type: "icecandidate", candidate: candidate })
        if (this.onicecandidate != null) {
          this.onicecandidate({ type: "icecandidate", candidate: candidate });
        }
        break;
      case NativeRTCEventType.iceconnectionstatechange:
        this.iceConnectionState = ev.payload;
        this.dispatchEvent({ type: "iceconnectionstatechange" })
        if (this.oniceconnectionstatechange != null) {
          this.oniceconnectionstatechange();
        }
        break;
      case NativeRTCEventType.icegatheringstatechange:
        this.iceConnectionState = ev.payload;
        this.dispatchEvent({ type: "icegatheringstatechange" })
        if (this.onicegatheringstatechange != null) {
          this.onicegatheringstatechange();
        }
        break;
      case NativeRTCEventType.onConnectionStateChange:
        this.connectionState = ev.payload;
        this.dispatchEvent({ type: "connectionstatechange" })
        if (this.onconnectionstatechange != null) {
          this.onconnectionstatechange();
        }
        break;
      case NativeRTCEventType.negotiationneeded:
        this.dispatchEvent({ type: "negotiationneeded" })
        if (this.onnegotiationneeded != null) {
          this.onnegotiationneeded();
        }
        break;
      case NativeRTCEventType.signalingstatechange:
        this.signalingState = ev.payload;
        this.dispatchEvent({ type: "signalingstatechange" })
        if (this.onsignalingstatechange != null) {
          this.onsignalingstatechange();
        }
        break;
      case NativeRTCEventType.track:
        if (!this.remoteStream) {
          this.remoteStream = new MediaStream();
        }
        var summary = JSON.parse(ev.payload);
        var track = new MediaStreamTrack(summary.kind, summary.id);
        this.remoteStream.addTrack(track)
        this.dispatchEvent({
          type: "track",
          track: track, streams: [this.remoteStream]
        })
        if (this.ontrack != null) {
          this.ontrack({ track: track, streams: [this.remoteStream] });
        }
        break;
      default:
        console.log(
          "[Debug] not implement RTCPeerConnection event handler" + ev.event);
    }
  }

  createOffer(config) {
    return new Promise((resolve, reject) => {
      cordova.exec(function (ev) {
        resolve(JSON.parse(ev))
      }, function (ev) {
        reject != null && reject("createOffer exception:" + ev);
      }, WebRTCService, 'createOffer', [this.id, config]);
    })
  }

  setLocalDescription(offer) {
    var thiz = this;
    return new Promise((resolve, reject) => {
      cordova.exec(function (ev) {
        var senders = JSON.parse(ev)
        senders.forEach(sender => {
          if (sender && sender.parameter) {
            var param = JSON.parse(sender.parameter)
            var idx = thiz.senders.findIndex(
              sdr => sdr.track.kind === sender.kind)
            if (idx == -1) {
              thiz.senders.push(new RTCRtpSender(param, sender.kind, thiz.id))
            } else {
              //TODO: validy usage of modified
              if (thiz.senders[idx].modified) {
                // DegradationPreference, android supported:
                // DISABLED, MAINTAIN_FRAMERATE,MAINTAIN_RESOLUTION,BALANCED
                var degra = "BALANCED";
                var maxBitrate = 0;
                var minBitrate = 0;
                var scaleDown = 0;
                if (thiz.senders[idx].parameter) {
                  degra = thiz.senders[idx].parameter.degradationPreference
                  if (thiz.senders[idx].parameter.encodings &&
                    thiz.senders[idx].parameter.encodings.length > 0) {
                    var encoding = thiz.senders[idx].parameter.encodings[0]
                    maxBitrate =
                      encoding.maxBitrate == null ? 0 : encoding.maxBitrate;
                    minBitrate =
                      encoding.minBitrate == null ? 0 : encoding.minBitrate;
                    scaleDown =
                      encoding.scaleResolutionDownBy == null ?
                        0 : encoding.scaleResolutionDownBy;
                  }
                }
                cordova.exec(function (ev) {
                  resolve(ev);
                }, function (ev) {
                  reject != null && reject("setSenderParameter exception: " + ev);
                }, WebRTCService, 'setSenderParameter',
                  [thiz.id, thiz.senders[idx].track.kind, degra,
                    maxBitrate, minBitrate, scaleDown]);
              }
              thiz.senders[idx].parameter = param
              thiz.senders[idx].modified = false
            }
          }
        })
        resolve();
      }, function (ev) {
        reject != null && reject(ev);
      }, WebRTCService, 'setLocalDescription', [this.id, offer.type, offer.sdp]);
    })
  }

  setRemoteDescription(answer) {
    return new Promise((resolve, reject) => {
      cordova.exec(function (ev) {
        resolve(ev);
      }, function (ev) {
        reject != null && reject("setRemoteDescription exception: " + ev);
      }, WebRTCService, 'setRemoteDescription',
        [this.id, answer.type, answer.sdp]);
    })
  }

  addIceCandidate(candidate) {
    return new Promise((resolve, reject) => {
      cordova.exec(function (ev) {
        resolve(ev);
      }, function (ev) {
        reject != null && reject("addIceCandidate exception:" + ev);
      }, WebRTCService, 'addIceCandidate', [this.id, candidate]);
    })
  }

  addTrack(track) {
    if (!this.localStream) {
      this.localStream = new MediaStream();
    }
    this.localStream.addTrack(track);
    var sender = new RTCRtpSender(null, track.kind, this.id)
    this.senders.push(sender)
    cordova.exec(function (ev) {
    }, function (ev) {
      throw 'addTrack exception: ' + ev
    }, WebRTCService, 'addTrack', [this.id, track.id, track.kind]);
    return sender;
  }

  close() {
    cordova.exec(function (ev) {
    }, function (ev) {
      throw 'close exception: ' + ev
    }, WebRTCService, 'close', [this.id]);
  }

  removeTrack(sender) {
    cordova.exec(function (ev) {
    }, function (ev) {
      throw 'removeTrack exception: ' + ev
    }, WebRTCService, 'removeTrack',
      [this.id, sender.track.id, sender.track.kind]);
  }

  getTransceivers() {
    cordova.exec(function (ev) {
    }, function (ev) {
      throw 'getTransceivers exception: ' + ev
    }, WebRTCService, 'getTransceivers', [this.id]);
  }

  addTransceiver() {
    cordova.exec(function (ev) {
    }, function (ev) {
      throw 'addTransceivers exception: ' + ev
    }, WebRTCService, 'addTransceiver', [this.id]);
  }

  getSenders() {
    return this.senders
  }

  getStats(selector) {
    return new Promise((resolve, reject) => {
      cordova.exec(function (ev) {
        resolve(new RTCStatsReport(ev));
      }, function (ev) {
        reject != null && reject(ev);
      }, WebRTCService, 'getStats', [this.id]);
    })
  }

  /**
   * Deprecated
   * This feature is no longer recommended. 
   * Though some browsers might still support it, it may have already been
   *  removed from the relevant web standards, may be in the process of
   *  being dropped, or may only be kept for compatibility purposes. 
   * Avoid using it, and update existing code if possible;
   * Be aware that this feature may cease to work at any time.
   */
  //addStream(stream) { }
}

cordova.addConstructor(function () {
  // console.log("RTCPeerConnection.js addConstructor");
  window.RTCRtpSender = RTCRtpSender;
  window.RTCPeerConnection = RTCPeerConnection;
  window.webkitRTCPeerConnection = RTCPeerConnection;

  return window.RTCPeerConnection;
});

console.log("RTCPeerConnection.js onloaded");