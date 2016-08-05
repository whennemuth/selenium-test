package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebDriver;

import edu.bu.ist.apps.kualiautomation.entity.LabelAndValue;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
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
	private LabelAndValue lv;
	private Locator locator;
	
	@SuppressWarnings("unused")
	private LocatorRunner() { /* Restrict the default constructor */ }
	
	public LocatorRunner(WebDriver driver, LabelAndValue lv) {
		this.driver = driver;
		this.lv = lv;
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
		
		List<Element> elements = new ArrayList<Element>();
		
		switch(lv.getElementTypeEnum()) {
		case BUTTON: case BUTTONIMAGE:
			elements = runBatch(new Class<?>[]{
				LabelledElementLocator.class,
				BasicElementLocator.class // the label in LabelAndValue will be treated as an attribute of the sought element
			});
			break;
		case HYPERLINK:
			elements = runBatch(new Class<?>[]{
				HyperlinkElementLocator.class,
				HotspotElementLocator.class
			});
			break;
		case TEXTBOX: case PASSWORD: case TEXTAREA: case SELECT: case RADIO: case CHECKBOX:
			elements.addAll(locateElements(new LabelledElementLocator(driver)));
			break;
		case HOTSPOT:
			elements.addAll(locateElements(new HotspotElementLocator(driver)));
			break;
		case SHORTCUT:
			elements.addAll(locateElements(new ShortcutElementLocator(driver, lv.getConfigShortcut())));
			break;
		case OTHER:
			elements = runBatch(new Class<?>[]{
				LabelledElementLocator.class,
				HotspotElementLocator.class,
				(lv.getConfigShortcut() == null ? null : ShortcutElementLocator.class)
			});
			break;
		default:
			break;
		}
		
		if(elements.isEmpty()) {
			System.out.println("Could not locate " + String.valueOf(lv.getLabel() + ": " + lv.getElementType()));
		}
		else if(elements.size() > 1){
			System.out.println("Located multiple for " + String.valueOf(lv.getLabel() + ": " + lv.getElementType()));
		}
		else {
			System.out.println("Located " + String.valueOf(lv.getLabel() + ": " + lv.getElementType()));
		}
		
		return elements;
	}
	
	private List<Element> locateElements(Locator locator) {
		return locator.locateAll(lv.getElementTypeEnum(), Arrays.asList(new String[]{ lv.getLabel() }));
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
			if(classes[i] == null)
				continue;
			StringBuilder s = new StringBuilder(classes[i].getName())
				.append(BatchElementLocator.PARAMETER_DELIMITER)
				.append(lv.getLabel());
			if(!Utils.isEmpty(lv.getIdentifier())) {
				s.append(BatchElementLocator.PARAMETER_DELIMITER).append(lv.getIdentifier());
			}
			parameters[i] = s.toString();
		}
		List<Element> elements = locator.locateAll(lv.getElementTypeEnum(), Arrays.asList(parameters));
		
		return elements;
	}
}
