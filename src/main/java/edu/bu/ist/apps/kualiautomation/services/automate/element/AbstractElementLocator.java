package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class AbstractElementLocator implements Locator {

	protected WebDriver driver;
	protected ElementType elementType;
	protected List<String> attributes = new ArrayList<String>();
	
	public AbstractElementLocator(WebDriver driver) {
		this.driver = driver;
	}
	
	@Override
	public Element locateFirst(ElementType elementType, List<String> attributes) {
		
		List<Element> results = locateAll(elementType, attributes);
		if(results.isEmpty())
			return null;
		
		return results.get(0);
	}
	
	@Override
	public List<Element> locateAll(ElementType elementType, List<String> attributes) {
		
		this.elementType = elementType;
		this.attributes = attributes;
		final List<WebElement> webElements = new ArrayList<WebElement>();
		List<Element> results = new ArrayList<Element>();
		
		customLocate(webElements);
		
		if(webElements.isEmpty()) {
			defaultLocate(webElements);
		}
		
		if(!webElements.isEmpty()) {
			for(WebElement we : webElements) {
				results.add(new BasicElementImpl(driver, we));
			}
		}
		
		return results;
	}
	
	/**
	 * Attributes are evaluated in the following order, with the element of the first matching attribute being returned:
	 *    1) id
	 *    2) name
	 */
	protected void defaultLocate(List<WebElement> located) {
		if(attributes.isEmpty() || elementType == null)
			return;
		
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
