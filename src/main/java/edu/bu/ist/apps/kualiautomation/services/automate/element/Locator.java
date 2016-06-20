package edu.bu.ist.apps.kualiautomation.services.automate.element;

import org.openqa.selenium.WebDriver;

public interface Locator {

	public Element locate(String label, ElementType elementType);
	
	public WebDriver getDriver();
	
}
