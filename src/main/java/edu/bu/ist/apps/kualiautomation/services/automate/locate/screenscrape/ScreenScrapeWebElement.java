package edu.bu.ist.apps.kualiautomation.services.automate.locate.screenscrape;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.services.automate.element.AbstractWebElement;

/**
 * This class adapts {@link org.openqa.selenium.WebElement} to change the value getText() returns.
 * The value to return for getText() becomes the value sought through "screen scraping" and not the entire
 * innerText of the WebElement that scraped value was nested in. For example:
 * <pre style="font: inherit">{@code
 * 
 *    WebElement.text(): "This is the inner text with a nested mylabel: myvalue substring nested inside"
 *    
 *    If screen scraping was specified to find an unbroken sequence of alpha characters labelled with the term "mylabel:", 
 *    then...
 *    
 *    ScreenScrapeWebElement.text(): "myvalue"
 *    
 * }</pre>
 * @author wrh
 *
 */
public class ScreenScrapeWebElement extends AbstractWebElement {

	private ComparableScreenScrape screenscrape;
	private Set<String> candidates = new HashSet<String>();

	public ScreenScrapeWebElement(WebElement webElement, ComparableScreenScrape screenscrape) {
		super(webElement);
		this.screenscrape = screenscrape;
		if(!screenscrape.getScrapedValues().isEmpty()) {
			// Add scraped values to a set to eliminate duplicates
			candidates.addAll(screenscrape.getScrapedValues());
		}
	}

	@Override
	public String getText() {
		
		if(candidates.isEmpty())
			return screenscrape.getRawText(); // This wont' be what we are looking for (maybe should return null?)
		else
			// return the first element
			return candidates.toArray(new String[candidates.size()])[0];
	}
	
	/**
	 * @return True if more than one screen scrape value was matched.
	 */
	public boolean isIndeterminate() {
		return candidates.size() > 1;
	}
	
	public List<String> getCandidates() {
		return Arrays.asList(candidates.toArray(new String[candidates.size()]));
	}
}
