function test(params){
	print(params.name)
	print(typeof test2)
	if(typeof test2 != 'undefined'){
		print('test2 exist')
	}else{
		print('test2 not exist')
	}
	return JSON.stringify({k:'1'});
}
function test1(params){
	print(params.name)
	print(typeof test2)
	if(typeof test2 != 'undefined'){
		print('test2 exist')
	}else{
		print('test2 not exist')
	}
	return JSON.stringify({k:'1'});
}
var o = {
		x:function(){
			print('x')
		}
};
var x = o.x;