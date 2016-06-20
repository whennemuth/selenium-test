package edu.bu.ist.apps.kualiautomation.services.element;

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
		if(elementType.getTagname() != null) {
			List<WebElement> elements = 
					// NOT WORKS:
					//driver.findElements(By.xpath("//div"));
					//driver.findElements(By.xpath("//text()['" + label + "')]"));
					//driver.findElements(By.xpath("//descendent::text()[contains(., '" + label + "')]"));
					//driver.findElements(By.tagName(elementType.getTagname()));
					// WORKS:
					driver.findElements(By.xpath("//*[text()[contains(., \"" + label + "\")]]"));
					//driver.findElements(By.xpath("//*[text()='" + label + "']"));

			located.addAll(elements);
		}
	}

}
