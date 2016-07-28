package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;

public class LabelElementLocatorTest extends AbstractLocatorTest {

	static {
		javascriptEnabled = false;
	}
	
	private LabelElementLocator locator;

	@Override
	public void setupBefore() {
		locator = new LabelElementLocator(driver);
	}

	@Override
	public void loadHandlers(Map<String, String> handlers) {
		handlers.put("hello1", "<html><body><div>hello<div> hello </div></div><div><input type='text'></div></body></html>");
		handlers.put("hello2", "<html><body><div> hello <div>hello</div></div><div><input type='text'></div></body></html>");
		handlers.put("hello3", "<html><body><div> hello <div></div></div><div><input type='text'></div></body></html>");
		handlers.put("similar1", "<html><body><div>matched similar<div> similarity </div></div><div><input type='text'></div></body></html>");
		handlers.put("similar2", "<html><body><div> simila </div><div><input type='text'></div></body></html>");
		handlers.put("quote", "<html><body><div> text with  single 'quote' </div></body></html>");
		handlers.put("colon1", "<html><body><span> label: </span></body></html>");
		handlers.put("colon2", "<html><body><span> :label </span></body></html>");
		handlers.put("colon3", "<html><body><span> label : : </span></body></html>");
		handlers.put("prop-log-lookup-frame", "ProposalLogLookupFrame.htm");
		handlers.put("prop-log-lookup", "ProposalLogLookup.htm");
	}
	
	@Test
	public void testFindByInnerText() {
		locator.getWebDriver().get("http://localhost:8080/hello1");
		Element element = locator.locate("hello");
		assertNotNull(element);
		assertEquals("div", element.getWebElement().getTagName().toLowerCase());
		
		locator.getWebDriver().get("http://localhost:8080/hello2");
		element = locator.locate("hello");
		assertNotNull(element);
		assertEquals("div", element.getWebElement().getTagName().toLowerCase());
		
		locator.getWebDriver().get("http://localhost:8080/hello3");
		element = locator.locate("hello");
		assertNotNull(element);
		assertEquals("div", element.getWebElement().getTagName().toLowerCase());
	}
	
	@Test
	public void testAvoidSimilarInnerText() {
		locator.getWebDriver().get("http://localhost:8080/similar1");
		Element element = locator.locate("similar");
		assertNotNull(element);
		assertEquals("similarity", element.getWebElement().getText());
		
		locator.getWebDriver().get("http://localhost:8080/similar2");
		element = locator.locate("similar");
		assertNull(element);
	}
	
	/**
	 * Accounting for whitespace and colons, the ENTIRE element innerText must match the search string, not just part of it.
	 */
	@Test
	public void testFindByInnerTextWithQuote() {
		locator.getWebDriver().get("http://localhost:8080/quote");
		Element element = locator.locate("text with single 'quote'");
		assertNotNull(element);
		assertEquals("div", element.getWebElement().getTagName().toLowerCase());
	}

	@Test
	public void testFindByInnerTextWithColon() {
		locator.getWebDriver().get("http://localhost:8080/colon1");
		Element element = locator.locate("label");
		assertNotNull(element);
		assertEquals("span", element.getWebElement().getTagName().toLowerCase());
		
		locator.getWebDriver().get("http://localhost:8080/colon2");
		element = locator.locate("label");
		assertNull(element);
		
		locator.getWebDriver().get("http://localhost:8080/colon3");
		element = locator.locate("label");
		assertNotNull(element);
		assertEquals("span", element.getWebElement().getTagName().toLowerCase());
	}
	
	@Test 
	public void findProposalLogLabel() {
		locator.getWebDriver().get("http://localhost:8080/prop-log-lookup");
		Element element = locator.locate("Proposal Number");
		assertNotNull(element);
		assertEquals("label", element.getWebElement().getTagName().toLowerCase());
		assertEquals("Proposal Number:", element.getWebElement().getText());
	}
	
	@Test 
	public void findProposalLogLabelInFrame() {
		locator.getWebDriver().get("http://localhost:8080/prop-log-lookup-frame");
		Element element = locator.locate("Proposal Number");
		assertNotNull(element);
		assertEquals("label", element.getWebElement().getTagName().toLowerCase());
		assertEquals("Proposal Number:", element.getWebElement().getText());
	}

}
