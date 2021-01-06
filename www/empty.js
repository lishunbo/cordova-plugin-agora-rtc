
// var pc = new RTCPeerConnection()
// pc.createAnswer()
// dc = pc.createDataChannel("def",{})
// pc.getStats()

var can = 'candidate:2641496685 1 udp 33562367 113.207.108.198 54328 typ relay raddr 0.0.0.0 rport 0 generation 0 ufrag RIxn network-cost 999'

var reg = new RegExp('ufrag\\s\\w*')
var result = can.match('ufrag\\s\\w*')
console.log(result)
console.log(result[0])