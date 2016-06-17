package edu.bu.ist.apps.kualiautomation.services.config;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.util.component.AbstractLifeCycle.AbstractLifeCycleListener;
import org.eclipse.jetty.util.component.LifeCycle;

public abstract class EmbeddedJetty {

	private static Server server;
	private static WebAppContext context;
	
	public EmbeddedJetty() {
		this(null);
	}
	
	public EmbeddedJetty(Integer port) {
		
		// Instantiate the web server and define what extra tasks it must do when it starts up.
    	String webappDirLocation = "src/main/webapp/";
    	server = new Server(port);
        server.addLifeCycleListener(new AbstractLifeCycleListener(){
			@Override
			public void lifeCycleStarted(LifeCycle event) {
				super.lifeCycleStarted(event);
				try {
					onStart();
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}});
        
        // Instantiate the web app context
        context = new WebAppContext();
        context.setContextPath("/");
        context.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        context.setResourceBase(webappDirLocation);

        // Parent loader priority is a class loader setting that Jetty accepts.
        // By default Jetty will behave like most web containers in that it will
        // allow your application to replace non-server libraries that are part of the
        // container. Setting parent loader priority to true changes this behavior.
        // Read more here: http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
        context.setParentLoaderPriority(true);
        server.setHandler(context);
	}
	
	public void start() throws Exception {
        server.start();
        server.dumpStdErr();
        server.join();
	}
	
	public void stop() throws Exception {
		server.stop();
		server.destroy();
	}
	
	public abstract void onStart() throws Exception;
	
	public static void main(String[] args) throws Exception {
		EmbeddedJetty jetty = new EmbeddedJetty(8080){
			@Override public void onStart() {
				System.out.println("Jetty started!!!");
			}};
		jetty.start();
		jetty.stop();
	}
}
