package edu.bu.ist.apps.kualiautomation.services.config;

import java.io.File;
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
		
		loadHandlers("html/", handlers);
		
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

	private void loadHandlers(String dir, Map<String, String> handlers) {
		
		for(String source : handlers.keySet()) {
			String target = handlers.get(source);
			
			if(target == null)
				target = source;
			
			if(dir != null) {
				target = dir + target;
				target = target.replaceAll("//", "/");
			}
// RESUME NEXT: This is not working, fix it.			
			File parent = Utils.getClassPathResource(target);
			if(parent.isDirectory()) {
				Map<String, String> subhandlers = new HashMap<String, String>();
				for(File child : parent.listFiles()) {
					if(child.isDirectory()) {
						subhandlers.put(target + "/" + child.getName(), null);
					}
					else {
						this.handlers.put(parent.getName() + "/" + child.getName(), null);
						System.out.println(parent.getName() + "/" + child.getName() + " = null");
					}
				}
				if(!subhandlers.isEmpty()) {
					loadHandlers(null, subhandlers);
				}
			}
			else {
				this.handlers.put(source, target);
				System.out.println(source + " = " + target);
			}
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
				String page = parts[parts.length-1];
				String source = handlers.get(page);
				
				if(source == null) {
					System.out.println("No source found for: " + target);
				}
				else if(!source.contains("<")) {
					File f = Utils.getClassPathResource(source);
					if(f.isDirectory()) {
						System.out.println(f.getAbsolutePath());
					}
					// source is not raw html, but a reference to a classpath resource that contains html
					source = Utils.getClassPathResourceContent(source);
				}
				
				out.print(source == null ? target : source);
				out.flush();
				baseRequest.setHandled(true);
			}});
	}
	
	public void stop() throws Exception {
		server.stop();
		server.destroy();
	}
}
