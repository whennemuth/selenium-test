package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.services.automate.element.AttributeInspector;
import edu.bu.ist.apps.kualiautomation.services.automate.element.BasicElement;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.label.LabelledElementLocator;

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

//	@Override
//	protected List<WebElement> customLocate() {
//		List<WebElement> located = new ArrayList<WebElement>();
//		
//		if(elementType != null && elementType.getTagname() != null) {
//			
//			HyperlinkElementLocator locator1 = new HyperlinkElementLocator(driver, searchContext);
//			List<Element> elements = locator1.locateAll(ElementType.HYPERLINK, parameters);
//			
//			if(elements.isEmpty()) {
//				elements.addAll(new BasicElementLocator(
//						ElementType.BUTTONIMAGE,
//						driver, 
//						searchContext).locateAll(parameters));
//			}
//			
//			for(Element e : elements) {
//				located.add(e.getWebElement());
//			}
//		}		
//		
//		// If located is empty, then the ElementType.HOTSPOT regex will be used to attempt the search
//		
//		return located;
//	}

	@Override
	protected List<WebElement> customLocate() {
		List<WebElement> located = new ArrayList<WebElement>();
		
		if(elementType != null && elementType.getTagname() != null) {
			
			List<Element> elements = new ArrayList<Element>();
			
			LabelledElementLocator locator1 = new LabelledElementLocator(driver, searchContext);
			locator1.setLabelCanBeHyperlink(false);
			elements.addAll(locator1.locateAll(ElementType.BUTTONIMAGE, parameters));
			
			if(elements.isEmpty() || !attributeMatched(elements)) {
				HyperlinkElementLocator locator2 = new HyperlinkElementLocator(driver, searchContext);
				List<Element> hyperlinks = locator2.locateAll(ElementType.HYPERLINK, parameters);
				if(elements.isEmpty()) {
					elements.addAll(hyperlinks);
				}
				else if(attributeMatched(hyperlinks)) {
					// The button images matched on label only, but some hyperlinks matched on label AND attribute
					elements.clear();
					elements.addAll(hyperlinks);
				}
			}
			
			for(Element e : elements) {
				located.add(e.getWebElement());
			}
		}		
		
		// If located is empty, then the ElementType.HOTSPOT regex will be used to attempt the search
		
		return located;
	}

	private boolean attributeMatched(List<Element> elements) {
		if(!elements.isEmpty() && parameters.size() > 1) {
			// A label AND at least one attribute was specified.
			for(Element element : elements) {
				List<String> attributeValues = parameters.subList(1, parameters.size());
				for(String val : attributeValues) {
					AttributeInspector inspector = new AttributeInspector(searchContext, element.getWebElement());						
					if(!inspector.findForValue(val).isEmpty()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	protected Element getElement(WebDriver driver, WebElement we) {
		return new BasicElement(driver, we);
	}
}
