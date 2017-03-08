package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
			
			// 3) If a WebElement is not an anchor tag, regard it as an element that labels an anchor tag and use the LabelledElementLocator to find it.
			boolean foundOne = false;
			for(@SuppressWarnings("unused") WebElement label : labels) {
				//if(!attributeValues.isEmpty()) {
					LabelledElementLocator labelledLocator = new LabelledElementLocator(driver, searchContext);
					//labelledLocator.setLabelCanBeHyperlink(false);
					List<Element> anchorTagElements = labelledLocator.locateAll(ElementType.HYPERLINK, parameters);
					for(Element anchorTagElement : anchorTagElements) {
						if(!anchortags.contains(anchorTagElement.getWebElement())) {
							anchortags.add(anchorTagElement.getWebElement());
							foundOne = true;
						}
					}
				//}
			}
			if(!foundOne) {
				// Treat the anchortags as if they exist in a table and that the label is a row or column header of that
				// table. Reduce the list down to those that are "closest" in the table to the label.
				List<Element> lbls = new ArrayList<Element>();
				for(WebElement label : labels) {
					lbls.add(new BasicElement(driver, label));
				}
				List<WebElement> tableBased = LabelledElementLocator.getBestInTable(
						driver, 
						elementType, 
						lbls, 
						anchortags, 
						parameters);
				if(!tableBased.isEmpty()) {
					anchortags.clear();
					anchortags.addAll(tableBased);
				}
			}
			
			//4)  With all of the anchor tags found, remove those that do not have an attribute value for each value in attributeValues
			AttributeInspector inspector = new AttributeInspector(searchContext, anchortags);
			located.addAll(inspector.findForValues(attributeValues));
		}
		
		return located;
	}

	private List<WebElement> getWebElements(List<Element> elements) {
		List<WebElement> results = new ArrayList<WebElement>();
		for(Element e : elements) {
			results.add(e.getWebElement());
		}
		return results;
	}

	@Override
	protected Element getElement(WebDriver driver, WebElement we) {
		return new BasicElement(driver, we);
	}
}
