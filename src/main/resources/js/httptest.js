(function(){
	var module = {};
	module.test = function(){
		return http.post('http://www.baidu.com','');
	};
	module.testjson = function(){
		return [{a:'b'},{},1,'1'];
	};
	return module;
})();