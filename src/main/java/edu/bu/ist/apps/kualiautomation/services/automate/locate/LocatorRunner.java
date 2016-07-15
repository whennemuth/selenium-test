package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebDriver;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.util.Utils;

/**
 * This class is used to locate a single web element on an html page with selenium.
 * Initially given the element type, one or more implementations of Locator are used, one after the other
 * using the BatchElementLocator, until one of them finds the element.
 * 
 * @author wrh
 *
 */
public class LocatorRunner {

	private WebDriver driver;
	private ElementType elementType;
	private String label;
	private String attribute;
	private Locator locator;
	
	@SuppressWarnings("unused")
	private LocatorRunner() { /* Restrict the default constructor */ }
	
	public LocatorRunner(WebDriver driver, ElementType elementType, String label) {
		this.driver = driver;
		this.elementType = elementType;
		this.label = label;
	}
	
	public LocatorRunner(WebDriver driver, ElementType elementType, String label, String attribute) {
		this(driver, elementType, label);
		this.attribute = attribute;
	}
	
	public List<Element> run() {
		return run(false);
	}

	/**
	 * Find an element on a web page, returning it or multiple elements if the search criteria yielded more than one match.
	 * @param greedy Stop searching for elements that match the criteria after the first successful locate attempt.
	 * @return
	 */
	public List<Element> run(boolean greedy) {
		
		List<Element> element = null;
		
		switch(elementType) {
		case BUTTON:
			element = runBatch(new Class<?>[]{
				BasicElementLocator.class,
				LabelledElementLocator.class
			});
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
			// TODO: write code here.
			break;
		case TEXTBOX: case PASSWORD:
			locator = new LabelledElementLocator(driver);
			element = locator.locateAll(elementType, Arrays.asList(new String[]{ label }));
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
	
	/**
	 * Locate an element using the BatchElementLocator.
	 * Construct strings, one for each Locator implementation to invoke, constructing a string for each that  
	 * identifies the Class Locator to use, and a delimited list of parameters (label and attribute).
	 * 
	 * @param classes
	 * @return
	 */
	private List<Element> runBatch(Class<?>[] classes) {
		locator = new BatchElementLocator(driver);
		String[] parameters = new String[classes.length];
		for(int i=0; i<classes.length; i++) {
			StringBuilder s = new StringBuilder(classes[i].getName())
				.append(BatchElementLocator.PARAMETER_DELIMITER)
				.append(label);
			if(!Utils.isEmpty(attribute)) {
				s.append(BatchElementLocator.PARAMETER_DELIMITER).append(attribute);
			}
			parameters[i] = s.toString();
		}
		List<Element> elements = locator.locateAll(elementType, Arrays.asList(parameters));
		
		return elements;
	}
}
