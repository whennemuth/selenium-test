package edu.bu.ist.apps.kualiautomation.services.automate.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.util.Utils;

/**
 * This class computes the closest among a list of choices those web elements that are closest to a specified 
 * label web element with respect to their vertical proximity to that web element as members of a table.
 * Typically, the label web element would be a table column header.
 * <pre style="font: inherit">
 * 
 * 1) Assume the label web element exists in a table. 
 * 2) Provide functionlity to pick from webElements only items that exist somewhere in the same table column. 
 * 3) Provide additional functionality to pick the web element closest to (highest) to the label.
 * </pre>
 * @author wrh
 *
 */
public class TableColumnData {
	
	private WebElement labelElement;
	private List<WebElement> webElements;
	private String javascript;
	private JavascriptExecutor executor;
	
	public TableColumnData(WebDriver driver, WebElement labelElement, List<WebElement> webElements, String javascriptResourceURL) {
		this.labelElement = labelElement;
		this.webElements = webElements;
		this.javascript = Utils.getClassPathResourceContent(javascriptResourceURL);
		this.executor = (JavascriptExecutor) driver;
	}

	@SuppressWarnings("unchecked")
	public List<TableColumnCell> getElementsInSameColumnAsLabel() {
		
		List<Map<String, Object>> datamaps = (List<Map<String, Object>>) executor.executeScript(
				javascript, "column", labelElement, webElements);	
		
		List<TableColumnCell> filtered = TableColumnCell.getInstances(datamaps);
		
		Collections.sort(filtered, new Comparator<TableColumnCell>() {
			@Override
			public int compare(TableColumnCell cell1, TableColumnCell cell2) {
				// Returns a negative integer, zero, or a positive integer as 
				// the first argument is less than, equal to, or greater than the second.
				if(cell1.getLabelRowIndex() < cell2.getLabelRowIndex()) {
					return -1;
				}
				if(cell1.getLabelRowIndex() > cell2.getLabelRowIndex()) {
					return 1;
				}
				if(cell1.getCommonColumnIndex() < cell2.getCommonColumnIndex()) {
					return -1;
				}
				if(cell1.getCommonColumnIndex() > cell2.getCommonColumnIndex()) {
					return 1;
				}
				if(cell1.getRowIndex() == cell2.getRowIndex()) {
					return 0;
				}
				return cell1.getRowIndex() < cell2.getRowIndex() ? -1 : 1;
			}});
		
		return filtered;
	}
	
	/**
	 * @return The web element closest to the label web element in the same table column.
	 */
	public WebElement getFirstElementBelowLabelInSameColumn() {
		return  getElementsInSameColumnAsLabel().get(0).getOriginalField();
	}
	
	/**
	 * This class is a bean to take in meta data about the table cells returned by a javascript query.
	 * 
	 * @author wrh
	 *
	 */
	public static class TableColumnCell {

		private WebElement originalField;
		private Integer rowIndex;
		private Integer commonColumnIndex;
		private WebElement label;
		private Integer labelRowIndex;
		private Integer labelColumnIndex;
		
		public TableColumnCell(Map<String, Object> data) {
			originalField = (WebElement) data.get("originalField");
			commonColumnIndex = getInteger("commonColumnIndex", data);
			rowIndex = getInteger("rowIndex", data);
			label = (WebElement) data.get("label");
			labelColumnIndex = getInteger("labelColumnIndex", data);
			labelRowIndex = getInteger("labelRowIndex", data);
		}
		
		public WebElement getOriginalField() {
			return originalField;
		}
		public Integer getRowIndex() {
			return rowIndex;
		}
		public Integer getCommonColumnIndex() {
			return commonColumnIndex;
		}
		public WebElement getLabel() {
			return label;
		}
		public Integer getLabelRowIndex() {
			return labelRowIndex;
		}
		public Integer getLabelColumnIndex() {
			return labelColumnIndex;
		}
		private Integer getInteger(String fieldname, Map<String, Object> data) {
			Object val = data.get(fieldname);
			if(val == null)
				return null;
			Integer i = null;
			try {
				i = Integer.valueOf(val.toString());
			} 
			catch (NumberFormatException e) {
				return null;
			}
			return i;
		}
		
		public static List<TableColumnCell> getInstances(List<Map<String, Object>> datamaps) {
			List<TableColumnCell> cells = new ArrayList<TableColumnCell>();
			for(Map<String, Object> map : datamaps) {
				cells.add(new TableColumnCell(map));
			}
			return cells;
		}
	};
}
