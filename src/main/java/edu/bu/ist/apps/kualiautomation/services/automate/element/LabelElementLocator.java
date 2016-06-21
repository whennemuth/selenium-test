package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LabelElementLocator extends AbstractElementLocator {
	
	private LabelElementLocator() {
		super(null); // Restrict the default constructor
	}
	public LabelElementLocator(WebDriver driver){
		super(driver);
	}
	
	public Element locate(String label) {
		return super.locateFirst(null, Arrays.asList(new String[]{label}));
	}
	
	@Override
	protected void customLocate(List<WebElement> located) {
		
		String label = new String(attributes.get(0));
		String cleanedLabel = getCleanedValue(label);
		
		// If there is no element type specified, then we assume that label indicates the innerText of the element being sought.
		List<WebElement> elements = 
			//driver.findElements(By.xpath("//*[text()[contains(., \"" + cleanedLabel + "\")]]"));
			//driver.findElements(By.xpath("//*[normalize-space(text())=\"" + cleanedLabel + "\"]"));
			//driver.findElements(By.xpath("//*[text()[normalize-space(.)=\"" + cleanedLabel + "\"]]"));
		driver.findElements(By.xpath("//*[starts-with(normalize-space(text()), \"" + cleanedLabel + "\")]"));
		
		List<WebElement> filtered = new ArrayList<WebElement>();
		for(WebElement we : elements) {
			String cleanedElementText = getCleanedValue(we.getText());
			
			if(cleanedElementText.toLowerCase().startsWith(cleanedLabel.toLowerCase())) {
				filtered.add(we);
			}
		}
		located.addAll(filtered);
	}
	
	/**
	 * Clean out all parts of a string that are not to be considered during a search to determine if an elements text
	 * is a match for the search text.
	 * 
	 * @param s
	 * @return
	 */
	private String getCleanedValue(String s) {
		// Normalize the spaces (trim from edges, and replace multiple contiguous whitespace with single space character)
		String cleaned = s.trim().replaceAll("\\s+", " ");
		// Trim off any sequence of colons and whitespace from the end of the text.
		cleaned = cleaned.replaceAll("(\\s*:\\s*)*$", "");
		return cleaned;
	}

}
