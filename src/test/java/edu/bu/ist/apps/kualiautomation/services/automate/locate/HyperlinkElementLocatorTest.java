package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import edu.bu.ist.apps.kualiautomation.AbstractJettyBasedTest;
import edu.bu.ist.apps.kualiautomation.ElementsAssertion;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.label.LabelledElementLocator;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HyperlinkElementLocatorTest extends AbstractJettyBasedTest {

	static {
		// This test operates on iframe content directly without outer page and its scripts present.
		// Normally this would cause a javascript exception due to the missing scripts, so suppress. 
		javascriptIgnoreExceptions = true;		
	}

	private HyperlinkElementLocator locator;
	
	@Override
	public void setupBefore() { 
		locator = new HyperlinkElementLocator(driver);
	}

	@Override
	public void loadHandlers(Map<String, String> handlers) {
		handlers.put("hyperlink-page", "HyperlinkPage.htm");
		handlers.put("prop-log-lookup-frame", "ProposalLogLookup_files/ProposalLogLookupFrame.htm");
		handlers.put("prop-log-lookup", "ProposalLogLookup.htm");
		handlers.put("ProposalLogLookup_files", "ProposalLogLookup_files");	
		handlers.put("address-book-lookup", "AddressBookLookup1.htm");
		handlers.put("AddressBookLookup_files", "AddressBookLookup1_files");
	}

	@Test
	public void assert01Links1and2() {
		
		String url = "http://localhost:8080/hyperlink-page";
		
		ElementsAssertion asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 1");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a1");
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor TAG 2");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a2");
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.addAttributeValue("anchor tag 2");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a2");
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(9);
		asserter.findAndAssertElements();
	}

	@Test
	public void assert02Links3() {
		
		String url = "http://localhost:8080/hyperlink-page";
		
		ElementsAssertion asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 3");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(2);
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 3");
		asserter.addAttributeValue("anchor3b");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a4");
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.addAttributeValue("anchor tag 3");
		asserter.addAttributeValue("anchor3a");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a3");
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
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
		
		ElementsAssertion asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 4");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(2);
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 4");
		asserter.addAttributeValue("anchor4");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a5");
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.addAttributeValue("anchor4");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a5");
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
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
		
		ElementsAssertion asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 5");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(2);
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 5");
		asserter.addAttributeValue("anchor5");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a8");
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.addAttributeValue("anchor5");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a8");
		asserter.findAndAssertElements();
		
		//
		asserter = new ElementsAssertion(locator);
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

		ElementsAssertion asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("label 6");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a9");
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("anchor tag 6");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a9");
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.addAttributeValue("anchor tag 6");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a9");
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.addAttributeValue("label 6");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a9");
		asserter.findAndAssertElements();
		
		asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.setLabel("top label");
		asserter.addAttributeValue("anchor tag 6");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.addAttributeAssertion("testid", "a9");
		asserter.findAndAssertElements();

	}
	
	@Test
	public void assert06TableColumnLinks() {
		long start = System.currentTimeMillis();
		
		LabelledElementLocator locator = new LabelledElementLocator(driver);
		
		new ElementsAssertion(locator)
			.setUrl("http://localhost:8080/address-book-lookup")
			.setLabel("search")
			.setElementType(ElementType.BUTTON)
			.setNumResults(1)
			.addAttributeAssertion("title", "search")
			.findAndAssertElements();
		
		long end = System.currentTimeMillis();
		
		Long duration = (end - start) / 1000L;
		
		System.out.println("duration is " + duration.toString() + " seconds");
	}
}
