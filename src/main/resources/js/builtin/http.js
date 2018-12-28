//nashorn js调用java示例
var CloseableHttpClient = Java.type('org.apache.http.impl.client.CloseableHttpClient')
var HttpClients = Java.type('org.apache.http.impl.client.HttpClients')
var HttpPost = Java.type('org.apache.http.client.methods.HttpPost')
var HttpEntity = Java.type('org.apache.http.HttpEntity')
var StringEntity = Java.type('org.apache.http.entity.StringEntity')
var EntityUtils = Java.type('org.apache.http.util.EntityUtils')

var url = 'http://www.baidu.com'
var client = HttpClients.createMinimal();
var request = new HttpPost(url);
var reqEntity = new StringEntity('', "utf-8");
request.setEntity(reqEntity);
var res = client.execute(request, function(response){
	var entity = response.getEntity();
	var json = EntityUtils.toString(entity , "utf-8" );
	return json;
});
print('===')
print(res)
print('===')