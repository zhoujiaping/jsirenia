(function(global){
	var __starter = {};
	__starter.__invoke = function(obj,funcName,args){
		return JSON.stringify(obj[funcName](args))
	};
	__starter.__init = function(){
		//global.http = load(__root+'/builtin/http.js')
		//global.file = load(__root+'/builtin/file.js')
		//global.mysql = load(__root+'/builtin/mysql.js')
	};
	__starter.__init();
	return __starter;
})(this);
