package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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

	public Element locate(ElementType elementType, String label) {
		return super.locateFirst(elementType, Arrays.asList(new String[]{label}));
	}
	
	@Override
	protected void customLocate(List<WebElement> located) {
		if(elementType != null && elementType.getTagname() != null) {
			
			String label = new String(parameters.get(0));
			String attribute = null;
			if(parameters.size() > 1) {
				attribute = parameters.get(1);
			}
			LabelElementLocator labelLocator = new LabelElementLocator(driver);
			List<Element> candidates = labelLocator.locateAll(elementType, Arrays.asList(new String[]{label}));
			
			for(Element labelElement : candidates) {
				WebElement fld = getInputField(labelElement.getWebElement(), attribute);
				if(fld != null) {
					located.add(fld);
					break;
				}
			}
		}
	}

	/**
	 * Recurse up the DOM from the provided elements parent until the sought element is found, or the root node is reached.
	 * 
	 * @param element
	 * @return
	 */
	private WebElement getInputField(WebElement element, String attributeValue) {
		StringBuilder xpath = new StringBuilder(".//");
		xpath.append(elementType.getTagname());
		if(elementType.getTypeAttribute() != null) {
			xpath.append("[@type='")
			.append(elementType.getTypeAttribute())
			.append("']");
		}

		List<WebElement> flds = element.findElements(By.xpath(xpath.toString()));
		
		if(flds.isEmpty()) {
			// WebElement parent = getParentElement(element);
			WebElement parent = element.findElement(By.xpath("./.."));
			if(parent != null) {
				return getInputField(parent, attributeValue);
			}
			return null;
		}
		else {
			WebElement fld = flds.get(0);
			
			// A second parameter is present, it is an attribute value, so search attributes of the webElement for one with a matching value
			if(attributeValue == null) {
				return fld;
			}
			else {
				Attribute attrib = new Attribute(fld);
				if(attrib.existsForValue(attributeValue)) {
					System.out.println(attrib.getMessage());
					return fld;
				}
				return null;
			}
		}
	}

	/**
	 * This only works if javascript is enabled on the driver.
	 * @param childElement
	 * @return
	 */
	private WebElement getParentElement(WebElement childElement) {
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		WebElement parentElement = (WebElement)executor.executeScript("return arguments[0].parentNode;", childElement);
		return parentElement;
	}
}
