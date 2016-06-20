package edu.bu.ist.apps.kualiautomation.services.element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import edu.bu.ist.apps.kualiautomation.services.automate.element.AbstractElementLocator;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;

public class AbstractElementLocatorImplTest {
	
	private static JettyServer server;
	private static Map<String, String> handlers = new HashMap<String, String>();
	private static AbstractElementLocator locator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		handlers.put("hello1", "<html><body><div>hello<div> hello </div></div><div><input type='text'></div></body></html>");
		handlers.put("hello2", "<html><body><div> hello <div>hello</div></div><div><input type='text'></div></body></html>");
		handlers.put("hello3", "<html><body><div> hello <div></div></div><div><input type='text'></div></body></html>");
		handlers.put("similar1", "<html><body><div>matched similar<div> similarity </div></div><div><input type='text'></div></body></html>");
		handlers.put("similar2", "<html><body><div> simila </div><div><input type='text'></div></body></html>");
		handlers.put("quote", "<html><body><div> text with  single 'quote' </div></body></html>");
		handlers.put("colon1", "<html><body><span> label: </span></body></html>");
		handlers.put("colon2", "<html><body><span> :label </span></body></html>");
		handlers.put("colon3", "<html><body><span> label : : </span></body></html>");
		
		server = new JettyServer();
		server.start(handlers);
		
		setLocator(false);
	}
		
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		server.stop();
		locator.getDriver().quit();
	}
		
	public static void setLocator(boolean specifyWindows) {
			
		if(specifyWindows) {
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			capabilities.setCapability("version", "latest");
			capabilities.setCapability("platform", Platform.WINDOWS);
			capabilities.setCapability("name", "Testing Selenium");	
			capabilities.setJavascriptEnabled(true);
			locator = new AbstractElementLocator(new HtmlUnitDriver(capabilities)) {
				@Override protected void customLocate(List<WebElement> located) {
					// Do nothing. We are only testing functionality of the abstract class, not custom functionality.				
				}};
		}
		else {
			locator = new AbstractElementLocator(new HtmlUnitDriver(BrowserVersion.FIREFOX_38, true)) {
				@Override protected void customLocate(List<WebElement> located) {
					// Do nothing. We are only testing functionality of the abstract class, not custom functionality.				
				}};
		}
	}
	
	@Test
	public void testFindByInnerText() {
		locator.getDriver().get("http://localhost:8080/hello1");
		Element element = locator.locate("hello");
		assertNotNull(element);
		assertEquals("div", element.getWebElement().getTagName().toLowerCase());
		
		locator.getDriver().get("http://localhost:8080/hello2");
		element = locator.locate("hello");
		assertNotNull(element);
		assertEquals("div", element.getWebElement().getTagName().toLowerCase());
		
		locator.getDriver().get("http://localhost:8080/hello3");
		element = locator.locate("hello");
		assertNotNull(element);
		assertEquals("div", element.getWebElement().getTagName().toLowerCase());
	}
	
	@Test
	public void testAvoidSimilarInnerText() {
		locator.getDriver().get("http://localhost:8080/similar1");
		Element element = locator.locate("similar");
		assertNotNull(element);
		assertEquals("similarity", element.getWebElement().getText());
		
		locator.getDriver().get("http://localhost:8080/similar2");
		element = locator.locate("similar");
		assertNull(element);
	}
	
	/**
	 * Accounting for whitespace and colons, the ENTIRE element innerText must match the search string, not just part of it.
	 */
	@Test
	public void testFindByInnerTextWithQuote() {
		locator.getDriver().get("http://localhost:8080/quote");
		Element element = locator.locate("text with single 'quote'");
		assertNotNull(element);
		assertEquals("div", element.getWebElement().getTagName().toLowerCase());
	}

	@Test
	public void testFindByInnerTextWithColon() {
		locator.getDriver().get("http://localhost:8080/colon1");
		Element element = locator.locate("label");
		assertNotNull(element);
		assertEquals("span", element.getWebElement().getTagName().toLowerCase());
		
		locator.getDriver().get("http://localhost:8080/colon2");
		element = locator.locate("label");
		assertNull(element);
		
		locator.getDriver().get("http://localhost:8080/colon3");
		element = locator.locate("label");
		assertNotNull(element);
		assertEquals("span", element.getWebElement().getTagName().toLowerCase());
	}
}