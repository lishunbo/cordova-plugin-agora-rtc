
console.log("player.js onloading");


class VideoPlayer {
    constructor(config) {
        console.log("construct interface in plugin VideoControl", config)
    }
    udpateConfig(config) {
        console.log("udpateConfig interface in plugin VideoControl", config)
    }
}

cordova.addConstructor(function () {
    if (!window.plugins){
        window.plugins = {}
    }

    console.log("nativeplayer.js addConstructor")
});

/**
 * @module AgoraPlugins
 */
module.exports = {
    VideoPlayer,
}
console.log("player.js onloaded");