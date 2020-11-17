class AudioControl {
    constructor(){
        console.log("construct interface in plugin AudioControl")
    }
    play(){
        console.log("play interface in plugin AudioControl")
    }
    stop(){
        console.log("stop interface in plugin AudioControl")
    }
}

class VideoControl {
    constructor(){
        console.log("construct interface in plugin VideoControl")
    }
    play(){
        console.log("play interface in plugin VideoControl")
    }
    stop(){
        console.log("stop interface in plugin VideoControl")
    }
}

cordova.addConstructor(function () {
    media.AudioControl = AudioControl
    meida.VideoControl = VideoControl
});
