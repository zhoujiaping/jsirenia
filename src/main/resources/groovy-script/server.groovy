//http://ju.outofmemory.cn/entry/112185
@Grab('org.eclipse.jetty.aggregate:jetty-server:8.1.19.v20160209')
@Grab('org.eclipse.jetty.aggregate:jetty-servlet:8.1.19.v20160209')
@Grab('javax.servlet:javax.servlet-api:3.0.1')
/*
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import groovy.servlet.TemplateServlet
def runServer(duration) {
    def server = new Server(8080)
    def context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS)
    context.resourceBase = "."
    context.addServlet(TemplateServlet, "*.gsp")
    server.start()
    server.join()
    //sleep duration
    //server.stop()
}

runServer(10000)
*/
/*
import javax.servlet.http.* 
import org.eclipse.jetty.server.* 
import org.eclipse.jetty.server.handler.* 
import org.eclipse.jetty.servlet.*
class DefaultHandler extends AbstractHandler {        
         void handle(String target, Request baseRequest, 
         HttpServletRequest request,HttpServletResponse response) { 
                  response.contentType = "text/html;charset=utf-8"
                  response.status = HttpServletResponse.SC_OK 
                  baseRequest.handled = true 
                  response.writer.println "<h1>Hello World!</h1>"
         } 
 } 
 Server server = new Server( 8080 ) 
 server.setHandler( new DefaultHandler() ) 
 server.start() 
 server.join()
 */
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import groovy.servlet.TemplateServlet
Server server = new Server( 8080 ) 
def context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS)
context.resourceBase = "."
context.addServlet(TemplateServlet, "*.gsp")
server.setHandler(context) 
server.start() 
server.join()