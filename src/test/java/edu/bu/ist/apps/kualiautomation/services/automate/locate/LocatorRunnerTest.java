package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import edu.bu.ist.apps.kualiautomation.AbstractJettyBasedTest;
import edu.bu.ist.apps.kualiautomation.ElementsAssertion;
import edu.bu.ist.apps.kualiautomation.entity.LabelAndValue;
import edu.bu.ist.apps.kualiautomation.services.automate.RunLog;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LocatorRunnerTest extends AbstractJettyBasedTest {

	private RunLog runlog;
	private LocatorRunner runner;
	
	@Override
	public void setupBefore() { 
		runlog = new RunLog(true);
		runner = new LocatorRunner(driver, runlog);
	}

	@Override
	public void loadHandlers(Map<String, String> handlers) {
		handlers.put("address-book-lookup", "AddressBookLookup1.htm");
		handlers.put("AddressBookLookup1_files", "AddressBookLookup1_files");
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	@Test
	public void assert01FindSearchButtonImage() {

		AbstractElementLocator.printDuration = true;
		
		LabelAndValue lv = new LabelAndValue();
		lv.setLabel("search");
		lv.setElementType(ElementType.BUTTON.name());
		
		// 1) Start a search for buttons which fails-over to a search for a hotspot
		ElementsAssertion asserter = new ElementsAssertion(runner, true);
		asserter
			.setUrl("http://localhost:8080/address-book-lookup")
			.addLabelAndValue(lv)
			.setNumResults(1)
			.addAttributeAssertion("title", "search")
			.findAndAssertElements();
		
		// 2) Search for the hotspot directly (should be faster).
		lv.setElementType(ElementType.HOTSPOT.name());
		asserter.findAndAssertElements();
	}
	
	@Test
	public void assert02FindFirstOfIdenticalLinks() {

		AbstractElementLocator.printDuration = true;
		
		LabelAndValue lv = new LabelAndValue();
		lv.setLabel("Return Value");
		lv.setElementType(ElementType.HYPERLINK.name());
		
		new ElementsAssertion(runner, true)
		.setUrl("http://localhost:8080/address-book-lookup")
		.addLabelAndValue(lv)
		.setNumResults(1)
		.setTagNameAssertion("a")
		.setTextAssertion("return value")
		.findAndAssertElements();
	}
}
