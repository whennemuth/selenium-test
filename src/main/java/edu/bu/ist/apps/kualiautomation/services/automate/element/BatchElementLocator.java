package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * This Locator returns elements by invoking other Locator implementations and returning the aggregate results.
 * The other implementations are indicated by class name and invoked through reflection.
 * 
 * @author wrh
 *
 */
public class BatchElementLocator implements Locator {
	
	private WebDriver driver;
	
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
		
		List<Element> results = new ArrayList<Element>();
		
		for(Class<?> clazz : parms.keySet()) {
			try {
				Constructor<?> ctr = clazz.getConstructor(WebDriver.class);
				List<String> attributes = parms.get(clazz);
				Locator locator = (Locator) ctr.newInstance(driver);
				Element result = locator.locateFirst(elementType, attributes);
				results.add(result);
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


	public static void main(String args) {
		WebDriver driver = null;
		try {
			driver = new HtmlUnitDriver();
			driver.get("http://google.com");
			BatchElementLocator locator = new BatchElementLocator(driver);
			locator.locateAll(ElementType.TEXTBOX, Arrays.asList(new String[]{
					"edu.bu.ist.apps.kualiautomation.services.automate.element.LabelElementLocator:",
					"edu.bu.ist.apps.kualiautomation.services.automate.element.LabelledElementLocator:"
			}));
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