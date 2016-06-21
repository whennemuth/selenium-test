package edu.bu.ist.apps.kualiautomation;

import edu.bu.ist.apps.kualiautomation.services.config.EmbeddedJettyServer;
import edu.bu.ist.apps.kualiautomation.services.config.OperatingSystem;
import edu.bu.ist.apps.kualiautomation.services.config.WebBrowser;
import edu.bu.ist.apps.kualiautomation.services.config.WebBrowserFactory;

public class ScriptBuilderGUI {

	private static OperatingSystem os = new OperatingSystem();
	
	public void open(final Integer port) throws Exception {
		
		// Start the web server
		EmbeddedJettyServer jetty = new EmbeddedJettyServer(port) {
			@Override public void onStart() throws Exception {
				// Open a web browser to a page from the web server
				WebBrowser wb = new WebBrowserFactory().getBrowser(os);
				wb.open("http://localhost" + (port == null ? "" : (":"+String.valueOf(port))) + "/main.htm");
			}
		};
		jetty.start();		
	}
	
	public static void main(String[] args) throws Exception {
		new ScriptBuilderGUI().open(8080);		
	}
}
