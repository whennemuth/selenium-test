package edu.bu.ist.apps.kualiautomation.services.element;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BasicLocatorImpl extends BasicLocator {

	public BasicLocatorImpl(WebDriver driver) {
		super(driver);
	}

	@Override
	protected void extraLocate(List<WebElement> located) {
		return;
	}

}
