package edu.bu.ist.apps.kualiautomation.services.element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import edu.bu.ist.apps.kualiautomation.services.automate.element.AbstractElementLocator;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.element.LabelledElementLocator;

public class LabelledElementLocatorTest {
	
	private static JettyServer server;
	private static Map<String, String> handlers = new HashMap<String, String>();
	private static AbstractElementLocator locator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		handlers.put("prop-log-lookup-frame", "ProposalLogLookupFrame.htm");
		
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
			locator = new LabelledElementLocator(new HtmlUnitDriver(capabilities)); 
		}
		else {
			locator = new LabelledElementLocator(new HtmlUnitDriver(BrowserVersion.FIREFOX_38, false));
		}
	}
	
	@Test 
	public void findNeighboringLabel() {
		locator.getDriver().get("http://localhost:8080/prop-log-lookup-frame");
		Element element = locator.locate("Proposal Number", ElementType.TEXTBOX);
		assertNotNull(element);
		assertEquals("input", element.getWebElement().getTagName().toLowerCase());
		assertEquals("text", element.getWebElement().getAttribute("type").toLowerCase());
		assertEquals("proposalNumber", element.getWebElement().getAttribute("id"));
	}

}
