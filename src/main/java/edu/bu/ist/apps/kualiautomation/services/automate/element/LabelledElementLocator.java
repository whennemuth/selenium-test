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
			
			String label = new String(attributes.get(0));
			LabelElementLocator labelLocator = new LabelElementLocator(driver);
			List<Element> candidates = labelLocator.locateAll(elementType, Arrays.asList(new String[]{label}));
			
			for(Element labelElement : candidates) {
				WebElement fld = getInputField(labelElement.getWebElement());
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
	private WebElement getInputField(WebElement element) {
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
				return getInputField(parent);
			}
			return null;
		}
		else {
			return flds.get(0);
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
