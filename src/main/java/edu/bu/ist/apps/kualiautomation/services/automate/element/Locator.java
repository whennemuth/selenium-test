package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.List;

import org.openqa.selenium.WebDriver;

public interface Locator {

	public Element locateFirst(ElementType elementType, List<String> attributes);

	public List<Element> locateAll(ElementType elementType, List<String> attributes);
	
	public WebDriver getDriver();
	
}
