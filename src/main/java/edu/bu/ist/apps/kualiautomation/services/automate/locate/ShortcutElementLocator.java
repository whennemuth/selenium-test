package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jgroups.tests.perf.UPerf.Results;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import edu.bu.ist.apps.kualiautomation.entity.ConfigShortcut;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;

public class ShortcutElementLocator  extends AbstractElementLocator {

	private static final int TIMEOUT_SECONDS = 10;
	private Parameters parms;
	private List<Element> lastResults = new ArrayList<Element>();
	
	private ShortcutElementLocator() {
		super(null); // Restrict the default constructor
	}
	
	public ShortcutElementLocator(WebDriver driver, ConfigShortcut shortcut) {
		super(driver);
		parms = new Parameters();
		parms.setDriver(driver);
		parms.setShortcut(shortcut);
	}
	
	public ShortcutElementLocator(Parameters parms) {
		super(parms.getDriver());
		this.parms = parms;
	}

	@Override
	protected List<WebElement> customLocate() {
		List<WebElement> results = new ArrayList<WebElement>();
		try {
			String node = parms.getShortcut().getLabelHierarchyParts()[0];
			
			results = find(node);
			
			if(!results.isEmpty()) {
				if(results.size() == 1) {
					if(parms.isHeader()) {
						return findNext(results.get(0));
					}
					return results;
				}
				else {
					/**
					 * There is more than one matching heading, but it may still be possible that only one
					 * is part of a hierarchy that terminates at the sought WebElement. Explore down each
					 * hierarchy and determine if this is so.
					 */
					List<WebElement> aggregateResults = new ArrayList<WebElement>();
					for(WebElement result : results) {						
						aggregateResults.addAll(findNext(result));
					}
					return aggregateResults;
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<WebElement>();
		}
		
		return results;
	}
	
	/**
	 * This function performs another call to customLocate(), but with the top level of 
	 * getShortcut.getLabelHierarchyParts() popped off the start. Effectively, this repeats
	 * the search for the target element incrementally restarted one level deeper into the html element 
	 * hierarchy where the element is nested.
	 * 
	 * @param webElmt
	 * @return
	 * @throws Exception
	 */
	private List<WebElement> findNext(WebElement webElmt) throws Exception {
		ElementType etype = ElementType.getInstance(webElmt);
		
		Parameters newparms = new Parameters();
		if(etype.canNavigate()) {						
			webElmt.click();
			newparms.setWebDriverWait(new WebDriverWait(parms.getDriver(), TIMEOUT_SECONDS));
		}
		
		newparms.setDriver(parms.getDriver());
		newparms.setSearchContext(webElmt);
		newparms.setShortcut(getNestedShortcut());
		ShortcutElementLocator locator = new ShortcutElementLocator(newparms);
		return locator.customLocate();
	}

	/**
	 * Find an element assuming first that it is a label, secondly that it is a hyperlink or other clickable item.
	 * If clickable, click it and invoke a WebDriverWait assuming that the next element in the hierarchy may not
	 * appear instantaneously.
	 * 
	 * @param heading
	 * @return
	 */
	private List<WebElement> find(String heading) {
		
		List<Locator> locators = new ArrayList<Locator>();
		if(parms.isHeader()) {
			locators.add(new LabelElementLocator(parms.getDriver(), parms.getSearchContext()));
			locators.add(new HotspotElementLocator(parms.getDriver(), parms.getSearchContext()));

			if(parms.doWait()) {
				parms.waitPatiently().until(webElementLocated(locators));
			}
		}
		else {
			// RESUME NEXT: Write code here.
		}
		
		List<WebElement> webElements = new ArrayList<WebElement>(lastResults.size());
		for(Element elmt : lastResults) {
			webElements.add(elmt.getWebElement());
		}
		lastResults.clear();
		return webElements;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private ConfigShortcut getNestedShortcut() throws Exception {
		// Get a hierarchy array from the current shortcut with the first element removed.
		String[] hierarchy = Arrays.copyOfRange(
				parms.getShortcut().getLabelHierarchyParts(), 
				1, 
				parms.getShortcut().getLabelHierarchyParts().length);
		ConfigShortcut subshortcut = (ConfigShortcut) parms.getShortcut().clone();
		subshortcut.setLabelHierarchyParts(hierarchy);
		return subshortcut;
	}
	
	private ExpectedCondition<Boolean> webElementLocated(List<Locator> locators) {	
		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {			  
			public Boolean apply(WebDriver drv) {
				for(Locator locator : locators) {
					lastResults = locator.locateAll(elementType, parameters);
					if(lastResults.isEmpty()) {
						continue;
					}
				}
				return !lastResults.isEmpty();
			}
		};
		return condition;
	}
	
	private static class Parameters {
		private ConfigShortcut shortcut;
		private WebDriver driver;
		private SearchContext searchContext;
		private WebDriverWait webDriverWait;
		public ConfigShortcut getShortcut() {
			return shortcut;
		}
		public void setShortcut(ConfigShortcut shortcut) {
			this.shortcut = shortcut;
		}
		public WebDriverWait getWebDriverWait() {
			return webDriverWait;
		}
		public void setWebDriverWait(WebDriverWait webDriverWait) {
			this.webDriverWait = webDriverWait;
		}
		public WebDriver getDriver() {
			return driver;
		}
		public void setDriver(WebDriver driver) {
			this.driver = driver;
		}
		public SearchContext getSearchContext() {
			return searchContext == null ? driver : searchContext;
		}
		public void setSearchContext(SearchContext searchContext) {
			this.searchContext = searchContext;
		}
		public boolean doWait() {
			return getWebDriverWait() != null;
		}
		public WebDriverWait waitPatiently() {
			return getWebDriverWait();
		}
		public boolean isHeader() {
			return shortcut.getLabelHierarchyParts().length > 1;
		}
	}
	
}
