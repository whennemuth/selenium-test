package edu.bu.ist.apps.kualiautomation.services.element;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;

public class BasicElementLocatorImplTest {
	
	private static JettyServer server;
	private static Map<String, String> handlers = new HashMap<String, String>();
	private static Locator locator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		handlers.put("hello", "<html><body><div>hello<div> hello </div></div><div><input type='text'></div></body></html>");
		handlers.put("quote", "<html><body><div>text with ' mark</div></body></html>");
		
		server = new JettyServer();
		server.start(handlers);
		
		setLocator(false);
	}
		
	public static void setLocator(boolean specifyWindows) {
		locator = new LabelledElementLocator(new HtmlUnitDriver(true));
		if(specifyWindows) {
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			capabilities.setCapability("version", "latest");
			capabilities.setCapability("platform", Platform.WINDOWS);
			capabilities.setCapability("name", "Testing Selenium");	
			locator = new LabelledElementLocator(new HtmlUnitDriver(capabilities)); 
		}
		else {
			locator = new LabelledElementLocator(new HtmlUnitDriver(BrowserVersion.FIREFOX_38));
		}
	}
	
	@Test
	public void testSimpleLabel() {
		locator.getDriver().get("http://localhost:8080/hello");
		Element element = locator.locate("hello", ElementType.TEXTBOX);
		assertNotNull(element);
		assertEquals("div", element.getWebElement().getTagName().toLowerCase());
	}
	
	@Test
	public void testLabelWithQuote() {
		locator.getDriver().get("http://localhost:8080/quote");
		Element element = locator.locate("text with ' mark", ElementType.TEXTBOX);
		assertNotNull(element);
		assertEquals("div", element.getWebElement().getTagName().toLowerCase());
	}
	
	@Test void findAdjacentLabel() {
		// RESUME NEXT
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		server.stop();
		locator.getDriver().quit();
	}

}
