package edu.bu.ist.apps.kualiautomation.services.automate.locate.screenscrape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.MatchResult;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.services.automate.locate.AbstractElementLocator;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.label.ComparableLabel;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.screenscrape.ScreenScrapeComparePattern;
import edu.bu.ist.apps.kualiautomation.util.Utils;

/**
 * This locator finds an html element(s) whose innerText best matches a pattern, where that pattern specifies a
 * static sequence of characters followed by a sequence of characters that match a selected pattern.
 * This would usually be a value, like and id, labelled on its left side. By "innerText" is meant what the 
 * user would see in the browser within the bounds of the html element in terms of text. 
 * For example, the innerText of:
 *     <div> employeeId: 35334545</div>
 * would be...
 *     "employeeId: 35334545"
 * and the innertText of:
 *     <div><span>organizationID:</span><div style="padding:5px;">45649389</div></div>
 * would be...
 *     "organizationID:45649389"
 * 
 * Unlike other locators, we are not searching for an element for user input, but are instead "screen scraping"
 * for a text value. The inner-most element that best matches is returned, but the textual match is captured so it
 * is available for use further down along the suite, like inserting it into a text field and clicking "search".
 * 
 * @author wrh
 *
 */
public class ScreenScrapeElementLocator extends AbstractElementLocator {

	private static final String XPATH_CONTAINS = 
			"//*[contains(string(normalize-space(translate(., "
			+ "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', "
			+ "'abcdefghijklmnopqrstuvwxyz'))), \"[INSERT-LABEL]\")]";
	
	public ScreenScrapeElementLocator(WebDriver driver) {
		super(driver);
	}
	public ScreenScrapeElementLocator(WebDriver driver, SearchContext searchContext){
		super(driver, searchContext);
	}

	@Override
	protected List<WebElement> customLocate() {
		List<WebElement> located = new ArrayList<WebElement>();
		if(validParameters()) {
			
			String scope = "";	// global
			if(searchContext instanceof WebElement) {
				scope = ".";	// current scope within element.
			}
			
			String label = new String(parameters.get(0)).trim().replaceAll("\\s+", " ");
			String sPattern = parameters.get(1);					
			String xpath = scope + XPATH_CONTAINS.replace("[INSERT-LABEL]", label);
			List<WebElement> elements = searchContext.findElements(By.xpath(xpath));
			List<ComparableLabel> scraped = new ArrayList<ComparableLabel>();
			
			if(!elements.isEmpty()) {
				for (Iterator<WebElement> iterator = elements.iterator(); iterator.hasNext();) {
					
					WebElement we = iterator.next();
					
					ComparableParameters parms = new ComparableParameters()
							.setPattern(ScreenScrapeComparePattern.valueOf(sPattern))
							.setLabel(label)
							.setText(we.getText())
							.setWebElement(we)
							.setUseDefaultMethodIfIndeterminate(false);
							
					scraped.add(new ComparableScreenScrape(parms));
				}
			}
			
			Collections.sort(scraped);
		}
		
		return located;
	}

	private String invalidParmsMsg = null;
	
	private boolean validParameters() {
		if(Utils.isEmpty(elementType)) {
			invalidParmsMsg = "Invalid parameter: elementType cannot be null or empty";
			return false;
		}
		if(parameters.size() < 2) {
			invalidParmsMsg = "Invalid parameter: Expecting parameters list to have at least 2 entries";
			return false;
		}
		if(Utils.isEmpty(parameters.get(0))) {
			invalidParmsMsg = "Invalid parameter: First parameter (label) cannot be null or empty";
			return false;
		}
		if(Utils.isEmpty(parameters.get(1))) {
			invalidParmsMsg = "Invalid parameter: Second parameter (pattern) cannot be null or empty";
			return false;
		}

		return true;
	}
}
