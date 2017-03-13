package edu.bu.ist.apps.kualiautomation.services.automate.locate.label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.services.automate.element.AbstractWebElement;
import edu.bu.ist.apps.kualiautomation.services.automate.element.BasicElement;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.AbstractElementLocator;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.BasicComparableLabel;
import edu.bu.ist.apps.kualiautomation.util.Utils;

public class LabelElementLocator extends AbstractElementLocator {
	
	/**
	 * 1) First priority (NOTE: lower-case if for xpath v2.0, but firefox uses v1.0, so have to use the translate function).
	 * Matches an element with a text value that matches the provided value, both values normalized for whitespace.
	 */
	private static final String XPATH_EQUALS = 
			"//*[text()[normalize-space(translate(., "
			+ "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', "
			+ "'abcdefghijklmnopqrstuvwxyz'))=\"[INSERT-LABEL]\"]]";
	
	/**
	 * 2) Second priority
	 * Matches for an element with a text value that starts with the provided value, both values normalized for whitespace.
	 */
	private static final String XPATH_STARTS_WITH = 
			"//*[text()[starts-with(normalize-space(translate(., "
			+ "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', "
			+ "'abcdefghijklmnopqrstuvwxyz')), \"[INSERT-LABEL]\")]]";
	
	/**
	 * 3) Third priority
	 * Matches for an element with a text value that contains the provided value, both values normalized for whitespace.
	 */
	private static final String XPATH_CONTAINS = 
			"//*[text()[contains(normalize-space(translate(., "
			+ "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', "
			+ "'abcdefghijklmnopqrstuvwxyz')), \"[INSERT-LABEL]\")]]";
	
	private boolean labelCanBeHyperlink = true;
	
	private static final String[] DISALLOWED_TAGNAMES = new String[] {
		"option"
	};
	
	private LabelElementLocator() {
		super(null); // Restrict the default constructor
	}
	public LabelElementLocator(WebDriver driver){
		super(driver);
	}
	public LabelElementLocator(WebDriver driver, SearchContext searchContext){
		super(driver, searchContext);
	}
	
	public Element locate(String label) {
		return super.locateFirst(null, Arrays.asList(new String[]{label}));
	}
	
	@Override
	protected List<WebElement> customLocate() {
		
		List<WebElement> located = new ArrayList<WebElement>();
		String label = new String(parameters.get(0)).trim().toLowerCase();
		String cleanedLabel = ComparableLabel.getCleanedValue(label).toLowerCase();
		String scope = "";	// global
		if(searchContext instanceof WebElement) {
			scope = ".";	// current scope within element.
		}
		
		/**
		 * Search for an element that matches the provided value with xpath expressions in order of their priority.
		 * Only proceed to the next search if no results have yet been found with the prior search(es).
		 */
		
		/** 1) Search for full length match */
		String xpath = scope + XPATH_EQUALS.replace("[INSERT-LABEL]", label);
		List<WebElement> elements = AbstractWebElement.wrap(searchContext.findElements(By.xpath(xpath)));
		applyFiltering(elements, label);
		
		/** 2) Search for full length match where the label is the value of a button
		 * <input type="[value]"> or... <button>[value]</button> 
		 * (yes, some applications disguise buttons as hotspots or links).
		 */
		if(elements.isEmpty()) {
			elements = ElementType.BUTTON.findAll(searchContext);
			applyFiltering(elements, label);
		}
		
		/** 3) Search for match that starts with the provided value */
		if(elements.isEmpty()) {
			xpath = scope + XPATH_STARTS_WITH.replace("[INSERT-LABEL]", cleanedLabel);
			elements = AbstractWebElement.wrap(searchContext.findElements(By.xpath(xpath)));
			applyFiltering(elements, label);
			
			if(!elements.isEmpty()) {
				/**
				 * The above xpath will not include text wrapped in an html block as part of the innerText
				 * being evaluated. For this reason, results could be returned that do not appear to the
				 * user to start with the specified value. Trim these edge cases off the list. 
				 */
				for (Iterator<WebElement> iterator = elements.iterator(); iterator.hasNext();) {
					WebElement we = iterator.next();
					String text = getText(driver, we);
					if(!text.toLowerCase().trim().startsWith(label.toLowerCase())) {
						iterator.remove();
					}
				}
			}
		}
		
		/** 4) Search for a match that would start with the provided value if garbage characters 
		 * are trimmed from the start.
		 */
		if(elements.isEmpty()) {
			xpath = scope + XPATH_CONTAINS.replace("[INSERT-LABEL]", cleanedLabel);
			elements = AbstractWebElement.wrap(searchContext.findElements(By.xpath(xpath)));
			applyFiltering(elements, label);
		}

		// Wrap the web elements in ComparableLabel instances for sorting so higher ranked results are on top.
		List<ComparableLabel> labels = new ArrayList<ComparableLabel>();
		for(WebElement elmt : elements) {
			if(!isDisallowedHyperlink(elmt)) {
				String text = null;
				if(isButton(elmt)) {
					text = elmt.getAttribute("value");
					if(Utils.isEmpty(text) && "button".equalsIgnoreCase(elmt.getTagName())) {
						text = elmt.getText();
					}
				}
				else {
					text = getText(driver, elmt);
				}
				labels.add(new BasicComparableLabel(elmt, label, text));
			}
		}
		
		// Unwrap the highest ranked result(s) back into a WebElement collection.
		located.addAll(ComparableLabel.getHighestRanked(labels));
		
		// We are only searching for labels, but if the search fails then the upcoming default search
		// will try to find fields. Prevent this by indicating the default search has already run.
		defaultRan = true;
		
		return located;
	}
	
	/**
	 * Remove from the list of located label web elements those that have a tagname that is not 
	 * qualified or are buttons whose value does not match the value the label search was based on.
	 * 
	 * @param elements
	 * @param label
	 */
	private void applyFiltering(List<WebElement> elements, String label) {
		for (Iterator<WebElement> iterator = elements.iterator(); iterator.hasNext();) {
			WebElement we = (WebElement) iterator.next();
			if(ignoreHidden && !we.isDisplayed()) {
				iterator.remove();
			}
			else if(ignoreDisabled && !we.isEnabled()) {
				iterator.remove();
			}
			else {
				for(String tagname : DISALLOWED_TAGNAMES) {
					if(tagname.equalsIgnoreCase(we.getTagName())) {
						iterator.remove();
					}
				}
				if(isButton(we)) {
					String val = we.getAttribute("value");
					if(Utils.isEmpty(val) && "button".equalsIgnoreCase(we.getTagName())) {
						val = we.getText();
					}
					if(Utils.isEmpty(val)) {
						iterator.remove();
					}
					else {
						val = val.trim();
						if(!label.equalsIgnoreCase(val)) {
							iterator.remove();
						}
					}
				}
			}
		}		
	}
	
	private boolean isButton(WebElement we) {
		return ElementType.getInstance(we).is(ElementType.BUTTON.name());
	}
	
	@Override
	protected Element getElement(WebDriver driver, WebElement we) {
		return new BasicElement(driver, we);
	}
	
	public boolean isDisallowedHyperlink(WebElement we) {
		return "a".equalsIgnoreCase(we.getTagName()) && labelCanBeHyperlink == false;
	}
	
	public boolean isLabelCanBeHyperlink() {
		return labelCanBeHyperlink;
	}
	
	public void setLabelCanBeHyperlink(boolean labelCanBeHyperlink) {
		this.labelCanBeHyperlink = labelCanBeHyperlink;
	}

}
