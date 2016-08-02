package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ElementValue {

	private WebDriver driver;
	private String value;
	private Element element;
	private WebDriverWait wait;
	private boolean navigation;
	
	public ElementValue(WebDriver driver, String value) {
		this.driver = driver;
		this.value = value;
		this.wait = new WebDriverWait(driver, 10);
	}

	public boolean applyTo(Element element, boolean navigate) {

		this.element = element;
		
		switch(element.getElementType()) {
		case TEXTBOX: case TEXTAREA: case PASSWORD:
			element.getWebElement().sendKeys(value);
			break;
		case CHECKBOX: case RADIO:
			if(shouldCheck()) {
				element.click();
			}
			break;
		case BUTTON: case BUTTONIMAGE: case HOTSPOT: case HYPERLINK: case OTHER:
			element.click();
			break;
		case SELECT:
			List<WebElement> options = element.getWebElement().findElements(By.tagName("option"));
			for(WebElement option : options) {
				// RESUME NEXT:
			}
			break;
		case SHORTCUT:
			break;
		default:
			break;
		}
		
		if(navigate && element.getElementType().canNavigate()) {
			wait.until(arrivedAtNextPage());	
		}
		
		return true;
	}
	
	private boolean shouldCheck() {
		if(wantChecked() && !isChecked())
			return true;
		if(!wantChecked() && isChecked())
			return true;
		return false;
	}
	
	private boolean isChecked() {
		return "true".equalsIgnoreCase(element.getWebElement().getAttribute("checked"));
	}

	private boolean wantChecked() {
		return "true".equalsIgnoreCase(value);
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
