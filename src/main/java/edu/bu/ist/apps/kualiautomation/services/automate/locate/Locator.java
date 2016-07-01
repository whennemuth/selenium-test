package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.List;

import org.openqa.selenium.WebDriver;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;

public interface Locator {

	public Element locateFirst(ElementType elementType, List<String> parameters);

	public List<Element> locateAll(ElementType elementType, List<String> parameters);
	
	public WebDriver getDriver();
	
	public boolean busy();
}
