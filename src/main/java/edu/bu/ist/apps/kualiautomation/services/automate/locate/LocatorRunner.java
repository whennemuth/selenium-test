package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.Arrays;

import org.openqa.selenium.WebDriver;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;

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
			// RESUME NEXT: write code here.
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
		String[] parameters = new String[classes.length];
		for(int i=0; i<classes.length; i++) {
			parameters[i] = classes[i].getName() + ":" + attribute;
		}
		Element element = locator.locateFirst(elementType, Arrays.asList(parameters));
		return element;
	}
}
