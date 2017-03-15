package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.services.automate.element.AttributeInspector;
import edu.bu.ist.apps.kualiautomation.services.automate.element.BasicElement;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.label.LabelElementLocator;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.label.LabelledElementLocator;

public class HyperlinkElementLocator extends AbstractElementLocator {

	public HyperlinkElementLocator(WebDriver driver) {
		super(driver);
	}

	public HyperlinkElementLocator(WebDriver driver, SearchContext searchContext) {
		super(driver, searchContext);
	}

	@Override
	protected List<WebElement> customLocate() {
		List<WebElement> located = new ArrayList<WebElement>();
		if(elementType != null && elementType.getTagname() != null) {
			
			String innerText = new String(parameters.get(0));
			List<String> attributeValues = new ArrayList<String>();
			if(parameters.size() > 1) {
				attributeValues = parameters.subList(1, parameters.size());
			}
			
			// 1) A hyperlink will come up as a result of a more basic label search, so start there and filter later.
			LabelElementLocator labelLocator = new LabelElementLocator(driver, searchContext);
			List<Element> candidates = labelLocator.locateAll(null, Arrays.asList(new String[]{ innerText }));
			List<WebElement> webElements = getWebElements(candidates);
			
			// 2) Separate the anchor tags from the other labels
			List<WebElement> labels = new ArrayList<WebElement>();
			List<WebElement> anchortags = new ArrayList<WebElement>();			
			for(WebElement we : webElements) {
				if("a".equalsIgnoreCase(we.getTagName()))
					anchortags.add(we);
				else
					labels.add(we);
			}
			
			List<WebElement> finalresults = new ArrayList<WebElement>();
					
			if(anchortags.size() == 1 || labels.isEmpty()) {
				finalresults.addAll(anchortags);
			}
			else {
				
				// 3) Find out if the elements found so far are in a table and we can pick one from it that best matches label.
				List<WebElement> tabledAnchortags1 = filterByTabularLogic(labels, anchortags);
				List<WebElement> tabledAnchortags2 = new ArrayList<WebElement>();
				List<WebElement> tabledAnchortags3 = new ArrayList<WebElement>();
						
				if(tabledAnchortags1.size() == 1) {
					finalresults.addAll(tabledAnchortags1);
				}
				else {
					
					// 4) We turned up another label in the initial search for anchor tags. Regard it as an element that 
					// labels one or more other anchor tags and use the LabelledElementLocator to find them.
					for(@SuppressWarnings("unused") WebElement label : labels) {
						LabelledElementLocator labelledLocator = new LabelledElementLocator(driver, searchContext, getElements(labels));
						labelledLocator.setLabelCanBeHyperlink(false);
						List<Element> anchorTagElements = labelledLocator.locateAll(ElementType.HYPERLINK, parameters);
						for(Element anchorTagElement : anchorTagElements) {
							if(!tabledAnchortags1.contains(anchorTagElement.getWebElement())) {
								tabledAnchortags2.add(anchorTagElement.getWebElement());
							}
						}
					}
					
					// 5) Repeat the table-based search for any newly found elements
					if(tabledAnchortags2.size() == 1) {
						finalresults.addAll(tabledAnchortags2);
					}
					else {
						tabledAnchortags3 = filterByTabularLogic(labels, tabledAnchortags2);
						if(tabledAnchortags3.size() == 1) {
							finalresults.addAll(tabledAnchortags3);
						}
						else {
							finalresults = mergeAll(tabledAnchortags1, tabledAnchortags2, tabledAnchortags3);
						}
					}
				}
			}
			
			// 6)  With all of the anchor tags found, remove those that do not have an attribute value for each value in attributeValues
			AttributeInspector inspector = new AttributeInspector(searchContext, finalresults);
			located.addAll(inspector.findForValues(attributeValues));
		}
		
		return located;
	}

	/**
	 * Treat the anchortags as if they exist in a table and that the label is a row or column header of that
	 * table. Reduce the list down to those that are "closest" in the table to the label.
	 * @param labels
	 * @param anchortags
	 */
	private List<WebElement> filterByTabularLogic(List<WebElement> labels, List<WebElement> anchortags) {
		if(anchortags.isEmpty() || labels.isEmpty())
			return new ArrayList<WebElement>();
		
		List<Element> lbls = getElements(labels);
		return getWebElements(LabelledElementLocator.getBestInTable(
				driver, 
				elementType, 
				lbls, 
				anchortags, 
				parameters));
	}
	
	private static List<WebElement> merge(List<WebElement> list1, List<WebElement> list2) {
		List<WebElement> merged = new ArrayList<WebElement>();
		for(WebElement we : list1) {
			if(!list2.contains(we)) {
				merged.add(we);
			}
		}
		merged.addAll(list2);
		return merged;
	}

	@SafeVarargs
	private static final List<WebElement> mergeAll(List<WebElement> list1, List<WebElement>...others) {		
		for(List<WebElement> other : others) {
			list1 = merge(list1, other);
		}
		return list1;
	}
	
	private List<WebElement> getWebElements(List<Element> elements) {
		List<WebElement> results = new ArrayList<WebElement>();
		for(Element e : elements) {
			results.add(e.getWebElement());
		}
		return results;
	}
	
	private List<Element> getElements(List<WebElement> webElements) {
		List<Element> elements = new ArrayList<Element>();
		for(WebElement we : webElements) {
			elements.add(new BasicElement(driver, we));
		}
		return elements;
	}

	@Override
	protected Element getElement(WebDriver driver, WebElement we) {
		return new BasicElement(driver, we);
	}
}
