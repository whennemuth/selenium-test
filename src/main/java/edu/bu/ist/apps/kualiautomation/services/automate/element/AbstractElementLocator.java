package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
		
		this.elementType = elementType;
		this.parameters = parameters;
		final List<WebElement> webElements = new ArrayList<WebElement>();
		List<Element> results = new ArrayList<Element>();
		
		customLocate(webElements);
		
		if(webElements.isEmpty()) {
			defaultLocate(webElements);
		}
		
		if(!webElements.isEmpty()) {
			for(WebElement we : webElements) {
				results.add(new BasicElement(driver, we));
			}
		}
		
		if(webElements.isEmpty()) {
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
		
		return results;
	}
	
	public void setDefaultRan(boolean defaultRan) {
		this.defaultRan = defaultRan;
	}
	
	protected void defaultLocate(List<WebElement> located) {
		
		if(defaultRan || parameters.isEmpty() || elementType == null)
			return;
		
		switch(elementType) {
		case BUTTON:
			break;
		case BUTTONIMAGE:
			
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
		
		defaultRan = true;
	}
	
	protected abstract void customLocate(List<WebElement> located);

	@Override
	public WebDriver getDriver() {
		return driver;
	}

}
