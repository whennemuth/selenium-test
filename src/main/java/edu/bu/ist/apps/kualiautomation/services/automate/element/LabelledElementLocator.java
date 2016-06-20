package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
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

	private WebElement getInputField(WebElement label) {
		StringBuilder xpath = new StringBuilder(elementType.getTagname());
		if(elementType.getTypeAttribute() != null) {
			xpath.append("[@type='")
			.append(elementType.getTypeAttribute())
			.append("']");
		}
		
		List<WebElement> flds = label.findElements(By.xpath(xpath.toString()));
		
		if(flds.isEmpty()) {
			return null;
		}
		else {
			return flds.get(0);
		}
	}

}
