(function(){
	var javaInvoker = {};
	javaInvoker.invoke0 = function(target,method,args){
	    return target[method]();
	};
	//这里不能用js的apply函数，因为传进来的target不是js对象。所以这里无法展开参数。
	javaInvoker.invoke1 = function(target,method,args){
		return target[method](args[0]);
	};
	javaInvoker.invoke2 = function(target,method,args){
	    return target[method](args[0],args[1]);
	};
	javaInvoker.invoke3 = function(target,method,args){
	    return target[method](args[0],args[1],args[2]);
	};
	javaInvoker.invoke4 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3]);
	};
	javaInvoker.invoke5 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4]);
	};
	javaInvoker.invoke6 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5]);
	};
	javaInvoker.invoke7 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
	};
	javaInvoker.invoke8 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7]);
	};
	javaInvoker.invoke9 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8]);
	};
	javaInvoker.invoke10 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9]);
	};
	javaInvoker.invoke11 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10]);
	};
	javaInvoker.invoke12 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11]);
	};
	javaInvoker.invoke13 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12]);
	};
	javaInvoker.invoke14 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12],args[13]);
	};
	javaInvoker.invoke15 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12],args[13],args[14]);
	};
	javaInvoker.invoke16 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12],args[13],args[14],args[15]);
	};
	javaInvoker.invoke17 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12],args[13],args[14],args[15],args[16]);
	};
	javaInvoker.invoke18 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12],args[13],args[14],args[15],args[16],args[17]);
	};
	javaInvoker.invoke19 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12],args[13],args[14],args[15],args[16],args[17],args[18]);
	};
	javaInvoker.invoke20 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12],args[13],args[14],args[15],args[16],args[17],args[18],args[19]);
	};
	javaInvoker.invoke = function(target,method,args){
		var prefix = 'invoke['+target.getClass().getName()+'.'+method+']';
		print(prefix+'<==' + args);
		var res = null;
		if(args==null){
			res =  target[method]();
		}else{
			res = javaInvoker['invoke'+args.length](target,method,args);
		}
		print(prefix+'==>' + res);
		return res;
	};
	javaInvoker.invokeStatic = function(clazzName,method,args){
		var target = Java.type(clazzName);
		var prefix = 'invokeStatic['+clazzName+'.'+method+']';
		print(prefix+'<==' + args);
		var res = null;
		if(args==null){
			res =  target[method]();
		}else{
			res = javaInvoker['invoke'+args.length](target,method,args);
		}
		print(prefix+'==>' + res);
		return res;
	};
	return javaInvoker;
})();