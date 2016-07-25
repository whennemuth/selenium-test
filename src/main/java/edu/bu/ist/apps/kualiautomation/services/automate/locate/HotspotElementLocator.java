package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;

public class HotspotElementLocator extends AbstractElementLocator {

	public HotspotElementLocator(WebDriver driver) {
		super(driver);
	}

	public HotspotElementLocator(WebDriver driver, SearchContext searchContext) {
		super(driver, searchContext);
	}

	/**
	 * Locate a hotspot element by searching for elements of various clickable types in the following order:
	 *    1) HOTSPOT (matches the ElementType.HOTSPOT regex)
	 *    2) HYPERLINK (overlaps much of what a HOTSPOT search will do, with some extra functionality)
	 *    3) BUTTONIMAGE (matches the ElementType.BUTTONIMAGE regex)
	 */
	@Override
	protected List<WebElement> customLocate() {
		List<WebElement> located = new ArrayList<WebElement>();
		
		if(elementType != null && elementType.getTagname() != null) {
			
			located.addAll(super.defaultLocate());

			// If customLocate() fails then the upcoming default search will try to find fields. 
			// Prevent this by indicating the default search has already run.
			defaultRan = true;
			
			if(located.isEmpty()) {
				HyperlinkElementLocator locator1 = new HyperlinkElementLocator(driver, searchContext);
				List<Element> elements = locator1.locateAll(ElementType.HYPERLINK, parameters);
				
				if(elements.isEmpty()) {
					BasicElementLocator locator2 = new BasicElementLocator(driver, searchContext);
					elements = locator2.locateAll(ElementType.BUTTONIMAGE, parameters);
				}
				
				for(Element e : elements) {
					located.add(e.getWebElement());
				}
			}		
		}
		
		return located;
	}
}
