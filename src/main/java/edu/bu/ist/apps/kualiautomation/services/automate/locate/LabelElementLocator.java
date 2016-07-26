package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;

public class LabelElementLocator extends AbstractElementLocator {
	
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
		String label = new String(parameters.get(0));
		String cleanedLabel = getCleanedValue(label);
		String scope = "";	// global
		if(searchContext instanceof WebElement) {
			scope = ".";	// current scope within element.
		}
		
		// If there is no element type specified, then we assume that label indicates the innerText of the element being sought.
		List<WebElement> elements = 
			//driver.findElements(By.xpath(scope + "//*[text()[normalize-space(lower-case(.))=\"" + cleanedLabel.toLowerCase() + "\"]]"));
			//driver.findElements(By.xpath(scope + "//*[normalize-space(lower-case(text()))=\"" + label.trim().toLowerCase() + "\"]"));
			// lower-case if for xpath v2.0, but firefox uses v1.0, so have to use the translate function.
			searchContext.findElements(By.xpath(scope + "//*[normalize-space(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'))=\"" + label.trim().toLowerCase() + "\"]"));
		
		if(elements.isEmpty()) {
			// Find a match that starts with the specified label. This is for long labels that can be uniquely identified by the way they start.
			elements = 
				//driver.findElements(By.xpath(scope + "//*[text()[contains(lower-case(.), \"" + cleanedLabel.toLowerCase() + "\")]]"));
				//driver.findElements(By.xpath(scope + "//*[starts-with(normalize-space(lower-case(text())), \"" + cleanedLabel.toLowerCase() + "\")]"));
				// lower-case if for xpath v2.0, but firefox uses v1.0, so have to use the translate function.
				searchContext.findElements(By.xpath(scope + "//*[starts-with(normalize-space(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')), \"" + cleanedLabel.toLowerCase() + "\")]"));
		}
		
		// Double check the startswith/normalization filtering 
		List<WebElement> filtered = new ArrayList<WebElement>();
		Integer shortest = null;
		for(WebElement we : elements) {
			String cleanedElementText = getCleanedValue(we.getText());
			
			if(cleanedElementText.toLowerCase().startsWith(cleanedLabel.toLowerCase())) {
				if(shortest == null) {
					shortest = cleanedElementText.length();
				}
				else{
					int len = cleanedElementText.length();
					if(shortest > len)
						shortest = len;
				}
				filtered.add(we);
			}
		}
		
		// Of the filtered matches, keep the shortest match(s).
		for(WebElement we : filtered) {
			String cleanedElementText = getCleanedValue(we.getText());
			int len = cleanedElementText.length();
			if(shortest.equals(len)) {
				located.add(we);
			}
		}

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
	public static String getCleanedValue(String s) {
		// Normalize the spaces (trim from edges, and replace multiple contiguous whitespace with single space character)
		String cleaned = s.trim().replaceAll("\\s+", " ");
		// Trim off any sequence of colons and whitespace from the end of the text.
		cleaned = cleaned.replaceAll("(\\s*:\\s*)*$", "");
		return cleaned;
	}

}
