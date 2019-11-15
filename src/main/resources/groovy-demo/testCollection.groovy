//list
println([1,3,5,7,9])
//set
println(["1","1","2"] as Set)
//map
def map = [1:"one",2:"two","three":3,"four":4]
println(map)
println map.three
println map['three']
map['five'] = 5
println map
//array
println([1,2,3,4,5] as Integer[])

println([1,2,3,4,5][-3,-4,-1])
println([1,2,3,4,5][-3..-1])

def list = [1,2]
list+=[3,4]
list<<5
println list

def chars = ('A'..'Z')+('a'..'z')+('0'..'9')+['+','/']
println chars
