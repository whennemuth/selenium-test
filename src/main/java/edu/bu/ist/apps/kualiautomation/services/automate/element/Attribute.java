package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebElement;

/**
 * Check attributes of the webElement for the specified attributeValue in the order found in attributesToCheck.
 * 
 * @author wrh
 *
 */
public class Attribute {

	private WebElement webElement;
	private String attributeValue;
	private String attributeName;
	private ElementType elementType;
	private List<String> attributesToCheck = new ArrayList<String>();
	static String[] DEFAULT_ATTRIBUTES_TO_CHECK = new String[] {
			"id", "title", "name", "value"
	};
	
	public Attribute(WebElement webElement) {
		this.webElement = webElement;
		this.elementType = ElementType.getInstance(webElement);
	}

	/**
	 * Check attributes of the webElement for the specified attributeValue in the following order:
	 *     id, title, name, value
	 * @param attributeValue
	 * @return
	 */
	public boolean existsForValue(String attributeValue) {
		if(attributesToCheck.isEmpty()) {
			this.attributesToCheck = Arrays.asList(DEFAULT_ATTRIBUTES_TO_CHECK);
		}
		this.attributeValue = attributeValue;
		
		for(String attrib : attributesToCheck) {
			if(elementType.acceptsKeystrokes() && "value".equalsIgnoreCase(attrib)) {
				// This attribute will have a value that is the result of user input, so skip it.
				continue;
			}
			String attribval = webElement.getAttribute(attrib);
			if(attribval != null && attribval.equalsIgnoreCase(attributeValue)) {
				attributeName = attrib;
				return true;
			}
		}

		return false;
	}
	
	public boolean existsForValue(String attributeValue, String[] attributesToCheck) {
		this.attributesToCheck = Arrays.asList(attributesToCheck);
		return existsForValue(attributeValue);
	}
	
	public String getValue() {
		return attributeValue;
	}

	public String getName() {
		return attributeName;
	}
	
	public WebElement getWebElement() {
		return webElement;
	}

	public String getMessage() {
		StringBuilder s = new StringBuilder();
		if(attributeName == null) {
			s.append("Could not find ")
				.append(webElement.getTagName())
				.append(" element attribute having value \"")
				.append(attributeValue)
				.append("\"");
		}
		else {
			s.append("Found ")
				.append(webElement.getTagName())
				.append(" element attribute \"")
				.append(attributeName)
				.append("\" having value \"")
				.append(attributeValue)
				.append("\"");
		}
		return s.toString();
	}

	/**
	 * For a given list of webElements, find each that has an attribute with the specified attribute value.
	 * Of those results, return a list of those whose attribute match are foremost in the array of attributes to check.
	 *   
	 * @param webElements
	 * @param attributeValue
	 * @return
	 */
	public static List<WebElement> findForValue(List<WebElement> webElements, String attributeValue) {
		List<Attribute> candidates = new ArrayList<Attribute>();
		List<WebElement> results = new ArrayList<WebElement>();
		
		// Find any WebElements that have any attribute that with attributeValue as a value.
		for(WebElement we : webElements) {
			Attribute attribute = new Attribute(we);
			if(attribute.existsForValue(attributeValue)) {
				candidates.add(attribute);
			}
		}
		
		// Pick from the candidates list only those attributes whose names are foremost in the array of attributes to check
		// For example, if candidates contained 2 id matches and 2 title matches, only the 2 id matches would be picked.
		for(String attrib : DEFAULT_ATTRIBUTES_TO_CHECK) {
			if(!results.isEmpty()) {
				break;
			}
			for(Attribute a : candidates) {
				if(attrib.equalsIgnoreCase(a.getName())) {
					results.add(a.getWebElement());
				}
			}
		}
		
		return results;
	}
	
	/**
	 * For a given list of webElements, find each that has every one of the specified 
	 * attribute values accounted for among its attributes.
	 *  
	 * @param webElements
	 * @param attributeValues
	 * @return
	 */
	public static List<WebElement> findForValues(List<WebElement> webElements, List<String> attributeValues) {
		List<WebElement> results = new ArrayList<WebElement>();
		outerloop:
		for(WebElement we : webElements) {			 
			for(String attributeValue : attributeValues) {
				Attribute attribute = new Attribute(we);
				if(!attribute.existsForValue(attributeValue)) {
					continue outerloop;
				}
			}
			results.add(we);
		}
		return results;
	}
}
