package edu.bu.ist.apps.kualiautomation.services.automate.element;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import edu.bu.ist.apps.kualiautomation.entity.LabelAndValue;

public class ElementValue {

	private WebDriver driver;
	private LabelAndValue lv;
	private Element element;
	private WebDriverWait wait;
	private boolean navigation;
	
	public ElementValue(WebDriver driver, LabelAndValue lv) {
		this.driver = driver;
		this.lv = lv;
		this.wait = new WebDriverWait(driver, 10);
	}

	public boolean applyTo(Element element, boolean navigate) {

		this.element = element;
		
		if(element.getElementType().acceptsKeystrokes()) {
			element.setValue(lv.getValue());
		}
		else {
			element.click();
			if(navigate && element.getElementType().canNavigate()) {
				wait.until(arrivedAtNextPage());	
			}
		}
		
		return true;
	}
	
	public boolean isNavigation() {
		return navigation;
	}

	public void setNavigation(boolean navigation) {
		this.navigation = navigation;
	}

	private Boolean waitingForNextPage = false;
	private ExpectedCondition<Boolean> arrivedAtNextPage() {		
		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {			  
			public Boolean apply(WebDriver drv) {
				if(waitingForNextPage) {
					System.out.println("busy finding next page");
					return false;
				}
				try {
					waitingForNextPage = true;
					element.getWebElement().findElements(By.id("does not matter"));
				} 
				catch (StaleElementReferenceException e) {
					if(!driver.findElements(By.tagName("html")).isEmpty()) {
						return true;
					}
				}
				catch(Exception e) {
					// The apply method will ignore checked exceptions, so wrap them in RuntimeExceptions
					throw new RuntimeException(e);
				}
				finally {
					waitingForNextPage = false;
				}
				return false;
			}
		};
		return condition;
	}

}
