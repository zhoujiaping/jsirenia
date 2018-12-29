(function(){
	var invoker = {};
	invoker.invoke0 = function(target,method,args){
	    return target[method]();
	};
	//这里不能用js的apply函数，因为传进来的target不是js对象。所以这里无法展开参数。
	invoker.invoke1 = function(target,method,args){
		return target[method](args[0]);
	};
	invoker.invoke2 = function(target,method,args){
	    return target[method](args[0],args[1]);
	};
	invoker.invoke3 = function(target,method,args){
	    return target[method](args[0],args[1],args[2]);
	};
	invoker.invoke4 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3]);
	};
	invoker.invoke5 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4]);
	};
	invoker.invoke6 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5]);
	};
	invoker.invoke7 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
	};
	invoker.invoke8 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7]);
	};
	invoker.invoke9 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8]);
	};
	invoker.invoke10 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9]);
	};
	invoker.invoke11 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10]);
	};
	invoker.invoke12 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11]);
	};
	invoker.invoke13 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12]);
	};
	invoker.invoke14 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12],args[13]);
	};
	invoker.invoke15 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12],args[13],args[14]);
	};
	invoker.invoke16 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12],args[13],args[14],args[15]);
	};
	invoker.invoke17 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12],args[13],args[14],args[15],args[16]);
	};
	invoker.invoke18 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12],args[13],args[14],args[15],args[16],args[17]);
	};
	invoker.invoke19 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12],args[13],args[14],args[15],args[16],args[17],args[18]);
	};
	invoker.invoke20 = function(target,method,args){
	    return target[method](args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10],args[11],args[12],args[13],args[14],args[15],args[16],args[17],args[18],args[19]);
	};
	invoker.invoke = function(target,method,args){
		var prefix = 'invoke['+target.getClass().getName()+'.'+method+']';
		print(prefix+'<==' + args);
		var res = null;
		if(args==null){
			res =  target[method]();
		}else{
			res = invoker['invoke'+args.length](target,method,args);
		}
		print(prefix+'==>' + res);
		return res;
	};
	invoker.invokeStatic = function(clazzName,method,args){
		var target = Java.type(clazzName);
		var prefix = 'invokeStatic['+clazzName+'.'+method+']';
		print(prefix+'<==' + args);
		var res = null;
		if(args==null){
			res =  target[method]();
		}else{
			res = invoker['invokeStatic'+args.length](target,method,args);
		}
		print(prefix+'==>' + res);
		return res;
	};
	return invoker;
})();