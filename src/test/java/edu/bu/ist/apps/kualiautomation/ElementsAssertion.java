package edu.bu.ist.apps.kualiautomation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.Locator;
import edu.bu.ist.apps.kualiautomation.util.Utils;

/**
 * This class performs the typical tests you would want to make against elements gathered using a locator
 * or any other means.
 * 
 * @author wrh
 *
 */
public class ElementsAssertion {
	private String url;
	private ElementType elementType;
	private int numResults;
	private String label;
	private List<String> attributeValues = new ArrayList<String>();
	private Map<String, String> attributeAssertions = new HashMap<String, String>();
	private Map<String, String> anyAttributeAssertions;
	private Map<String, String> anyAttributeAssertionsFound;
	private String tagNameAssertion;
	private String textAssertion;
	private HashSet<String> anyTextAssertions;
	private HashSet<String> anyTextFound;
	private Locator locator;
	private boolean webPageOpen;
	
	@SuppressWarnings("unused")
	private ElementsAssertion() { /* Restrict default constructor */ }
	
	public ElementsAssertion(Locator locator) {
		this.locator = locator;
	}
	
	/**
	 * Execute the locate method of the locator to produce the list of elements.
	 * Assert the list is not empty and for each element in the list make assertions against its attributes.
	 * 
	 * @return
	 */
	public List<Element> findAndAssertElements() {
		
		openWebPage();
		
		List<Element> elements = locator.locateAll(elementType, attributeValues);
		
		assertElements(elements);
		
		return elements;
	}
	
	public void assertElements(List<Element> elements) {
		assertNotNull(elements);
		if(numResults <= 0 ) {
			assertTrue(elements.isEmpty());
		}
		else {
			assertFalse(elements.isEmpty());
			assertEquals(numResults, elements.size());
			for(Element element : elements) {
				assertElement(element);
			}
		}
		if(anyTextAssertions != null && !anyTextAssertions.isEmpty()) {
			StringBuilder failMsg = new StringBuilder("Could not find all expected text() values:\n   UNFOUND: (");
			for (Iterator<String> iterator = anyTextAssertions.iterator(); iterator.hasNext();) {
				String s = iterator.next();
				failMsg.append("\"").append(s).append("\"");
				if(iterator.hasNext())
					failMsg.append(", ");
			}
			
			failMsg.append(")\n   FOUND: (");
			
			for (Iterator<String> iterator = anyTextFound.iterator(); iterator.hasNext();) {
				String s = iterator.next();
				failMsg.append("\"").append(s).append("\"");
				if(iterator.hasNext())
					failMsg.append(", ");
			}
			
			fail(failMsg.toString());
		}
		if(anyAttributeAssertions != null && !anyAttributeAssertions.isEmpty()) {
			StringBuilder failMsg = new StringBuilder("Could not find all expected attribute values:\n   UNFOUND: (");

			for (Iterator<String> iterator = anyAttributeAssertions.keySet().iterator(); iterator.hasNext();) {
				String attribName = iterator.next();
				String attribVal = anyAttributeAssertions.get(attribName);
				String s = attribName + ":" +
				failMsg.append("[").append(attribName).append(":").append(attribVal).append("]");
				if(iterator.hasNext())
					failMsg.append(", ");
			}
			
			failMsg.append(")\n   FOUND: (");
			
			for (Iterator<String> iterator = anyAttributeAssertionsFound.keySet().iterator(); iterator.hasNext();) {
				String attribName = iterator.next();
				String attribVal = anyAttributeAssertions.get(attribName);
				String s = attribName + ":" +
				failMsg.append("[").append(attribName).append(":").append(attribVal).append("]");
				if(iterator.hasNext())
					failMsg.append(", ");
			}
			
			fail(failMsg.toString());
		}
	}
	
	public void openWebPage() {
		if(!webPageOpen) {
			if(locator.getWebDriver().getCurrentUrl() == null || !locator.getWebDriver().getCurrentUrl().equalsIgnoreCase(url)) {
				locator.getWebDriver().get(url);
				webPageOpen = true;
			}
		}
	}
	
	/**
	 * Make all the assertions in attributeAssertions against the provided element.
	 * @param element
	 */
	private void assertElement(Element element) {
		if(elementType.getTagname() != null && !elementType.equals(ElementType.HOTSPOT)) {
			assertEquals(elementType.getTagname().toLowerCase(), element.getWebElement().getTagName().toLowerCase());
		}
		if(!elementType.equals(ElementType.HOTSPOT)) {
			assertTrue(areEmptyOrEqual(elementType.getTypeAttribute(), element.getWebElement().getAttribute("type")));
		}
		if(tagNameAssertion != null) {
			assertTrue(tagNameAssertion.equalsIgnoreCase(element.getWebElement().getTagName()));
		}
		if(textAssertion != null) {
			if(element.getWebElement().getText() == null) {
				fail("Expected text = \"" + textAssertion + "\", but was null");
			}
			assertEquals(textAssertion.trim(), element.getWebElement().getText().trim());
		}
		if(anyTextAssertions != null && !anyTextAssertions.isEmpty()) {
			String text = element.getWebElement().getText().trim();
			if(anyTextAssertions.contains(text)) {
				if(anyTextAssertions.remove(text)) {
					anyTextFound.add(text);
				}		
			}
		}
		for(String attributeName: attributeAssertions.keySet()) {
			String assertValue = attributeAssertions.get(attributeName);
			String actualValue = element.getWebElement().getAttribute(attributeName);
			assertTrue(areEmptyOrEqual(assertValue, actualValue));
		}
		if(anyAttributeAssertions != null && !anyAttributeAssertions.isEmpty()) {
			Iterator<Map.Entry<String,String>> iter = anyAttributeAssertions.entrySet().iterator();
			while (iter.hasNext()) {
			    Map.Entry<String,String> entry = iter.next();
			    String attributeName = entry.getKey();
				String assertValue = entry.getValue();
				String actualValue = element.getWebElement().getAttribute(attributeName);
				if(areEmptyOrEqual(assertValue, actualValue)) {
					iter.remove();
				}
			}	
		}
	}
	
	private boolean areEmptyOrEqual(String val1, String val2) {
		if(Utils.isEmpty(val1) && Utils.isEmpty(val2))
			return true;
		if(Utils.isEmpty(val1) || Utils.isEmpty(val2))
			return false;
		return val1.equalsIgnoreCase(val2);
	}
	
	public String getUrl() {
		return url;
	}
	public ElementsAssertion setUrl(String url) {
		this.url = url;
		return this;
	}
	public ElementType getElementType() {
		return elementType;
	}
	public ElementsAssertion setElementType(ElementType elementType) {
		this.elementType = elementType;
		return this;
	}
	public int getNumResults() {
		return numResults;
	}
	public ElementsAssertion setNumResults(int numResults) {
		this.numResults = numResults;
		return this;
	}
	public String getLabel() {
		return label;
	}
	/**
	 * A label is just another attribute, but it must be the first in the list.
	 * @param label
	 */
	public ElementsAssertion setLabel(String label) {
		if(this.label == null) {
			attributeValues.add(0, label);
		}
		else {
			attributeValues.set(0, label);
		}
		this.label = label;
		return this;
	}
	public List<String> getAttributeValues() {
		return attributeValues;
	}
	public ElementsAssertion setAttributeValues(List<String> attributeValues) {
		this.attributeValues = attributeValues;
		return this;
	}
	public ElementsAssertion addAttributeValue(String attributeValue) {
		attributeValues.add(attributeValue);
		return this;
	}
	public Map<String, String> getAttributeAssertions() {
		return attributeAssertions;
	}
	public ElementsAssertion setAttributeAssertions(Map<String, String> attributeAssertions) {
		this.attributeAssertions = attributeAssertions;
		return this;
	}
	public ElementsAssertion addAttributeAssertion(String attributeName, String assertValue) {
		attributeAssertions.put(attributeName, assertValue);
		return this;
	}
	public String getTagNameAssertion() {
		return tagNameAssertion;
	}
	public ElementsAssertion setTagNameAssertion(String tagName) {
		this.tagNameAssertion = tagName;
		return this;
	}
	public String getTextAssertion() {
		return textAssertion;
	}
	public ElementsAssertion setTextAssertion(String text) {
		this.textAssertion = text;
		return this;
	}
	public ElementsAssertion setAnyTextAssertions(String[] anyText) {
		this.anyTextAssertions = new HashSet<String>(Arrays.asList(anyText));
		this.anyTextFound = new HashSet<String>();
		return this;
	}
	public ElementsAssertion addAnyAttributeAssertion(String attributeName, String attributeValue) {
		if(anyAttributeAssertions == null) {
			anyAttributeAssertions = new HashMap<String, String>();
			anyAttributeAssertionsFound = new HashMap<String, String>();
		}
		anyAttributeAssertions.put(attributeName, attributeValue);
		return this;
	}
}
