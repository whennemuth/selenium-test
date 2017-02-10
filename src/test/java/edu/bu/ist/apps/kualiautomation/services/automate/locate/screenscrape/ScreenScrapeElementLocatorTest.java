package edu.bu.ist.apps.kualiautomation.services.automate.locate.screenscrape;

import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import edu.bu.ist.apps.kualiautomation.AbstractJettyBasedTest;
import edu.bu.ist.apps.kualiautomation.ElementsAssertion;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.screenscrape.ScreenScrapeComparePattern;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.screenscrape.ScreenScrapeElementLocator;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScreenScrapeElementLocatorTest extends AbstractJettyBasedTest {

	private ScreenScrapeElementLocator locator;
	
	@Override
	public void setupBefore() { 
		locator = new ScreenScrapeElementLocator(driver);
	}

	@Override
	public void loadHandlers(Map<String, String> handlers) {
		handlers.put("screen-scrape-1", "ScreenScrapeTestPage1.htm");
	}

	@Test
	public void assert01SimpleLabel() {
		
		String url = "http://localhost:8080/screen-scrape-1";
		
		ElementsAssertion asserter = new ElementsAssertion(locator);
		asserter.setUrl(url);
		asserter.setElementType(ElementType.SCREENSCRAPE);
		asserter.setLabel("mylabel1");
		asserter.addAttributeValue(ScreenScrapeComparePattern.LABELLED_NUMBER.name());
		asserter.setNumResults(1);
		List<Element> elements = asserter.findAndAssertElements();
		
		System.out.println(elements.size());
	}
}
