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

import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.element.LabelledElementLocator;
import edu.bu.ist.apps.kualiautomation.services.config.EmbeddedJettyStaticServer;

public class LabelledElementLocatorTest {
	
	private static EmbeddedJettyStaticServer server;
	private static Map<String, String> handlers = new HashMap<String, String>();
	private static LabelledElementLocator locator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		handlers.put("prop-log-lookup-frame", "ProposalLogLookupFrame.htm");
		
		server = new EmbeddedJettyStaticServer();
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
	public void findLabel() {
		locator.getDriver().get("http://localhost:8080/prop-log-lookup-frame");
		Element element = locator.locate(ElementType.TEXTBOX, "Proposal Number");
		assertNotNull(element);
		assertEquals("input", element.getWebElement().getTagName().toLowerCase());
		assertEquals("text", element.getWebElement().getAttribute("type").toLowerCase());
		assertEquals("proposalNumber", element.getWebElement().getAttribute("id"));
	}
	
	/**
	 * Find a label in a frame in an html page
	 */
	@Test 
	public void findLabelInFrame() {
		// RESUME NEXT: 
	}
	
	/**
	 * Find a field neighboring a label in a frameless html page
	 */
	@Test 
	public void findField() {
		// RESUME NEXT: 
	}
	
	/**
	 * Find a field neighboring a label in a frame in an html page
	 */
	@Test 
	public void findFieldInFrame() {
		// RESUME NEXT: 
	}
	
	
	/**
	 * Find all fields in a frame in an html page based on their labels
	 */
	@Test 
	public void findProposalLogFields() {
		// RESUME NEXT: 
	}
	

}
