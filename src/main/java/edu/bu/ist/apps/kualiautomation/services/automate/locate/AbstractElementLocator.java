package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
	protected boolean skipParameterMatching;
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
				List<WebElement> iframes = searchContext.findElements(By.tagName("iframe"));
				if(!iframes.isEmpty()) {
					for(WebElement iframe : iframes) {
						// (new WebDriverWait(driver, 5)).until(ExpectedConditions.visibilityOf(iframe));
						(new WebDriverWait(driver, 5)).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframe));
						skipFrameSearch = true;
						searchContext = driver;
						List<Element> frameResults = locateAll(elementType, parameters);
						results.addAll(frameResults);
						if(frameResults.isEmpty()) {
							driver.switchTo().defaultContent();
							searchContext = driver;
							skipFrameSearch = false;
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
			if(skipParameterMatching) {
				results.addAll(candidates);
			}
			else {
				results.addAll(Attribute.findForValues(candidates, parameters));
			}
			break;
		case SHORTCUT:
			break;
		default:
			break;
		}
		
		defaultRan = true;
		
		return results;
	}
	
	/**
	 * WebElement.getText() will always return an empty string if the WebElement is not displayed,
	 * even if it has innerText. This function gets the text regardless of the WebElements display status.
	 *  
	 * @param driver
	 * @param we
	 * @return
	 */
	public static String getText(WebDriver driver, WebElement we) {
		if(we.isDisplayed()) {
			return we.getText();
		}
		String txt = we.getAttribute("textContent");
		
		if(txt != null && !txt.trim().isEmpty())
			return txt;
		
		txt = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML", we);
		return txt;
	}

	@Override
	public SearchContext getSearchContext() {
		return searchContext;
	}

	@Override
	public WebDriver getWebDriver() {
		return driver;
	}

	public void setSkipParameterMatching(boolean skipParameterMatching) {
		this.skipParameterMatching = skipParameterMatching;
	}
	
	
}
