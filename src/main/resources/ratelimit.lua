--redis调用 eval script keysnumber keys ARGV
--例子：假设服务名称为service
--redis.eval(luascript,[],["service",System.currentTimeMillis()/1000])
--令牌桶容量，常量
local capacity = 10 
--最后一次取令牌的时间，从redis中读写
local timestampkey = ARGV[1]..':timestamp'
local tokenskey = ARGV[1]..':tokens'
local timestamp = redis.call('get',timestampkey)
local now = tonumber(ARGV[2])

if not timestamp then
	redis.call('set',timestampkey,now)
	redis.call('set',tokenskey,capacity-1)
	return 1
end
--生成令牌的速率,单位秒，常量（也可以参数传入，暂时定为常量）
local rate = 5 
--最后一次取令牌时剩余令牌数，从redis中读写
local tokens = redis.call('get',tokenskey)
--被限流的服务名称，参数传入
local service = ARGV[1]

local function grant() --获取一个令牌
	tokens = tokens+(now-timestamp)*rate
	if tokens > capacity then
		tokens = capacity
	end
	redis.call('set',ARGV[1]..':timestamp',now)
	if tokens<1 then
		redis.call('set',tokenskey,tokens)
		return 0
	else
		tokens=tokens-1
		redis.call('set',tokenskey,tokens)
		return 1
	end
end
return grant()
--[[
多行注释
注意返回值，不要使用true和false，因为redis执行lua脚本，返回true时，在java里面是1（long），
返回false时，在java里面是null。
所以，这里设计成，取到令牌返回1，没取到令牌返回0。
--]]