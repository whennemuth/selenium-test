package edu.bu.ist.apps.kualiautomation.services.element;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import edu.bu.ist.apps.kualiautomation.util.Utils;

public class JettyServer {

	private Server server;
	private Map<String, String> handlers = new HashMap<String, String>();
	
	public void start(Map<String, String> handlers) throws Exception {
		this.handlers = handlers;

        server = new Server(8080);
        try {
        	server.setStopAtShutdown(true);
            addHandler();
			server.start();
			server.dumpStdErr();
			// Don't use join as it causes this thread to wait for the completion of the 
			// server thread before it advances to the next line of code.
			// server.join();
		} 
        catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addHandler() {
        server.setHandler(new AbstractHandler(){
			@Override public void handle(
					String target, 
					Request baseRequest, 
					HttpServletRequest request, 
					HttpServletResponse response) throws IOException, ServletException {
				
				response.setContentType("text/html; charset=utf-8");
				response.setStatus(HttpServletResponse.SC_OK);
				
				PrintWriter out = response.getWriter();
				String[] parts = target.split("/");
				target = parts[parts.length-1];
				String source = handlers.get(target);
				
				if(!source.contains("<")) {
					// source is not raw html, but a reference to a classpath resource that contains html
					source = Utils.getClassPathResourceContent(
							"edu/bu/ist/apps/kualiautomation/services/element/html/" + source);
				}
				
				out.println(source);
				baseRequest.setHandled(true);
			}});
	}
	
	public void stop() throws Exception {
		server.stop();
		server.destroy();
	}
}
