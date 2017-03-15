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

import edu.bu.ist.apps.kualiautomation.services.automate.element.AbstractWebElement;
import edu.bu.ist.apps.kualiautomation.services.automate.element.AttributeInspector;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;

public abstract class AbstractElementLocator implements Locator {
	protected SearchContext searchContext;
	protected WebDriver driver;
	protected ElementType elementType;
	protected List<String> parameters = new ArrayList<String>();
	protected List<WebElement> iframes;
	protected boolean skipParameterMatching;
	protected boolean defaultRan;
	/**
	 * busy variable is queried to prevent repeated calls in case locator inside a WebDriverWait.until() method.
	 * The find methods of the WebDriver will no longer return immediately with results but will instead inherit
	 * the timeout of the WebDriverWait method.
	 */
	protected boolean busy; 
	protected boolean skipFrameSearch;
	protected boolean ignoreHidden = true;
	protected boolean ignoreDisabled = true;
	protected String message;
	
	public static boolean printDuration = true;
	
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
		List<Element> results = new ArrayList<Element>();
		long start = 0;
		try {
			if(printDuration)
				start = System.currentTimeMillis();
			
			busy = true;
			defaultRan = false;
			if(elementType != null) {
				this.elementType = elementType;
			}
			this.parameters = parameters;
			
			results = new ArrayList<Element>();
			
			if(framesFirst()) {
				
				loadFrameResults(results);
				
				if(results.isEmpty()) {
					
					loadResults(results);	
				}
			}
			else {			
				
				loadResults(results);
				
				if(results.isEmpty()) {
					
					loadFrameResults(results);
				}
			}
			
		} 
		finally {
			busy = false;
			iframes = null;
			if(printDuration) {
			//if(printDuration && skipFrameSearch) {
				long end = System.currentTimeMillis();
				Long mils = end - start;
				System.out.println(this.getClass().getName() + " found " + String.valueOf(results.size()) + " results in " + mils.toString() + " milliseconds for parameter(s):");
				for(String p : parameters) {
					System.out.println(p);
				}
			}				
		}
		
		return results;
	}
	
	/**
	 * Execute the searches - custom search first, followed by the default search if no results from custom.
	 * @param webElements
	 */
	private void loadResults(List<Element> results) {
		
		List<WebElement> webElements = new ArrayList<WebElement>();
		
		List<WebElement> custom = customLocate();
		
		for(WebElement found : custom) {
			if(ignoreHidden && !found.isDisplayed())
				continue;
			if(ignoreDisabled && !found.isEnabled())
				continue;
			webElements.add(found);
		}
		
		if(webElements.isEmpty()) {			
			List<WebElement> defaults = defaultLocate();			
			webElements.addAll(defaults);
		}
		
		for(WebElement we : webElements) {
			results.add(getElement(driver, we));
		}
	}
	
	/**
	 * Search for web elements within a frame.
	 * 
	 * @param elementType
	 * @param parameters
	 * @return
	 */
	private void loadFrameResults(List<Element> results) {
		
		if(skipFrameSearch) {
			return;
		}
		
		if(!iframes.isEmpty()) {
			for(WebElement iframe : iframes) {
				// (new WebDriverWait(driver, 5)).until(ExpectedConditions.visibilityOf(iframe));
				(new WebDriverWait(driver, 5)).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframe));
				skipFrameSearch = true;
				searchContext = driver;
				try {
					List<Element> frameResults = locateAll(elementType, parameters);
					results.addAll(frameResults);
					if(frameResults.isEmpty()) {
						driver.switchTo().defaultContent();
						searchContext = driver;
					}
					else {
						// Don't switch back to the parent window because you will not be able to use the WebElement as it would 
						// then belong to a frame that the WebDriver is longer focused on ( you will get a StaleElementReferenceException ).
						// driver.switchTo().defaultContent();
					}
				}
				finally {
					skipFrameSearch = false;
				}
			}
		}
	}
	
	/**
	 * By experience, most elements are to be found in a frame (at least for kuali). So, searching
	 * should start inside the frame and only be conducted outside of the frame if no results are found.
	 * However, shortcuts are usually found outside of frames as peripheral menu items.
	 * NOTE: This is arbitrary and favors searching norms for kuali - a future enhancement might be to inject this logic.
	 * @return
	 */
	private boolean framesFirst() {
		if(iframes == null)
			iframes = searchContext.findElements(By.tagName("iframe"));
		if(iframes.isEmpty())
			return false;
		return !ElementType.SHORTCUT.equals(elementType);
	}
	
	public void setDefaultRan(boolean defaultRan) {
		this.defaultRan = defaultRan;
	}
	
	public boolean isDefaultRan() {
		return defaultRan;
	}
	
	@Override
	public boolean busy() {
		return busy;
	}

	protected abstract List<WebElement> customLocate();
	
	protected abstract Element getElement(WebDriver driver, WebElement we);
	
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
			candidates = elementType.findAll(searchContext, skipFrameSearch);
			if(skipParameterMatching) {
				results.addAll(candidates);
			}
			else {
				AttributeInspector inspector = new AttributeInspector(candidates);
				results.addAll(inspector.findForValues(parameters));
			}
			break;
		case SHORTCUT: case SCREENSCRAPE:
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
		
		txt = (String) ((JavascriptExecutor) driver).executeScript(
				"return arguments[0].innerHTML", 
				AbstractWebElement.unwrap(we));
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

	@Override
	public boolean ignoreHidden() {
		return ignoreHidden;
	}

	@Override
	public boolean ignoreDisabled() {
		return ignoreDisabled;
	}
	
	public void setIgnoreHidden(boolean ignoreHidden) {
		this.ignoreHidden = ignoreHidden;
	}
	
	public void setIgnoreDisabled(boolean ignoreDisabled) {
		this.ignoreDisabled = ignoreDisabled;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
