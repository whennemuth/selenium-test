package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Attribute;
import edu.bu.ist.apps.kualiautomation.services.automate.element.BasicElement;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;

public abstract class AbstractElementLocator implements Locator {

	protected WebDriver driver;
	protected ElementType elementType;
	protected List<String> parameters = new ArrayList<String>();
	protected boolean defaultRan;
	
	public AbstractElementLocator(WebDriver driver) {
		this.driver = driver;
	}
	
	@Override
	public Element locateFirst(ElementType elementType, List<String> parameters) {
		
		List<Element> results = locateAll(elementType, parameters);
		if(results.isEmpty())
			return null;
		
		return results.get(0);
	}
	
	@Override
	public List<Element> locateAll(ElementType elementType, List<String> parameters) {
		defaultRan = false;
		this.elementType = elementType;
		this.parameters = parameters;
		final List<WebElement> webElements = new ArrayList<WebElement>();
		List<Element> results = new ArrayList<Element>();
		
		List<WebElement> custom = customLocate();
		webElements.addAll(custom);
		
		if(webElements.isEmpty()) {
			List<WebElement> defaults = defaultLocate();
			webElements.addAll(defaults);
		}
		
		if(webElements.isEmpty()) {
			// Check for frames and search those as well
			WebDriverWait wait = new WebDriverWait(driver, 100);
			List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
			if(!iframes.isEmpty()) {
				for(WebElement iframe : iframes) {
					driver = wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframe));
					List<Element> frameResults = locateAll(elementType, parameters);
					results.addAll(frameResults);
					// Don't switch back to the parent window because you will not be able to use the WebElement as it would 
					// then belong to a frame that the WebDriver is longer focused on ( you will get a StaleElementReferenceException ).
					// driver.switchTo().defaultContent();
				}
			}
		}
		else {
			for(WebElement we : webElements) {
				results.add(new BasicElement(driver, we));
			}
		}
		
		return results;
	}
	
	public void setDefaultRan(boolean defaultRan) {
		this.defaultRan = defaultRan;
	}
	
	protected abstract List<WebElement> customLocate();
	
	protected List<WebElement> defaultLocate() {
		
		List<WebElement> results = new ArrayList<WebElement>();
		
		if(defaultRan || parameters.isEmpty() || elementType == null)
			return results;
		
		switch(elementType) {
		case BUTTON:
			break;
		case BUTTONIMAGE:
			String attributeValue = parameters.get(0);
			List<WebElement> candidates = elementType.findAll(driver);
			results.addAll(Attribute.findForValue(candidates, attributeValue));
			break;
		case CHECKBOX:
			break;
		case HYPERLINK:
			break;
		case TEXTBOX:
			// RESUME NEXT: write code here.
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
		
		defaultRan = true;
		
		return results;
	}

	@Override
	public WebDriver getDriver() {
		return driver;
	}

}
