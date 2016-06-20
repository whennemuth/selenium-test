package edu.bu.ist.apps.kualiautomation.services.element;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class AbstractElementLocator implements Locator {

	protected WebDriver driver;
	protected String label;
	protected ElementType elementType;
	
	public AbstractElementLocator(WebDriver driver) {
		this.driver = driver;
	}
	
	@Override
	public Element locate(String label, ElementType elementType) {
		
		this.label = label;
		this.elementType = elementType;
		final List<WebElement> results = new ArrayList<WebElement>();
		
		customLocate(results);
		
		if(results.isEmpty()) {
			defaultLocate(results);
			if(results.isEmpty()) {
				return null;
			}
		}
		
		return new BasicElementImpl(driver, results.get(0));
	}
	
	protected void defaultLocate(List<WebElement> located) {
		
		switch(elementType) {
		case BUTTON:
			break;
		case CHECKBOX:
			break;
		case HYPERLINK:
			break;
		case TEXTBOX:
			break;
		case TEXTAREA:
			break;
		case SELECT:
			break;
		case RADIO:
			break;
		case OTHER:
			break;
		}
	}
	
	protected abstract void customLocate(List<WebElement> located);

	@Override
	public WebDriver getDriver() {
		return driver;
	}

}
