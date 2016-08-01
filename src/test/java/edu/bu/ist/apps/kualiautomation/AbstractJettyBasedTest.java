package edu.bu.ist.apps.kualiautomation;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import edu.bu.ist.apps.kualiautomation.services.config.EmbeddedJettyStaticServer;

/**
 * All test classes that extend this class can access a website run by this class and whose web pages 
 * those test classes define, either by configuring this class with static html or a classpath reference
 * to a file that contains html and a URL. 
 * This class will start a Jetty web server upon execution of the first test (and keep it running for
 * all subsequent tests) and serve up those web pages.
 * 
 * @author wrh
 *
 */
public abstract class AbstractJettyBasedTest {
	
	private static Map<String, String> handlers = new HashMap<String, String>();	
	private static EmbeddedJettyStaticServer server;
	private static boolean serverRunning;
	protected static boolean javascriptEnabled = true;
	private static boolean headless;
	private static boolean windows;
	
	protected WebDriver driver;

	@Before
	public void setUpBefore() throws Exception {
		
		checkServer();
		
		if(headless) {
			if(windows) {
				DesiredCapabilities capabilities = DesiredCapabilities.firefox();
				capabilities.setCapability("version", "latest");
				capabilities.setCapability("platform", Platform.WINDOWS);
				capabilities.setCapability("name", "Testing Selenium");	
				capabilities.setJavascriptEnabled(javascriptEnabled);
				driver = new HtmlUnitDriver(capabilities);
			}
			else {
				driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_38, javascriptEnabled);
			}
		}
		else {
			driver = new FirefoxDriver();
		}
		
		setupBefore();
	}
	
	private void checkServer() throws Exception {
		if(!serverRunning) {
			headless = !Boolean.valueOf(System.getenv("showbrowser"));
			windows = Boolean.valueOf(System.getenv("windows"));
			
			server = new EmbeddedJettyStaticServer();
			loadHandlers(handlers);
			server.start(handlers);		
			serverRunning = true;
		}
	}
	
	public abstract void setupBefore();
	
	public abstract void loadHandlers(Map<String, String> handlers);
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		server.stop();
		System.out.println("Jetty server stopped");
		serverRunning = false;
		javascriptEnabled = true;
	}

	@After
	public void tearDownAfter() {
		driver.quit();		
	}

}
