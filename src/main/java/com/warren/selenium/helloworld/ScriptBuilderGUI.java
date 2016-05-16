package com.warren.selenium.helloworld;

import com.warren.selenium.helloworld.services.EmbeddedJetty;
import com.warren.selenium.helloworld.services.OperatingSystem;
import com.warren.selenium.helloworld.services.WebBrowser;
import com.warren.selenium.helloworld.services.WebBrowserFactory;

public class ScriptBuilderGUI {

	private static OperatingSystem os = new OperatingSystem();
	
	public void open(final Integer port) throws Exception {
		
		// Start the web server
		EmbeddedJetty jetty = new EmbeddedJetty(port) {
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
