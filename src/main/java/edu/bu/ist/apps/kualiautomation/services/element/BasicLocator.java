package edu.bu.ist.apps.kualiautomation.services.element;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class BasicLocator implements Locator {

	private WebDriver driver;
	private String label;
	private ElementType elementType;
	
	public BasicLocator(WebDriver driver) {
		this.driver = driver;
	}
	
	@Override
	public Element locate(String label, ElementType elementType) {
		
		this.label = label;
		this.elementType = elementType;
		List<WebElement> results = new ArrayList<WebElement>();
		
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
		
		extraLocate(results);
		
		if(results.isEmpty()) {
			return null;
		}

		Element element = new Element() {
			@Override public WebElement getWebElement() {
				return results.get(0);
			}
			@Override public boolean isVisible() {
				return true;
			}			
		};
		
		return element;
	}
	
	protected abstract void extraLocate(List<WebElement> located);

	public WebDriver getDriver() {
		return driver;
	}

}
