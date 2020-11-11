def parse(String xml){
    def parser = new XmlParser()
    def services = []
    def beans = parser.parseText(xml)
    beans['dubbo:service'].each{
        def service = [:]
        service['interface'] = it['@interface']
        service['group'] = it['@group']
        service['ref']= it['@ref']
        services<<service
    }
    services
}

def xml = '''
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
xmlns:context="http://www.springframework.org/schema/context" xmlns:p="http://www.springframework.org/schema/p"
xsi:schemaLocation="
    http://www.springframework.org/schema/beans  http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
    http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd
">

    <!-- 配置系统应用名称 --> 
    <dubbo:application name="{APPLICATION_NAME}" />
    
    <!--通过注册中心发现监控中心服务 -->
    <dubbo:monitor protocol="registry" />
    <!--dubbo monitor service -->
    <monitor:config applicationCode="{APPLICATION_NAME}" />
    <!-- 配置注册中心地址 -->
    <dubbo:registry protocol="zookeeper" address="{ZOOKEEPER_ADDRESS}" file="/tomcat/logs/{project.name}/.dubbo/{project.artifactId}/dubbo.cache" default="true"/>
    
    <!--hession协议暴露服务　-->
    <dubbo:protocol name="hessianProxy" port="{APPLICATION_PORT}" server="servlet" contextpath="{APPLICATION_NAME}/dubbo"/>
    
    <!-- ********************RPC服务提供者******************** -->
    <dubbo:service interface="a.b.c.d.HelloService" ref="helloService"/>
    
    <dubbo:service  protocol="hessianProxy" interface="a.b.c.d.DemoService" ref="demoService"
                   timeout="60000" retries="0" loadbalance="random"/>
    <!-- ********************RPC服务消费者******************** -->
    <!-- 启动时不检查是否有可用服务 -->
    <dubbo:consumer check="false"/>
    <dubbo:reference id="smsService" interface="a.b.c.d.SMSService" />
    <dubbo:reference id="mailService" interface="a.b.c.d.MailService"/>
</beans> 
'''

def beans = parse(xml)

println beans

