package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.Locator;

public class LocateResultAssertion {
	private String url;
	private ElementType elementType;
	private int numResults;
	private String label;
	private List<String> attributeValues = new ArrayList<String>();
	private Map<String, String> attributeAssertions = new HashMap<String, String>();
	private Locator locator;
	
	@SuppressWarnings("unused")
	private LocateResultAssertion() { /* Restrict default constructor */ }
	
	public LocateResultAssertion(Locator locator) {
		this.locator = locator;
	}
	
	/**
	 * Execute the locate method of the locator to produce the list of elements.
	 * Assert the list is not empty and for each element in the list make assertions against its attributes.
	 * 
	 * @return
	 */
	public List<Element> findAndAssertElements() {
		if(locator.getWebDriver().getCurrentUrl() == null || !locator.getWebDriver().getCurrentUrl().equalsIgnoreCase(url)) {
			locator.getWebDriver().get(url);
		}
		
		List<Element> elements = locator.locateAll(elementType, attributeValues);
		assertNotNull(elements);	
		assertFalse(elements.isEmpty());
		assertEquals(numResults, elements.size());
		for(Element element : elements) {
			assertElement(element);
		}
		
		return elements;
	}
	
	/**
	 * Make all the assertions in attributeAssertions against the provided element.
	 * @param element
	 */
	private void assertElement(Element element) {
		assertEquals(elementType.getTagname(), element.getWebElement().getTagName().toLowerCase());
		assertTrue(areNullOrEqual(elementType.getTypeAttribute(), element.getWebElement().getAttribute("type")));
		for(String attributeName: attributeAssertions.keySet()) {
			String assertValue = attributeAssertions.get(attributeName);
			String actualValue = element.getWebElement().getAttribute(attributeName);
			assertTrue(areNullOrEqual(assertValue, actualValue));
		}
	}
	
	private boolean areNullOrEqual(String val1, String val2) {
		if(val1 == null && val2 == null)
			return true;
		if(val1 == null || val2 == null)
			return false;
		return val1.equalsIgnoreCase(val2);
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public ElementType getElementType() {
		return elementType;
	}
	public void setElementType(ElementType elementType) {
		this.elementType = elementType;
	}
	public int getNumResults() {
		return numResults;
	}
	public void setNumResults(int numResults) {
		this.numResults = numResults;
	}
	public String getLabel() {
		return label;
	}
	/**
	 * A label is just another attribute, but it must be the first in the list.
	 * @param label
	 */
	public void setLabel(String label) {
		if(this.label == null) {
			attributeValues.add(0, label);
		}
		else {
			attributeValues.set(0, label);
		}
		this.label = label;
	}
	public List<String> getAttributeValues() {
		return attributeValues;
	}
	public void setAttributeValues(List<String> attributeValues) {
		this.attributeValues = attributeValues;
	}
	public void addAttributeValue(String attributeValue) {
		attributeValues.add(attributeValue);
	}
	public Map<String, String> getAttributeAssertions() {
		return attributeAssertions;
	}
	public void setAttributeAssertions(Map<String, String> attributeAssertions) {
		this.attributeAssertions = attributeAssertions;
	}
	public void addAttributeAssertion(String attributeName, String assertValue) {
		attributeAssertions.put(attributeName, assertValue);
	}
	
}
