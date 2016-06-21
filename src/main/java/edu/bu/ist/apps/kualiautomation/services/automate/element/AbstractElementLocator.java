package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;

public abstract class AbstractElementLocator implements Locator {

	protected WebDriver driver;
	protected String label;
	protected ElementType elementType;
	
	public AbstractElementLocator(WebDriver driver) {
		this.driver = driver;
	}
	
	@Override
	public Element locate(String label, ElementType elementType) {
		
		this.label = label;
		this.elementType = elementType;
		final List<WebElement> results = new ArrayList<WebElement>();
		
		customLocate(results);
		
		if(results.isEmpty()) {
			defaultLocate(results);
			if(results.isEmpty()) {
				return null;
			}
		}
		
		return new BasicElementImpl(driver, results.get(0));
	}
	
	public Element locate(String label) {
		return locate(label, null);
	}
	
	protected void defaultLocate(List<WebElement> located) {
		
		if(elementType == null) {
			locateLabels(located);
		}
		else {
			/**
			 * The provided label indicates an attribute of the element being sought.
			 * Attributes are evaluated in the following order, with the element of the first matching attribute being returned:
			 *    1) id
			 *    2) name
			 */
			switch(elementType) {
			case BUTTON:
				break;
			case CHECKBOX:
				break;
			case HYPERLINK:
				break;
			case TEXTBOX:
				break;
			case TEXTAREA:
				break;
			case SELECT:
				break;
			case RADIO:
				break;
			case OTHER:
				break;
			}
		}
	}
	
	protected void locateLabels(List<WebElement> located) {
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
	
	protected abstract void customLocate(List<WebElement> located);

	@Override
	public WebDriver getDriver() {
		return driver;
	}

}
