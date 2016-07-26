package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Attribute;
import edu.bu.ist.apps.kualiautomation.services.automate.element.BasicElement;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;

public abstract class AbstractElementLocator implements Locator {

	protected SearchContext searchContext;
	protected WebDriver driver;
	protected ElementType elementType;
	protected List<String> parameters = new ArrayList<String>();
	protected boolean defaultRan;
	/**
	 * busy variable is queried to prevent repeated calls in case locator inside a WebDriverWait.until() method.
	 * The find methods of the WebDriver will no longer return immediately with results but will instead inherit
	 * the timeout of the WebDriverWait method.
	 */
	protected boolean busy; 
	protected boolean skipFrameSearch;
	
	public AbstractElementLocator(WebDriver driver) {
		this.driver = driver;
		this.searchContext = driver;
	}
	
	public AbstractElementLocator(WebDriver driver, SearchContext searchContext) {
		this.driver = driver;
		this.searchContext = searchContext;
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
		List<Element> results;
		try {
			busy = true;
			defaultRan = false;
			if(elementType != null) {
				this.elementType = elementType;
			}
			this.parameters = parameters;
			final List<WebElement> webElements = new ArrayList<WebElement>();
			results = new ArrayList<Element>();
			
			List<WebElement> custom = customLocate();
			
			webElements.addAll(custom);
			
			if(webElements.isEmpty()) {
				List<WebElement> defaults = defaultLocate();
				webElements.addAll(defaults);
			}
			
			if(webElements.isEmpty() && !skipFrameSearch) {
				// Check for frames and search those as well
				WebDriverWait wait = new WebDriverWait(driver, 100);
				List<WebElement> iframes = searchContext.findElements(By.tagName("iframe"));
				if(!iframes.isEmpty()) {
					for(WebElement iframe : iframes) {
						searchContext = wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframe));
						List<Element> frameResults = locateAll(elementType, parameters);
						results.addAll(frameResults);
						if(frameResults.isEmpty()) {
							driver.switchTo().defaultContent();
						}
						else {
							// Don't switch back to the parent window because you will not be able to use the WebElement as it would 
							// then belong to a frame that the WebDriver is longer focused on ( you will get a StaleElementReferenceException ).
							// driver.switchTo().defaultContent();
						}
					}
				}
			}
			else {
				for(WebElement we : webElements) {
					results.add(new BasicElement(driver, we));
				}
			}
		} 
		finally {
			busy = false;
		}
		
		return results;
	}
	
	public void setDefaultRan(boolean defaultRan) {
		this.defaultRan = defaultRan;
	}
	
	
	@Override
	public boolean busy() {
		return busy;
	}

	protected abstract List<WebElement> customLocate();
	
	/**
	 * This is the default method for locating a WebElement on an html page.
	 * It contains generic methods for matching and is invoked if the custom method found no results.
	 * 
	 * @return
	 */
	protected List<WebElement> defaultLocate() {
		
		List<WebElement> results = new ArrayList<WebElement>();
		List<WebElement> candidates = new ArrayList<WebElement>();
		
		if(defaultRan || parameters.isEmpty() || elementType == null)
			return results;
		
		switch(elementType) {
		case BUTTON:
		case BUTTONIMAGE:
		case CHECKBOX:
		case HYPERLINK:
		case TEXTBOX:
		case PASSWORD: 
		case TEXTAREA:
		case SELECT:
		case RADIO:
		case HOTSPOT:
		case OTHER:
			candidates = elementType.findAll(searchContext);
			results.addAll(Attribute.findForValues(candidates, parameters));
			break;
		case SHORTCUT:
			break;
		default:
			break;
		}
		
		defaultRan = true;
		
		return results;
	}

	@Override
	public SearchContext getSearchContext() {
		return searchContext;
	}

	@Override
	public WebDriver getWebDriver() {
		return driver;
	}
}
