package edu.bu.ist.apps.kualiautomation.services.config;

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

/**
 * This jetty server does not work off a web context.
 * Instead, content is issued by matching the last portion of the target url with a map of preconfigured output.
 * The target value is the key to the map and the values of the map are either raw html, or a classpath indicator
 * to a resource that contains html.
 * Useful for unit testing.
 * 
 * @author wrh
 *
 */
public class EmbeddedJettyStaticServer {

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
				
				if(source != null && !source.contains("<")) {
					// source is not raw html, but a reference to a classpath resource that contains html
					source = Utils.getClassPathResourceContent(
							"html/" + source);
				}
				
				out.println(source == null ? target : source);
				baseRequest.setHandled(true);
			}});
	}
	
	public void stop() throws Exception {
		server.stop();
		server.destroy();
	}
}
