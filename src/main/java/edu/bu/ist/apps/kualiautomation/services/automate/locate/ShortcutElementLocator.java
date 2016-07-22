package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;

public class ShortcutElementLocator implements Locator {

	private WebDriver driver;
	
	public ShortcutElementLocator(WebDriver driver) {
		this.driver = driver;
	}

	@Override
	public Element locateFirst(ElementType elementType, List<String> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Element> locateAll(ElementType elementType, List<String> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebDriver getDriver() {
		// TODO Auto-generated method stub
		return driver;
	}

	@Override
	public boolean busy() {
		// TODO Auto-generated method stub
		return false;
	}

}
