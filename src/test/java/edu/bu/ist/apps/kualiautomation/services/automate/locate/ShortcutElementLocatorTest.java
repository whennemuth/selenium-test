package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.Platform;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.config.EmbeddedJettyStaticServer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShortcutElementLocatorTest {
	private static EmbeddedJettyStaticServer server;
	private static Map<String, String> handlers = new HashMap<String, String>();
	private static ShortcutElementLocator locator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		handlers.put("welcome-page", "welcome.htm");
		handlers.put("/welcome_files", null);
//		No source found for: /welcome_files/kboot.2.5.3.1603.0001-kualico.min.css
//		No source found for: /welcome_files/core.css
//		No source found for: /welcome_files/bootstrap-select.css
//		No source found for: /welcome_files/multiselect.css
//		No source found for: /welcome_files/landingPage.css
//		No source found for: /welcome_files/kboot.2.5.3.1603.0001-kualico.min.js
//		No source found for: /welcome_files/global.js
//		No source found for: /welcome_files/bootstrap-select.js
//		No source found for: /welcome_files/multiselect.js
//		No source found for: /welcome_files/lineItemTable.js
//		No source found for: /welcome_files/KualiLogo.png
//		No source found for: /welcome_files/KualiCo.png
//		No source found for: /favicon.ico
		
		server = new EmbeddedJettyStaticServer();
		server.start(handlers);
		
		setLocator(false);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		server.stop();
		locator.getDriver().quit();
		System.out.println("Jetty server stopped");
	}
	
	public static void setLocator(boolean specifyWindows) {	
		
//		locator = new ShortcutElementLocator(new FirefoxDriver());
//		if(true) {
//			return;
//		}
		
		if(specifyWindows) {
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			capabilities.setCapability("version", "latest");
			capabilities.setCapability("platform", Platform.WINDOWS);
			capabilities.setCapability("name", "Testing Selenium");	
			capabilities.setJavascriptEnabled(true);
			locator = new ShortcutElementLocator(new HtmlUnitDriver(capabilities));
		}
		else {
			locator = new ShortcutElementLocator(new HtmlUnitDriver(BrowserVersion.FIREFOX_38, true));
		}
	}

	@Test
	public void test() {
		String url = "http://localhost:8080/welcome-page";
		LocateResultAssertion asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
//		asserter.setLabel("anchor tag 1");
//		asserter.setElementType(ElementType.HYPERLINK);
//		asserter.setNumResults(1);
//		asserter.addAttributeAssertion("testid", "a1");
		asserter.findAndAssertElements();
	}

}
