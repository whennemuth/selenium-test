package edu.bu.ist.apps.kualiautomation.services.element;

import org.openqa.selenium.WebElement;

public interface Element {

	public WebElement getWebElement();
	
	public boolean isInteractive();
	
	public void click();
	
	public void setValue(String value);
}