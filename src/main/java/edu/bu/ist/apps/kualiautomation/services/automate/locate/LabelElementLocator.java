package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;

public class LabelElementLocator extends AbstractElementLocator {
	
	/**
	 * 1) First priority (NOTE: lower-case if for xpath v2.0, but firefox uses v1.0, so have to use the translate function).
	 * Matches an element with a text value that matches the provided value, both values normalized for whitespace.
	 */
// RESUME NEXT: change to //*[text()[...]] and add more text/non-text node mixture test cases.
	private static final String XPATH_EQUALS = 
			"//*[normalize-space(translate(text(), "
			+ "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', "
			+ "'abcdefghijklmnopqrstuvwxyz'))=\"[INSERT-LABEL]\"]";
	
	/**
	 * 2) Second priority
	 * Matches for an element with a text value that starts with the provided value, both values normalized for whitespace.
	 */
// RESUME NEXT: change to //*[text()[...]] and add more text/non-text node mixture test cases.
	private static final String XPATH_STARTS_WITH = 
			"//*[starts-with(normalize-space(translate(text(), "
			+ "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', "
			+ "'abcdefghijklmnopqrstuvwxyz')), \"[INSERT-LABEL]\")]";
	
	/**
	 * 3) Third priority
	 * Matches for an element with a text value that contains the provided value, both values normalized for whitespace.
	 */
	private static final String XPATH_CONTAINS = 
			"//*[text()[contains(normalize-space(translate(., "
			+ "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', "
			+ "'abcdefghijklmnopqrstuvwxyz')), \"[INSERT-LABEL]\")]]";
	
	private LabelElementLocator() {
		super(null); // Restrict the default constructor
	}
	public LabelElementLocator(WebDriver driver){
		super(driver);
	}
	public LabelElementLocator(WebDriver driver, SearchContext searchContext){
		super(driver, searchContext);
	}
	
	public Element locate(String label) {
		return super.locateFirst(null, Arrays.asList(new String[]{label}));
	}
	
	@Override
	protected List<WebElement> customLocate() {
		
		List<WebElement> located = new ArrayList<WebElement>();
		String label = new String(parameters.get(0)).trim().toLowerCase();
		String cleanedLabel = ComparableLabel.getCleanedValue(label).toLowerCase();
		String scope = "";	// global
		if(searchContext instanceof WebElement) {
			scope = ".";	// current scope within element.
		}
		
		/**
		 * Search for an element that matches the provided value with xpath expressions in order of their priority.
		 * Only proceed to the next search of no results have yet been found with the prior search(es).
		 */
		
		/** 1) Search for full length match */
		String xpath = scope + XPATH_EQUALS.replace("[INSERT-LABEL]", label);
		List<WebElement> elements = searchContext.findElements(By.xpath(xpath));
		
		/** 2) Search for match that starts with the provided value */
		if(elements.isEmpty()) {
			xpath = scope + XPATH_STARTS_WITH.replace("[INSERT-LABEL]", cleanedLabel);
			elements = searchContext.findElements(By.xpath(xpath));
			
			if(!elements.isEmpty()) {
				/**
				 * The above xpath will not include text wrapped in an html block as part of the innerText
				 * being evaluated. For this reason, results could be returned that do not appear to the
				 * user to start with the specified value. Trim these edge cases off the list. 
				 */
				for (Iterator<WebElement> iterator = elements.iterator(); iterator.hasNext();) {
					WebElement we = iterator.next();
					String text = getText(driver, we);
					if(!text.toLowerCase().trim().startsWith(label.toLowerCase())) {
						iterator.remove();
					}
				}
			}
		}
		
		/** 3a) Search for a match that would start with the provided value if garbage characters 
		 * are trimmed from the start.
		 */
		if(elements.isEmpty()) {
			xpath = scope + XPATH_CONTAINS.replace("[INSERT-LABEL]", cleanedLabel);
			elements = searchContext.findElements(By.xpath(xpath));
		}

		// Wrap the web elements in ComparableLabel instances for sorting so higher ranked results are on top.
		List<ComparableLabel> labels = new ArrayList<ComparableLabel>();
		for(WebElement elmt : elements) {
			String text = getText(driver, elmt);
			labels.add(new BasicComparableLabel(elmt, label, text));
		}
		
		// Unwrap the highest ranked result(s) back into a WebElement collection.
		located.addAll(ComparableLabel.getHighestRanked(labels));
		
		// If there is no element type specified, then we assume that label indicates the innerText of the element being sought.
//		List<WebElement> elements = 
//			//driver.findElements(By.xpath(scope + "//*[text()[normalize-space(lower-case(.))=\"" + cleanedLabel.toLowerCase() + "\"]]"));
//			//driver.findElements(By.xpath(scope + "//*[normalize-space(lower-case(text()))=\"" + label.trim().toLowerCase() + "\"]"));
//			// lower-case if for xpath v2.0, but firefox uses v1.0, so have to use the translate function.
//			searchContext.findElements(By.xpath(scope + 
//					"//*[normalize-space(translate(text(), "
//					+ "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', "
//					+ "'abcdefghijklmnopqrstuvwxyz'))=\"" + label + "\"]"));
//		
//		if(elements.isEmpty()) {
//			// Find a match that starts with the specified label. This is for long labels that can be uniquely identified by the way they start.
//			elements = 
//				//driver.findElements(By.xpath(scope + "//*[text()[contains(lower-case(.), \"" + cleanedLabel.toLowerCase() + "\")]]"));
//				//driver.findElements(By.xpath(scope + "//*[starts-with(normalize-space(lower-case(text())), \"" + cleanedLabel.toLowerCase() + "\")]"));
//				// lower-case if for xpath v2.0, but firefox uses v1.0, so have to use the translate function.
//				searchContext.findElements(By.xpath(scope + 
//						"//*[starts-with(normalize-space(translate(text(), "
//						+ "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', "
//						+ "'abcdefghijklmnopqrstuvwxyz')), \"" + cleanedLabel + "\")]"));
//		}
//		
//		if(elements.isEmpty()) {
//			searchContext.findElements(By.xpath(scope + 
//					"//*[starts-with(normalize-space(translate(text(), "
//					+ "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', "
//					+ "'abcdefghijklmnopqrstuvwxyz')), \"" + cleanedLabel + "\")]"));
//		}
		
		// Double check the startswith/normalization filtering 
//		List<WebElement> filtered = new ArrayList<WebElement>();
//		Integer shortest = null;
//		for(WebElement we : elements) {
//			String cleanedElementText = getCleanedValue(getText(driver, we));
//			
//			if(cleanedElementText.toLowerCase().startsWith(cleanedLabel.toLowerCase())) {
//				if(shortest == null) {
//					shortest = cleanedElementText.length();
//				}
//				else{
//					int len = cleanedElementText.length();
//					if(shortest > len)
//						shortest = len;
//				}
//				filtered.add(we);
//			}
//		}
//		
//		// Of the filtered matches, keep the shortest match(s).
//		for(WebElement we : filtered) {
//			String cleanedElementText = getCleanedValue(getText(driver, we));
//			int len = cleanedElementText.length();
//			if(shortest.equals(len)) {
//				located.add(we);
//			}
//		}

		// We are only searching for labels, but if the search fails then the upcoming default search
		// will try to find fields. Prevent this by indicating the default search has already run.
		defaultRan = true;
		
		return located;
	}
	
	/**
	 * Clean out all parts of a string that are not to be considered during a search to determine if an elements text
	 * is a match for the search text.
	 * 
	 * @param s
	 * @return
	 */
//	public static String getCleanedValue(String s) {
//		// Normalize the spaces (trim from edges, and replace multiple contiguous whitespace with single space character)
//		String cleaned = s.trim().replaceAll("\\s+", " ");
//		// Trim off any sequence of colons and whitespace from the end of the text.
//		cleaned = cleaned.replaceAll("(\\s*:\\s*)*$", "");
//		return cleaned;
//	}

}
