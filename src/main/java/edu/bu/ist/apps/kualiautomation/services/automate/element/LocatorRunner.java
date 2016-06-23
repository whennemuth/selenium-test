package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.Arrays;

import org.openqa.selenium.WebDriver;

public class LocatorRunner {

	private WebDriver driver;
	private ElementType elementType;
	private String attribute;
	private Locator locator;
	
	@SuppressWarnings("unused")
	private LocatorRunner() { /* Restrict the default constructor */ }
	
	public LocatorRunner(WebDriver driver, ElementType elementType, String attribute) {
		this.driver = driver;
		this.elementType = elementType;
		this.attribute = attribute;
	}
	
	public Element run() {
		return run(false);
	}
	
	// RESUME NEXT: unit test what's here so far and then fill out the remaining cases and test.
	public Element run(boolean greedy) {
		
		Element element = null;
		
		switch(elementType) {
		case BUTTON:
			break;
		case BUTTONIMAGE:
			element = runBatch(new Class<?>[]{
				BasicElementLocator.class,
				LabelledElementLocator.class
			});
			break;
		case CHECKBOX:
			break;
		case HYPERLINK:
			break;
		case TEXTBOX:
			locator = new LabelledElementLocator(driver);
			element = locator.locateFirst(elementType, Arrays.asList(new String[]{ attribute }));
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
		
		return element;
	}
	
	private Element runBatch(Class<?>[] classes) {
		locator = new BatchElementLocator(driver);
		String[] attributes = new String[classes.length];
		for(int i=0; i<classes.length; i++) {
			attributes[i] = classes[i].getName() + ":" + attribute;
		}
		Element element = locator.locateFirst(elementType, Arrays.asList(attributes));
		return element;
	}
}
