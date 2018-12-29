(function(){
	var jsInvoker = {};
	jsInvoker.invoke = function(target,method,args){
		var prefix = 'invoke['+typeof(target)+'.'+method+']';
		print(prefix+'<==' + args);
		var res = null;
		if(args==null){
			res =  target[method]();
		}else{
			res = target[method].apply(target,args);
		}
		print(prefix+'==>' + res);
		return JSON.stringify(res);
	};
	return jsInvoker;
})();