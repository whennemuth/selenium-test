package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Locate html elements whose class attribute(s) 
 *    1) match the parameters list 
 *       OR... 
 *    2) match some of the parameters list, in which case, the remaining parameters must match attributes of the html element.
 * @author Warren
 *
 */
public class ClassBasedElementLocator extends AbstractElementLocator implements Locator {

	public ClassBasedElementLocator(WebDriver driver) {
		super(driver);
	}

	public ClassBasedElementLocator(WebDriver driver, SearchContext searchContext) {
		super(driver, searchContext);
	}

	@Override
	protected List<WebElement> customLocate() {

		List<WebElement> candidates = super.defaultLocate();
		List<WebElement> bestMatches = new ArrayList<WebElement>();
		TreeSet<RateableWebElement> ratings = new TreeSet<RateableWebElement>();
		
		for(WebElement candidate : candidates) {
			RateableWebElement rateable = new RateableWebElement(candidate, parameters);
			if(rateable.qualifies()) {
				ratings.add(rateable);
			}
		}
		
		// Remove those elements that rate lower than any other element in the set.
		do {
			if(ratings.last().compareTo(ratings.first()) == 0) {
				break;
			}
			ratings.pollFirst(); // remove the first (lowest) element
		} 
		while(true);
		
		for(RateableWebElement rateable : ratings) {
			bestMatches.add(rateable.getWebElement());
		}
		
		return bestMatches;
	}

	private static class RateableWebElement implements Comparable {
		private WebElement webElement;
		private List<String> parameters;
		private Map<String, List<String>> parmTests = new HashMap<String, List<String>>();
		private String cls; 
		private List<String> classes = new ArrayList<String>();

		public RateableWebElement(WebElement webElement, List<String> parameters) {
			this.webElement = webElement;
			this.parameters = parameters;
			cls = webElement.getAttribute("class");
			if(qualifies()) {
				cls = cls.trim();
				classes.addAll(Arrays.asList(cls.split("\\s+")));
			}
			
			process();
		}
		
		private void process() {
			// RESUME NEXT
			
		}

		public boolean qualifies() {
			if(cls == null || cls.trim().isEmpty())
				return false;
			return true;
		}

		public WebElement getWebElement() {
			return webElement;
		}

		@Override // Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object. 
		public int compareTo(Object o) {
			// TODO: finish this.
			return 0;
		}
	}
	
	
	
	
	
	
	
	
	
	
}
