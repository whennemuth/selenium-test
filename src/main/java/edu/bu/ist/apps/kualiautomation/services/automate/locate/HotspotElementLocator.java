package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;

/**
 * Locate a hotspot element by searching for elements of various clickable types in the following order:
 *    1) HYPERLINK (matches the ElementType.HYPERLINK regex)
 *    2) BUTTONIMAGE (matches the ElementType.BUTTONIMAGE regex)
 *    3) HOTSPOT (matches the ElementType.HOTSPOT regex, which overlaps much of what a the prior searches did, with some extra possibilities)
 */
public class HotspotElementLocator extends AbstractElementLocator {

	public HotspotElementLocator(WebDriver driver) {
		super(driver);
	}

	public HotspotElementLocator(WebDriver driver, SearchContext searchContext) {
		super(driver, searchContext);
	}

	@Override
	protected List<WebElement> customLocate() {
		List<WebElement> located = new ArrayList<WebElement>();
		
		if(elementType != null && elementType.getTagname() != null) {
			
			HyperlinkElementLocator locator1 = new HyperlinkElementLocator(driver, searchContext);
			List<Element> elements = locator1.locateAll(ElementType.HYPERLINK, parameters);
			
			if(elements.isEmpty()) {
				elements = (new BasicElementLocator(
						ElementType.BUTTONIMAGE,
						driver, 
						searchContext)).locateAll(parameters);
			}
			
			for(Element e : elements) {
				located.add(e.getWebElement());
			}
		}		
		
		// If located is empty, then the ElementType.HOTSPOT regex will be used to attempt the search
		
		return located;
	}
}
