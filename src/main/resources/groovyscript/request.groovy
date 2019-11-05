package groovyscript

//https://blog.csdn.net/ice00mouse/article/details/41647897
import groovyx.net.http.HTTPBuilder
//import groovyx.net.http.ContentType // this doesn't import ContentType
//import groovyx.net.http.Method // this doesn't import Method
import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseDecorator

// ContentType static import
import static groovyx.net.http.ContentType.*
// Method static import
import static groovyx.net.http.Method.*


@Grab(group='org.apache.httpcomponents', module='httpclient', version='4.2.2')

@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')

def http = new HTTPBuilder()
http.request( 'https://blog.csdn.net', GET, TEXT ) { req ->
    uri.path = '/ice00mouse/article/details/41647897'
    headers.'User-Agent' = "Mozilla/5.0 Firefox/3.0.4"
    headers.Accept = 'application/json'
    response.success = { resp, reader ->
        assert resp.statusLine.statusCode == 200
        println "Got response: ${resp.statusLine}"
        println "Content-Type: ${resp.headers.'Content-Type'}"
        println reader.text
    }
    response.'404' = {
        println 'Not found'
    }
}

