//内置一些函数。解决返回值问题。
function __20181228invoke(funcname,args){
	return JSON.stringify(this[funcname](args))
}
function __init(){
	load(__root+'/builtin/http.js')
}
