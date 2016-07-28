package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.Map;

import org.junit.Test;

import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;

public class LabelledElementLocatorTest extends AbstractLocatorTest {
	
	private LabelledElementLocator locator;

	static {
		javascriptEnabled = false;
	}
	
	@Override
	public void setupBefore() {
		locator = new LabelledElementLocator(driver);
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
