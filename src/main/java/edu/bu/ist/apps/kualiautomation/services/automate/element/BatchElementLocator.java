package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * This locator works by trying the of one or more other locators and returning their output. 
 * It returns search results for the element by invoking other Locator implementations and returning the aggregate results.
 * The other implementations are indicated by class name and invoked through reflection.
 * 
 * @author wrh
 *
 */
public class BatchElementLocator implements Locator {
	
	private WebDriver driver;
	private boolean defaultRan;
	
	public BatchElementLocator(WebDriver driver) {
		this.driver = driver;
	}

	public Element locateFirst(ElementType elementType, Map<Class<?>, List<String>> parms) {		
		List<Element> results = locateAll(elementType, parms, false);
		if(results.isEmpty())
			return null;
		
		return results.get(0);
	}
	
	private List<Element> locateAll(ElementType elementType, Map<Class<?>, List<String>> parms, boolean greedy) {
		
		defaultRan = false;
		List<Element> results = new ArrayList<Element>();
		
		for(Class<?> clazz : parms.keySet()) {
			try {
				Constructor<?> ctr = clazz.getConstructor(WebDriver.class);
				List<String> attributes = parms.get(clazz);
				Locator locator = (Locator) ctr.newInstance(driver);
				
				Element result = runLocator(locator, elementType, attributes);
				
				if(result != null) {
					results.add(result);
				}
				if(greedy)
					continue;
				else
					break;
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return results;		
	}
	
	/**
	 * Run the locator. If a locator is of type AbstractElementLocator, it will always run the default locate method 
	 * unless flagged not to do so, therefore set the defaultRan boolean field to true after the first locator has run
	 * so that subsequent locators in the batch do not also run this default location method.
	 *  
	 * @param locator
	 * @param elementType
	 * @param attributes
	 * @return
	 */
	private Element runLocator(Locator locator, ElementType elementType, List<String> attributes) {
		if(defaultRan) {
			if(locator instanceof AbstractElementLocator) {
				((AbstractElementLocator) locator).setDefaultRan(true);
			}
		}
		Element result = locator.locateFirst(elementType, attributes);
		defaultRan = true;
		return result;
	}
	
	@Override
	public Element locateFirst(ElementType elementType, List<String> parms) {
		return locateFirst(elementType, parseParms(parms));
	}

	@Override
	public List<Element> locateAll(ElementType elementType, List<String> parms) {
		return locateAll(elementType, parseParms(parms), true);
	}
	
	private Map<Class<?>, List<String>> parseParms(List<String> parms) {
		Map<Class<?>, List<String>> map = new LinkedHashMap<Class<?>, List<String>>();
		for(String parm : parms) {
			String[] parts = parm.split(":");
			String classname = parts[0];
			Class<?> clazz = null;
			try {
				clazz = Class.forName(classname);
			} 
			catch (ClassNotFoundException e) {
				e.printStackTrace();
				continue;
			}
			List<String> attributes = new ArrayList<String>();
			if(parts.length > 1) {
				for(int i=1; i<parts.length; i++) {
					attributes.add(parts[i]);
				}
			}
			map.put(clazz, attributes);
		}
		return map;
	}

	@Override
	public WebDriver getDriver() {
		return driver;
	}


	public static void main(String[] args) {
		WebDriver driver = null;
		try {
			driver = new HtmlUnitDriver();
			driver.get("file:///C:/whennemuth/workspaces/bu_workspace/selenium-test/src/test/resources/html/ProposalLogLookupFrame.htm");
			BatchElementLocator locator = new BatchElementLocator(driver);
			List<Element> elements = locator.locateAll(ElementType.TEXTBOX, Arrays.asList(new String[]{
					"edu.bu.ist.apps.kualiautomation.services.automate.element.LabelElementLocator:Proposal Number",
					"edu.bu.ist.apps.kualiautomation.services.automate.element.LabelledElementLocator:Proposal Number"
			}));
			for(Element e : elements) {
				System.out.println(e.getWebElement().getTagName() + ": " + e.getWebElement().getText());
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(driver != null) {
				driver.close();
				driver.quit();
			}
		}

	}
}
