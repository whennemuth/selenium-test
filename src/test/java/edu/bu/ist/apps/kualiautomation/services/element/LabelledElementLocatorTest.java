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
		
		handlers.put("prop-log-lookup", "ProposalLogLookup.htm");
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
	
	private void findAndAssertElement(String url, ElementType et, String label, String ...otherAttributes) {
		if(locator.getDriver().getCurrentUrl() == null || !locator.getDriver().getCurrentUrl().equalsIgnoreCase(url)) {
			locator.getDriver().get(url);
		}
		
		Element element = locator.locate(et, label);
		assertNotNull(element);	
		assertEquals(et.getTagname(), element.getWebElement().getTagName().toLowerCase());
		assertTrue(areNullOrEqual(et.getTypeAttribute(), element.getWebElement().getAttribute("type")));
		for(String pair: otherAttributes) {
			String[] parts = pair.split(":");
			String attributeName = parts[0];
			String assertValue = null;
			if(parts.length > 1) {
				assertValue = pair.split(":")[1];
				if(assertValue.trim().length() == 0) {
					assertValue = null;
				}
			}
			String actualValue = element.getWebElement().getAttribute(attributeName);
			assertTrue(areNullOrEqual(assertValue, actualValue));
		}
	}
	
	private boolean areNullOrEqual(String val1, String val2) {
		if(val1 == null && val2 == null)
			return true;
		if(val1 == null || val2 == null)
			return false;
		return val1.equalsIgnoreCase(val2);
	}
	
	/**
	 * Find all fields neighboring a label in a frameless html page
	 */
	@Test 
	public void findFields() {
		findFields("http://localhost:8080/prop-log-lookup");		
	}
	
	/**
	 * Find all fields in a frame in an html page based on their labels
	 */
	@Test 
	public void findFieldsInFrame() {
		findFields("http://localhost:8080/prop-log-lookup-frame");		
	}
	
	@Test
	public void findButton() {
// RESUME NEXT: write functionality for this test		
		findAndAssertElement(
				"http://localhost:8080/prop-log-lookup-frame", 
				ElementType.BUTTONIMAGE, 
				"search proposal log status", 
				"title:Search Proposal Log Status");
		
		findAndAssertElement(
				"http://localhost:8080/prop-log-lookup-frame", 
				ElementType.BUTTONIMAGE, 
				"Proposal Log Status", 
				"title:Search Proposal Log Status");
	}
	
	private void findFields(String url) {
		findAndAssertElement(url, ElementType.TEXTBOX, "Proposal Number", "id:proposalNumber");
		findAndAssertElement(url, ElementType.SELECT, "Proposal Log Type", "id:proposalLogTypeCode", "name:proposalLogTypeCode");
		findAndAssertElement(url, ElementType.SELECT, "Proposal Log Status", "id:logstatus", "name:logstatus");
		findAndAssertElement(url, ElementType.TEXTBOX, "Proposal Merged With", "id:mergedwith", "name:mergedwith");
		findAndAssertElement(url, ElementType.TEXTBOX, "Created Institutional Proposal", "id:instProposalNumber");
		findAndAssertElement(url, ElementType.SELECT, "Proposal Type", "id:proposalTypeCode");
		findAndAssertElement(url, ElementType.TEXTBOX, "Title", "id:title");
		findAndAssertElement(url, ElementType.TEXTBOX, "Principal Investigator (Employee)", "id:person.username");
		findAndAssertElement(url, ElementType.TEXTBOX, "Principal Investigator (Non-Employee)", "id:rolodexId");
		findAndAssertElement(url, ElementType.TEXTBOX, "Lead Unit", "id:leadunit");
		findAndAssertElement(url, ElementType.TEXTBOX, "Sponsor", "id:sponsorcode");
		findAndAssertElement(url, ElementType.TEXTBOX, "Sponsor Name", "id:sponsorname");
		findAndAssertElement(url, ElementType.TEXTBOX, "Comments", "id:comments");
		findAndAssertElement(url, ElementType.TEXTBOX, "Deadline Date From", "id:rangeLowerBoundKeyPrefix_deadlineDate");
		findAndAssertElement(url, ElementType.TEXTBOX, "Deadline Date To", "id:deadlinedate");
	}
}
