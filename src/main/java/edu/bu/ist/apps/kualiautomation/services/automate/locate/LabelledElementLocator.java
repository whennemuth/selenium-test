package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Attribute;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;

/**
 * With a specified WebElement that serves as a label, traverse up the DOM one element at a time until
 * a descendant is found that matches the criteria of elementType. This should be the field that is labelled.
 * 
 * ASSUMPTION: This method assumes that a label and its field will share the same parent, grandparent, etc. - but
 * not with any other labels and fields - other fields and labels exist outside of the shared ancestor.
 * 
 * @author wrh
 *
 */
public class LabelledElementLocator extends AbstractElementLocator {
	
	private LabelledElementLocator() {
		super(null); // Restrict the default constructor
	}
	
	public LabelledElementLocator(WebDriver driver){
		super(driver);
	}
	
	@Override
	protected List<WebElement> customLocate() {
		List<WebElement> located = new ArrayList<WebElement>();
		if(elementType != null && elementType.getTagname() != null) {
			
			String label = new String(parameters.get(0));
			List<String> attributeValues = new ArrayList<String>();
			if(parameters.size() > 1) {
				attributeValues = parameters.subList(1, parameters.size());
			}
			LabelElementLocator labelLocator = new LabelElementLocator(driver);
			List<Element> candidates = labelLocator.locateAll(elementType, Arrays.asList(new String[]{label}));
			
			for(Element labelElement : candidates) {
				List<WebElement> flds = getInputField(labelElement.getWebElement(), attributeValues);
				located.addAll(flds);
			}
		}
		
		return located;
	}

	/**
	 * Recurse up the DOM from the provided elements parent until the sought element is found, or the root node is reached.
	 * 
	 * @param element
	 * @return
	 */
	private List<WebElement> getInputField(WebElement element, List<String> attributeValues) {

		List<WebElement> flds = elementType.findFrom(element);

		if(flds.isEmpty()) {
			// WebElement parent = getParentElement(element);
			WebElement parent = null;
			try {
				parent = element.findElement(By.xpath("./.."));
			} 
			catch (InvalidSelectorException e) {
				// Runtime exception thrown when you are at the top of the DOM and try to select higher.
				// An instance of com.gargoylesoftware.htmlunit.html.HtmlPage is returned, which triggers the exception.
				return flds;
			}
			if(parent != null) {
				return getInputField(parent, attributeValues);
			}
			return flds;
		}
		else {
			if(attributeValues.isEmpty()) {
				return flds;
			}
			else {
				// If parameters are present beyond the first, they are attribute values, so pick only 
				// those webElements that have every attributeValue accounted for among their attributes.
				List<WebElement> filtered = Attribute.findForValues(flds, attributeValues);
				return filtered;
			}
		}
	}

	/**
	 * This only works if javascript is enabled on the driver.
	 * @param childElement
	 * @return
	 */
	@SuppressWarnings("unused")
	private WebElement getParentElement(WebElement childElement) {
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		WebElement parentElement = (WebElement)executor.executeScript("return arguments[0].parentNode;", childElement);
		return parentElement;
	}
}
