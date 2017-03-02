package edu.bu.ist.apps.kualiautomation.services.automate.locate.label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.services.automate.element.AttributeInspector;
import edu.bu.ist.apps.kualiautomation.services.automate.element.BasicElement;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.AbstractElementLocator;
import edu.bu.ist.apps.kualiautomation.services.automate.table.TableCellData;
import edu.bu.ist.apps.kualiautomation.services.automate.table.TableData;
import edu.bu.ist.apps.kualiautomation.util.Utils;

/**
 * With a specified WebElement that serves as a label, traverse up the DOM one element at a time until
 * a descendant is found that matches the criteria of elementType. This should be the field that is labelled.
 * 
 * ASSUMPTION: This method assumes that a label and its field will share the same parent, grandparent, etc. - but
 * not with any other labels and fields - other fields and labels exist outside of the shared ancestor.
 * The one exception to this is for fields found in a table row for a particular label. The rules for selecting
 * the correct field for that label among the candiates are covered in 
 * {@link edu.bu.ist.apps.kualiautomation.services.automate.table.TableData TableData}
 * 
 * @author wrh
 *
 */
public class LabelledElementLocator extends AbstractElementLocator {
	
	private boolean labelCanBeHyperlink = true;

	public LabelledElementLocator(WebDriver driver){
		super(driver);
	}
	
	public LabelledElementLocator(WebDriver driver, SearchContext searchContext){
		super(driver, searchContext);
	}
	
	/**
	 * Locate all WebElement instances that sufficiently match ElementType and parameters.
	 * Treat the first parameter as a label. 
	 * All remaining parameters will be considered values to test WebElement attributes against.
	 */
	@Override
	protected List<WebElement> customLocate() {
		List<WebElement> located = new ArrayList<WebElement>();
		if(elementType != null && elementType.getTagname() != null) {
			
			String label = new String(parameters.get(0));
			List<String> attributeValues = new ArrayList<String>();
			if(parameters.size() > 1) {
				attributeValues = parameters.subList(1, parameters.size());
			}
			LabelElementLocator labelLocator = new LabelElementLocator(driver, searchContext);
			labelLocator.setIgnoreHidden(super.ignoreHidden);
			labelLocator.setIgnoreDisabled(super.ignoreDisabled);
			labelLocator.setLabelCanBeHyperlink(this.labelCanBeHyperlink);
			List<Element> labelElements = labelLocator.locateAll(elementType, Arrays.asList(new String[]{label}));
			
			// Assume that more than one label is found and subdivide searches by each.
			boolean oneToOneMatch = false;
			List<List<WebElement>> locatedByLabel1 = new ArrayList<List<WebElement>>();
			for(Element labelElement : labelElements) {
				List<WebElement> flds = tryTraditionalLabelSearchMethod(labelElement.getWebElement(), attributeValues);
				if(flds.isEmpty()) {
					flds = trySearchingOutwardFromLabel(labelElement.getWebElement(), attributeValues);
				}
				if(flds.size() == 1) {
					oneToOneMatch = true;
				}				
				locatedByLabel1.add(flds);
			}
			
			List<WebElement> leastResults = combineSmallestResultBatches(locatedByLabel1);
			
			if(leastResults.size() == 1) {
				// SCENARIO 1: One label-based search returned a single match when the rest returned more. Return this.
				located.addAll(leastResults);
			}
			else if(oneToOneMatch) {
				// SCENARIO 2: Multiple label searches returned a single result. Combine only these and return them.
				for(List<WebElement> flds : locatedByLabel1) {
					if(flds.size() == 1) {
						located.addAll(flds);
					}
				}
			}
			else{
				// SCENARIO 3: No label search produced a single result. Assume these labels and fields exist in a table
				// and try to narrow down the results for each label by their relative proximity within the table that label.
				List<List<WebElement>> locatedByLabel2 = new ArrayList<List<WebElement>>();
				for(int i=0; i<locatedByLabel1.size(); i++) {
					Element labelElement = labelElements.get(i);
					List<WebElement> flds = locatedByLabel1.get(i);
					List<WebElement> tableFlds = tryTabularSearchMethod(labelElement.getWebElement(), flds);
					locatedByLabel2.add(tableFlds);
				}
				leastResults = combineSmallestResultBatches(locatedByLabel2);
				located.addAll(leastResults);
			}
		}
		
		return located;
	}

	/**
	 * The results of any single search attempt are in the form of a list of WebElement instances, or
	 * a "batch". A group of these are provided in another list or "batches". Find the smallest
	 * batch and any of the same size and return their combined content in a new list. 
	 * @param batches
	 * @return
	 */
	private List<WebElement> combineSmallestResultBatches(List<List<WebElement>> batches) {
		List<WebElement> smallest = new ArrayList<WebElement>();
		
		// Sort the results so that those that returned the least results are at the top.
		Collections.sort(batches, new Comparator<List<WebElement>>(){
			@Override public int compare(List<WebElement> o1, List<WebElement> o2) {
				return new Integer(o1.size()).compareTo(new Integer(o2.size()));
			}});
		
		// Add the the result or results of smallest size.
		for (ListIterator<List<WebElement>> iterator = batches.listIterator(); iterator.hasNext();) {
			if(iterator.hasPrevious()) {
				 List<WebElement> previous = batches.get(iterator.previousIndex());
				 List<WebElement> flds = (List<WebElement>) iterator.next();
				 if(flds.size() > previous.size() && !previous.isEmpty()) {
					 break;
				 }
				 smallest.addAll(flds);
			}
			else {
				smallest.addAll((List<WebElement>) iterator.next());				
			}
		}
		
		return smallest;
	}
	
	/**
	 * The label might be a <label> html element with a for attribute, in which case the search for the element
	 * should be easy.
	 * 
	 * @param labelElement
	 * @param attributeValues
	 * @return
	 */
	private List<WebElement> tryTraditionalLabelSearchMethod(WebElement labelElement, List<String> attributeValues) {
		List<WebElement> flds = new ArrayList<WebElement>();
		if("label".equals(labelElement.getTagName())) {
			String id = labelElement.getAttribute("for");
			if(!Utils.isEmpty(id)) {
				List<WebElement> temp = driver.findElements(By.id(id));
				for(WebElement candidate : temp) {
					if(ElementType.getInstance(candidate).equals(elementType)) {
						flds.add(candidate);
					}
				}
			}
		}
		return flds;
	}
	
	/**
	 * Recurse up the DOM from the provided elements parent until the sought element is found, or the root node is reached.
	 * 
	 * @param labelElement
	 * @return
	 */
	private List<WebElement> trySearchingOutwardFromLabel(WebElement labelElement, List<String> attributeValues) {

		List<WebElement> candidates = elementType.findAll(labelElement);

		if(candidates.isEmpty()) {
			// WebElement parent = getParentElement(element);
			WebElement parent = null;
			try {
				parent = labelElement.findElement(By.xpath("./.."));
			} 
			catch (InvalidSelectorException e) {
				// Runtime exception thrown when you are at the top of the DOM and try to select higher.
				// An instance of com.gargoylesoftware.htmlunit.html.HtmlPage is returned, which triggers the exception.
				return candidates;
			}
			if(parent != null) {
				return trySearchingOutwardFromLabel(parent, attributeValues);
			}
			return candidates;
		}
		else {
			if(attributeValues.isEmpty()) {
				return candidates;
			}
			else {
				// If parameters are present beyond the first, they are attribute values, so pick only 
				// those webElements that have every attributeValue accounted for among their attributes.
				AttributeInspector inspector = new AttributeInspector(candidates);
				List<WebElement> filtered = inspector.findForValues(attributeValues);				
				return filtered;
			}
		}
	}

	/**
	 * This function is called if searching up to now has not narrowed results down to one web element.
	 * Assume the reason for this is that the web elements, including the label element were found in a 
	 * table on separate rows. This would make them peers and therefore all included. A Tablular search
	 * method assumes that the webElement closest to the label in terms of rows and columns  would be 
	 * the "winner".
	 * 
	 * @param labelElement
	 * @param located
	 * @return
	 */
	private List<WebElement> tryTabularSearchMethod(WebElement labelElement, List<WebElement> located) {
		TableData tableData = new TableData(driver, labelElement, located);
		WebElement row = tableData.getFirstSharedTableRow();
		if(row != null) {
			List<TableCellData> cells = tableData.getClosestTableCells();
			if(cells.size() <= located.size() && !cells.isEmpty()) {
				List<WebElement> filtered = new ArrayList<WebElement>();
				for(TableCellData cell : cells) {
					filtered.add(cell.getWebChildElement());
				}
				return filtered;
			}
		}
		return located;
	}
	
	/**
	 * Find a web element that is a parent to the specified child element.
	 * This only works if javascript is enabled on the driver.
	 * @param childElement
	 * @return
	 */
	@SuppressWarnings("unused")
	private WebElement getParentElement(WebElement childElement) {
		JavascriptExecutor executor = (JavascriptExecutor)searchContext;
		WebElement parentElement = (WebElement)executor.executeScript("return arguments[0].parentNode;", childElement);
		return parentElement;
	}
	@Override
	protected Element getElement(WebDriver driver, WebElement we) {
		return new BasicElement(driver, we);
	}
	
	public boolean isLabelCanBeHyperlink() {
		return labelCanBeHyperlink;
	}
	
	public void setLabelCanBeHyperlink(boolean labelCanBeHyperlink) {
		this.labelCanBeHyperlink = labelCanBeHyperlink;
	}
}
