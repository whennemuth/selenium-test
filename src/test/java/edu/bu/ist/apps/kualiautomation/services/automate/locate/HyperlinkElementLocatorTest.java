package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.Platform;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.config.EmbeddedJettyStaticServer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HyperlinkElementLocatorTest {
	
	private static EmbeddedJettyStaticServer server;
	private static Map<String, String> handlers = new HashMap<String, String>();
	private static HyperlinkElementLocator locator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		handlers.put("hyperlink-page", "HyperlinkPage.htm");
		handlers.put("prop-log-lookup", "ProposalLogLookup.htm");
		handlers.put("prop-log-lookup-frame", "ProposalLogLookupFrame.htm");
		
		server = new EmbeddedJettyStaticServer();
		server.start(handlers);
		
		setLocator(false);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		server.stop();
		locator.getWebDriver().quit();
	}
	
	public static void setLocator(boolean specifyWindows) {			
		if(specifyWindows) {
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			capabilities.setCapability("version", "latest");
			capabilities.setCapability("platform", Platform.WINDOWS);
			capabilities.setCapability("name", "Testing Selenium");	
			capabilities.setJavascriptEnabled(true);
			locator = new HyperlinkElementLocator(new HtmlUnitDriver(capabilities)); 
		}
		else {
			locator = new HyperlinkElementLocator(new HtmlUnitDriver(BrowserVersion.FIREFOX_38, false));
		}
	}

	@Test
	public void assert01Links1and2() {
		
		String url = "http://localhost:8080/hyperlink-page";
		
		LocateResultAssertion asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 1");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a1");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor TAG 2");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a2");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.addAttributeValue("anchor tag 2");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a2");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(9);
		asserter.findAndAssertElements();
	}

	@Test
	public void assert02Links3() {
		
		String url = "http://localhost:8080/hyperlink-page";
		
		LocateResultAssertion asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 3");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(2);
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 3");
		asserter.addAttributeValue("anchor3b");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a4");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.addAttributeValue("anchor tag 3");
		asserter.addAttributeValue("anchor3a");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a3");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.addAttributeValue("anchor tag 3");
		asserter.addAttributeValue("anchor3b");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a4");
		asserter.findAndAssertElements();
	}

	@Test
	public void assert03Links4() {
		
		String url = "http://localhost:8080/hyperlink-page";
		
		LocateResultAssertion asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 4");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(2);
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 4");
		asserter.addAttributeValue("anchor4");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a5");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.addAttributeValue("anchor4");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a5");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("label 4");
		asserter.addAttributeValue("anchor tag 4");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a6");
		asserter.findAndAssertElements();
	}

	@Test
	public void assert04Links5() {
		
		String url = "http://localhost:8080/hyperlink-page";
		
		LocateResultAssertion asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 5");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(2);
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 5");
		asserter.addAttributeValue("anchor5");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a8");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.addAttributeValue("anchor5");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a8");
		asserter.findAndAssertElements();
		
		//
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("label 5a");
		asserter.addAttributeValue("anchor tag 5");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(2);
		asserter.findAndAssertElements();
	}

	@Test
	public void assert05Links6() {
		
		String url = "http://localhost:8080/hyperlink-page";

		LocateResultAssertion asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("label 6");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a9");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 6");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a9");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.addAttributeValue("anchor tag 6");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a9");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.addAttributeValue("label 6");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a9");
		asserter.findAndAssertElements();
		
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("top label");
		asserter.addAttributeValue("anchor tag 6");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a9");
		asserter.findAndAssertElements();

	}
}
