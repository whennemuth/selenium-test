package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.LabelledElementLocator;
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
	public void findButtonImageByNearestLabel() {
		findButtonImageByNearestLabel("http://localhost:8080/prop-log-lookup");
		findButtonImageByNearestLabel("http://localhost:8080/prop-log-lookup-frame");
	}
	public void findButtonImageByNearestLabel(String url) {
		
		LocateResultAssertion asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Proposal Log Status");
		asserter.setElementType(ElementType.BUTTONIMAGE);
		asserter.setNumResults(2);
		asserter.findAndAssertElements();

		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Proposal Log Status");
		asserter.addAttributeValue("Search Proposal Log Status");
		asserter.setElementType(ElementType.BUTTONIMAGE);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("title", "Search Proposal Log Status");
		asserter.findAndAssertElements();
	}
	
	@Test
	public void findButtonImageByTitle() {
		findButtonImageByTitle("http://localhost:8080/prop-log-lookup");
		findButtonImageByTitle("http://localhost:8080/prop-log-lookup-frame");
	}
	public void findButtonImageByTitle(String url) {

		LocateResultAssertion asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.addAttributeValue("Search Proposal Log Status");
		asserter.setElementType(ElementType.BUTTONIMAGE);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("title", "Search Proposal Log Status");
		asserter.findAndAssertElements();
	}
	
	private void findFields(String url) {
		
		LocateResultAssertion asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Proposal Number");
		asserter.setElementType(ElementType.TEXTBOX);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "proposalNumber");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Proposal Log Type");
		asserter.setElementType(ElementType.SELECT);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "proposalLogTypeCode");
		asserter.addAttributeAssertion("name", "proposalLogTypeCode");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Proposal Log Status");
		asserter.setElementType(ElementType.SELECT);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "logstatus");
		asserter.addAttributeAssertion("name", "logstatus");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Proposal Merged With");
		asserter.setElementType(ElementType.TEXTBOX);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "mergedwith");
		asserter.addAttributeAssertion("name", "mergedwith");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Created Institutional Proposal");
		asserter.setElementType(ElementType.TEXTBOX);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "instProposalNumber");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Proposal Type");
		asserter.setElementType(ElementType.SELECT);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "proposalTypeCode");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Title");
		asserter.setElementType(ElementType.TEXTBOX);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "title");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Principal Investigator (Employee)");
		asserter.setElementType(ElementType.TEXTBOX);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "person.username");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Principal Investigator (Non-Employee)");
		asserter.setElementType(ElementType.TEXTBOX);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "rolodexId");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Lead Unit");
		asserter.setElementType(ElementType.TEXTBOX);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "leadunit");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Sponsor");
		asserter.setElementType(ElementType.TEXTBOX);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "sponsorcode");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Sponsor Name");
		asserter.setElementType(ElementType.TEXTBOX);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "sponsorname");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Comments");
		asserter.setElementType(ElementType.TEXTBOX);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "comments");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Deadline Date From");
		asserter.setElementType(ElementType.TEXTBOX);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "rangeLowerBoundKeyPrefix_deadlineDate");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("Deadline Date To");
		asserter.setElementType(ElementType.TEXTBOX);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("id", "deadlinedate");
		asserter.findAndAssertElements();
	}
}
