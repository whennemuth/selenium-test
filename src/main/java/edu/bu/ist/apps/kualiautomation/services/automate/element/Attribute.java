package edu.bu.ist.apps.kualiautomation.services.automate.element;

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
	private String[] attributesToCheck = new String[] {
			"id", "title", "name", "value"
	};
	
	public Attribute(WebElement webElement) {
		this.webElement = webElement;
	}

	/**
	 * Check attributes of the webElement for the specified attributeValue in the following order:
	 *     id, title, name, value
	 * @param attributeValue
	 * @return
	 */
	public boolean existsForValue(String attributeValue) {
		this.attributeValue = attributeValue;
		
		for(String attrib : attributesToCheck) {
			String attribval = webElement.getAttribute(attrib);
			if(attribval != null && attribval.equalsIgnoreCase(attributeValue)) {
				return true;
			}
		}

		return false;
	}
	
	public boolean existsForValue(String attributeValue, String[] attributesToCheck) {
		this.attributesToCheck = attributesToCheck;
		return existsForValue(attributeValue);
	}
	
	public String getValue() {
		return attributeValue;
	}

	public String getName() {
		return attributeName;
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

}
