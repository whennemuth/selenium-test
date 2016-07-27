package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import edu.bu.ist.apps.kualiautomation.entity.ConfigShortcut;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;

/**
 * Find a WebElement instance whose location has been described as part of a "hierarch". That is,
 * the WebElement will exist within enclosing html that itself contains other labelling html. For
 * example, a hierarchy might be "label1 > label2 > mylink", which would indicate a hyperlink having
 * text "mylink" that exists inside html containing another element that has text  attribute "label2",
 * that itself is likewise contained in another element that has the text or attribute "label1".
 * 
 * @author wrh
 *
 */
public class ShortcutElementLocator  extends AbstractElementLocator {

	private static final int TIMEOUT_SECONDS = 10;
	private Parameters parms;
	private final List<Element> searchResults = new ArrayList<Element>();
	
	private ShortcutElementLocator() {
		super(null); // Restrict the default constructor
	}
	
	public ShortcutElementLocator(WebDriver driver, ConfigShortcut shortcut) {
		this(driver, driver, shortcut);
	}
	
	public ShortcutElementLocator(WebDriver driver, SearchContext searchContext, ConfigShortcut shortcut) {
		super(driver);
		parms = new Parameters();
		parms.setDriver(driver);
		parms.setSearchContext(searchContext);
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
			
			results = find(node, parms.doWait());			
				
			results = removeUnqualified(results, "hidden");
			
			if(!parms.isHeader())
				results = removeUnqualified(results, "disabled");
			
			if(!results.isEmpty()) {
				if(results.size() == 1) {
					if(parms.isHeader()) {
						return findNext(results.get(0));
					}
					return results;
				}
				else if(parms.isHeader()) {
					/**
					 * There is more than one matching heading, but it may still be possible that only one
					 * is part of a hierarchy that terminates at the sought WebElement. Explore down each
					 * hierarchy and determine if this is so.
					 * 
					 * TODO: Make a Junit test that covers this scenario.
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
	 * Hidden or disabled WebElements can be removed from the search results if necessary.
	 *  
	 * @param results
	 * @param type
	 * @return
	 */
	private List<WebElement> removeUnqualified(List<WebElement> results, String type) {
		int initialSize = results.size();
		for(WebElement wb : results) {
			if("hidden".equalsIgnoreCase(type) && !wb.isDisplayed()) {
				results.remove(wb);
				break;
			}
			else if("disabled".equalsIgnoreCase(type) && !wb.isEnabled()) {
				results.remove(wb);
				break;
			}
		}
		
		if(results.size() < initialSize) {
			return removeUnqualified(results, type);
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
		WebElement parentElmt = webElmt.findElement(By.xpath("./.."));
		newparms.setSearchContext(parentElmt);
		newparms.setShortcut(getNestedShortcut());
		ShortcutElementLocator locator = new ShortcutElementLocator(newparms);
		locator.elementType = elementType;
		return locator.customLocate();
	}

	/**
	 * Find an element assuming first that it is a label, secondly that it is a hyperlink or other clickable item.
	 * If clickable, click it and invoke a WebDriverWait assuming that the next element in the hierarchy may not
	 * appear instantaneously.
	 * 
	 * @param clue
	 * @return
	 */
	private List<WebElement> find(String clue, boolean wait) {
		
		List<WebElement> webElements = new ArrayList<WebElement>();
		
		if(wait) {
			parms.waitPatiently().until(webElementLocated(clue));
			
			for(Element elmt : searchResults) {
				webElements.add(elmt.getWebElement());
			}
			searchResults.clear();
		}
		else {
			List<String> searchparms = Arrays.asList(new String[]{ clue });
			if(parms.isHeader()) {
				
				// 1) Assume clue is a heading label value and search accordingly
				Locator locator = new LabelElementLocator(parms.getDriver(), parms.getSearchContext());
				searchResults.addAll(locator.locateAll(elementType, searchparms));
				
				// 2) Assume clue indicates an attribute value of a heading hotspot element and search accordingly
				if(searchResults.isEmpty()) {
					locator = new HotspotElementLocator(parms.getDriver(), parms.getSearchContext());
					searchResults.addAll(locator.locateAll(elementType, searchparms));
				}
			}
			else {
				
				// 1) Assume clue is the text or attribute value for a hotspot
				Locator locator = new HotspotElementLocator(parms.getDriver(), parms.getSearchContext());
				searchResults.addAll(locator.locateAll(elementType, searchparms));
				
				// 2) Assume that clue can refer to the name of a class (CSS) or a member of a multi-valued class
				if(searchResults.isEmpty()) {
					locator = new ClassBasedElementLocator(parms.getDriver(), parms.getSearchContext());
					searchResults.addAll(locator.locateAll(elementType, searchparms));
				}
				
				// 3) Assume that we are looking for any other kind of element.
				if(searchResults.isEmpty()) {
					locator = new BasicElementLocator(parms.getDriver(), parms.getSearchContext());
					searchResults.addAll(locator.locateAll(elementType, searchparms));
				}
			}
			for(Element elmt : searchResults) {
				webElements.add(elmt.getWebElement());
			}
		}	
		
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
	
	/**
	 * This condition evaluates if one or more elements have been found as the result of a 
	 * search by checking the list that stores the results for content. 
	 * 
	 * @param locators
	 * @return
	 */
	private ExpectedCondition<Boolean> webElementLocated(final String heading) {	
		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {			  
			public Boolean apply(WebDriver drv) {
				find(heading, false);
				return !searchResults.isEmpty();
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