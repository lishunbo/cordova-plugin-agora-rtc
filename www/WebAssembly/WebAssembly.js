

console.log("webassembly.js onloading");

var WebAssembly = {}

var wasmexport = {};

wasmexport.memory.buffer = new Array[10];
wasmexport.signmanager_new = function(){
    console.log("CustomWSAM: new:")
}
wasmexport.__wbg_signmanager_free = function(ptr){
    console.log("CustomWSAM: free:"+ptr)
}
wasmexport.signmanager_get_digest=function(ptr, obj){
    console.log("CustomWSAM: digest:"+ptr+":"+obj)
}
wasmexport.__wbindgen_malloc=function(){}
wasmexport.__wbindgen_malloc=function(){}

class wasm {
    constructor(){
        this.instance.exports = wasmexport;
        this.module=null;
    }
}

// first class
WebAssembly.instantiate = function (buffer, imports) {
    return new Promise((resolve, reject) => {
        resolve(new wasm());
    });
}



module.exports = WebAssembly;

console.log("webassembly.js onload");