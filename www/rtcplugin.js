// Empty constructor
function RTCPlugin() {}

// The function that passes work along to native shells
// Message is a string, duration may be 'long' or 'short'
RTCPlugin.prototype.echo = function(message, duration, successCallback, errorCallback) {
  var options = {};
  options.message = message;
  options.duration = duration;
  cordova.exec(successCallback, errorCallback, 'RTCPlugin', 'echo', [message]);
}

console.log("=== in rtcplujin.js")

// Installation constructor that binds ToastyPlugin to window
RTCPlugin.install = function() {
  if (!window.plugins) {
    window.plugins = {};
  }
  window.plugins.rtcPlugin = new RTCPlugin();
  return window.plugins.rtcPlugin;
};
cordova.addConstructor(RTCPlugin.install);


window.echo = function(str, callback) {
    cordova.exec(callback, function(err) {
        callback('Nothing to echo.');
    }, "RTCPlugin", "echo", [str]);
};

console.log("=== rtcplujin.js done")