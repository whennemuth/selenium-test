package edu.bu.ist.apps.kualiautomation.services.automate.locate.label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Attribute;
import edu.bu.ist.apps.kualiautomation.services.automate.element.BasicElement;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.AbstractElementLocator;
import edu.bu.ist.apps.kualiautomation.util.Utils;

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
	
	public LabelledElementLocator(WebDriver driver){
		super(driver);
	}
	public LabelledElementLocator(WebDriver driver, SearchContext searchContext){
		super(driver, searchContext);
	}
	
	/**
	 * Locate all WebElement instances that sufficiently match ElementType and parameters.
	 * Treat the first parameter as a label. 
	 * All remaining parameters will be considered values to test WebElement attributes against.
	 */
	@Override
	protected List<WebElement> customLocate() {
		List<WebElement> located = new ArrayList<WebElement>();
		if(elementType != null && elementType.getTagname() != null) {
			
			String label = new String(parameters.get(0));
			List<String> attributeValues = new ArrayList<String>();
			if(parameters.size() > 1) {
				attributeValues = parameters.subList(1, parameters.size());
			}
			LabelElementLocator labelLocator = new LabelElementLocator(driver, searchContext);
			labelLocator.setIgnoreHidden(super.ignoreHidden);
			labelLocator.setIgnoreDisabled(super.ignoreDisabled);
			List<Element> labelElements = labelLocator.locateAll(elementType, Arrays.asList(new String[]{label}));
			for(Element labelElement : labelElements) {
				List<WebElement> flds = tryTraditionalLabelSearchMethod(labelElement.getWebElement(), attributeValues);
				if(flds.isEmpty()) {
					flds = trySearchingOutwardFromLabel(labelElement.getWebElement(), attributeValues);
				}
				located.addAll(flds);
			}
		}
		
		return located;
	}

	/**
	 * The label might be a <label> html element with a for attribute, in which case the search for the element
	 * should be easy.
	 * 
	 * @param labelElement
	 * @param attributeValues
	 * @return
	 */
	private List<WebElement> tryTraditionalLabelSearchMethod(WebElement labelElement, List<String> attributeValues) {
		List<WebElement> flds = new ArrayList<WebElement>();
		if("label".equals(labelElement.getTagName())) {
			String id = labelElement.getAttribute("for");
			if(!Utils.isEmpty(id)) {
				List<WebElement> temp = driver.findElements(By.id(id));
				for(WebElement candidate : temp) {
					if(ElementType.getInstance(candidate).equals(elementType)) {
						flds.add(candidate);
					}
				}
			}
		}
		return flds;
	}
	/**
	 * Recurse up the DOM from the provided elements parent until the sought element is found, or the root node is reached.
	 * 
	 * @param labelElement
	 * @return
	 */
	private List<WebElement> trySearchingOutwardFromLabel(WebElement labelElement, List<String> attributeValues) {

		List<WebElement> candidates = elementType.findAll(labelElement);

		if(candidates.isEmpty()) {
			// WebElement parent = getParentElement(element);
			WebElement parent = null;
			try {
				parent = labelElement.findElement(By.xpath("./.."));
			} 
			catch (InvalidSelectorException e) {
				// Runtime exception thrown when you are at the top of the DOM and try to select higher.
				// An instance of com.gargoylesoftware.htmlunit.html.HtmlPage is returned, which triggers the exception.
				return candidates;
			}
			if(parent != null) {
				return trySearchingOutwardFromLabel(parent, attributeValues);
			}
			return candidates;
		}
		else {
			if(attributeValues.isEmpty()) {
				return candidates;
			}
			else {
				// If parameters are present beyond the first, they are attribute values, so pick only 
				// those webElements that have every attributeValue accounted for among their attributes.
				List<WebElement> filtered = Attribute.findForValues(candidates, attributeValues);
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
		JavascriptExecutor executor = (JavascriptExecutor)searchContext;
		WebElement parentElement = (WebElement)executor.executeScript("return arguments[0].parentNode;", childElement);
		return parentElement;
	}
	@Override
	protected Element getElement(WebDriver driver, WebElement we) {
		return new BasicElement(driver, we);
	}
}