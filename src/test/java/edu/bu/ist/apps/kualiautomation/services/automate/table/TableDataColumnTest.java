package edu.bu.ist.apps.kualiautomation.services.automate.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.AbstractJettyBasedTest;
import edu.bu.ist.apps.kualiautomation.services.automate.table.TableColumnData.TableColumnCell;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TableDataColumnTest extends AbstractJettyBasedTest {

	@Override
	public void setupBefore() {
	}

	@Override
	public void loadHandlers(Map<String, String> handlers) {
		handlers.put("table-test-page3", "TableTestPage3.htm");
	}
	
	@Test
	public void test01() {
		driver.get("http://localhost:8080/table-test-page3");
		
		WebElement label = driver.findElement(By.id("div3"));
		List<WebElement> elements = Arrays.asList((new WebElement[]{
				driver.findElement(By.id("txt15")),
				driver.findElement(By.id("txt17")),
				driver.findElement(By.id("txt16")),
				driver.findElement(By.id("txt13")),
		}));
		
		TableColumnData data = new TableColumnData(driver, label, elements, "TableCellAncestry.js");
		List<TableColumnCell> cells = data.getElementsInSameColumnAsLabel();
		assertOrderOfResults(new String[]{"txt13", "txt16", "txt17", "txt15"}, cells);		
	}
	
	@Test
	public void test02() {
		driver.get("http://localhost:8080/table-test-page3");
		
		WebElement label = driver.findElement(By.id("div9"));
		List<WebElement> elements = Arrays.asList((new WebElement[]{
				driver.findElement(By.id("txt2")),
				driver.findElement(By.id("txt8")),
				driver.findElement(By.id("txt10")),
				driver.findElement(By.id("txt12")),
		}));
		
		TableColumnData data = new TableColumnData(driver, label, elements, "TableCellAncestry.js");
		List<TableColumnCell> cells = data.getElementsInSameColumnAsLabel();
		assertOrderOfResults(new String[]{"txt12"}, cells);
	}
	
	@Test
	public void test03() {
		driver.get("http://localhost:8080/table-test-page3");
		
		WebElement label = driver.findElement(By.id("div1"));
		List<WebElement> elements = Arrays.asList((new WebElement[]{
				driver.findElement(By.id("txt2")),
				driver.findElement(By.id("txt7")),
				driver.findElement(By.id("txt12")),
		}));
		
		TableColumnData data = new TableColumnData(driver, label, elements, "TableCellAncestry.js");
		List<TableColumnCell> cells = data.getElementsInSameColumnAsLabel();
		assertTrue(cells.isEmpty());
	}
	
	@Test
	public void test04() {
		driver.get("http://localhost:8080/table-test-page3");
		
		WebElement label = driver.findElement(By.id("div8"));
		List<WebElement> elements = Arrays.asList((new WebElement[]{
				driver.findElement(By.id("txt7")),
				driver.findElement(By.id("txt12")),
				driver.findElement(By.id("txt9")),
				driver.findElement(By.id("txt11")),
				driver.findElement(By.id("txt8")),
		}));
		
		TableColumnData data = new TableColumnData(driver, label, elements, "TableCellAncestry.js");
		List<TableColumnCell> cells = data.getElementsInSameColumnAsLabel();
		assertOrderOfResults(new String[]{"txt8", "txt11"}, cells);
	}
	
	
	private void assertOrderOfResults(String[] ids, List<TableColumnCell> cells) {
		assertEquals(ids.length, cells.size());
		for(int i=0; i<ids.length; i++) {
			assertEquals(ids[i], cells.get(i).getOriginalField().getAttribute("id"));
		}
	}	
}
