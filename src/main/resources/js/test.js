function test(params){
	print(params.name)
	print(typeof test2)
	if(typeof test2 != 'undefined'){
		print('test2 exist')
	}else{
		print('test2 not exist')
	}
	return {k:'1'};
}
function test1(params){
	print(params.name)
	print(typeof test2)
	if(typeof test2 != 'undefined'){
		print('test2 exist')
	}else{
		print('test2 not exist')
	}
	return {k:'1'};
}
var o = {
		x:function(){
			print('x')
		}
};
var x = o.x;

function hello(args){
	print(args)
	//return [1,"h",{k:'v',arr:[3,4,5]}];
	return {k:'v',arr:[3,4,5]};
}
