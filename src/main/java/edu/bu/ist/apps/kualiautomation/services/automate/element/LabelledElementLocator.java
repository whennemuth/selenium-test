package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LabelledElementLocator extends AbstractElementLocator {

	public LabelledElementLocator(WebDriver driver) {
		super(driver);
	}

	@Override
	protected void customLocate(List<WebElement> located) {
		if(elementType != null && elementType.getTagname() != null) {

			List<WebElement> labels = new ArrayList<WebElement>();
			locateLabels(labels);
			
			for(WebElement label : labels) {
				WebElement fld = getInputField(label);
				if(fld != null) {
					located.add(fld);
					break;
				}
			}
		}
	}

	private WebElement getInputField(WebElement element) {
		StringBuilder xpath = new StringBuilder(elementType.getTagname());
		if(elementType.getTypeAttribute() != null) {
			xpath.append("[@type='")
			.append(elementType.getTypeAttribute())
			.append("']");
		}
// RESUME NEXT: Add descendent search to xpath expression.		
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
