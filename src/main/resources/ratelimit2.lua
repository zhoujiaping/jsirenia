--redis调用 eval script keysnumber keys ARGV
--例子：假设服务名称为service
--redis.eval(luascript,[],["service",System.currentTimeMillis()/1000])
--生成令牌的速率,单位秒，常量（也可以参数传入，暂时定为常量）
local rate = 5 

--被限流的服务名称，参数传入
local service = ARGV[1]

local tokenskey = service..':tokens'
--最后一次取令牌时剩余令牌数，从redis中读写
local tokens = tonumber(redis.call('get',tokenskey))
--令牌桶容量，常量
local capacity = 10 
--最后一次取令牌的时间，从redis中读写
local timestampkey = service..':timestamp'
local timestamp = redis.call('get',timestampkey)
local now = tonumber(ARGV[2])
if tokens and tokens>0 then --这种情况发生的概率最大，绝大多数情况满足，只调用一次redis
	tokens = tokens - 1
	redis.call('set',tokenskey,tokens)
	return 1
elseif tokens and tokens<1 then --令牌不够了
	tokens = tokens+(now-timestamp)*rate --重新计算令牌数
	if tokens>capacity then --如果溢出，低并发的情况发生
		tokens = capacity-1
		redis.call('set',timestampkey,now)
		redis.call('set',tokenskey,tokens)
		return 2
	elseif tokens<1 then --如果没有了，高并发的情况可能发生
		return 0
	else
		tokens = tokens - 1 --如果还有
		redis.call('set',timestampkey,now)
		redis.call('set',tokenskey,tokens)
		return 3
	end
else --tokens不存在，还没初始化，只调用一次
	tokens = capacity-1
	redis.call('set',timestampkey,now)
	redis.call('set',tokenskey,tokens)
	return 4
end

--[[
优化版
1、获取令牌，如果能取到，就不更新时间戳。只有在令牌没了的时候，在重新计算一次令牌，并且更新时间戳。
多行注释
注意返回值，不要使用true和false，因为redis执行lua脚本，返回true时，在java里面是1（long），
返回false时，在java里面是null。
所以，这里设计成，取到令牌返回值大于零，没取到令牌返回0。
--]]