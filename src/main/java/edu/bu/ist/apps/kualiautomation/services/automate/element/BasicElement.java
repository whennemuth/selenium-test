package edu.bu.ist.apps.kualiautomation.services.automate.element;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;

public class BasicElement implements Element {
	
	public static final String CHECKED_REGEX = "(?!)(true)|(yes)|(on)|(checked)";
	public static final String UNCHECKED_REGEX = "(?!)(false)|(no)|(off)|(unchecked)";
	
	private WebDriver driver;
	private WebElement webElement;
	private WebElement label;
	private ElementType elementType;
	
	public BasicElement(WebDriver driver, WebElement webElement) {
		this.driver = driver;
		setWebElement(webElement);
	}
	
	public BasicElement(WebDriver driver, WebElement webElement, WebElement label) {
		this.driver = driver;
		setWebElement(webElement);
		this.label = label;
	}

	@Override
	public WebElement getWebElement() {
		return webElement;
	}
	
	private void setWebElement(WebElement webElement) {
		this.webElement = webElement;
		this.elementType = ElementType.getInstance(webElement);
	}

	@Override
	public ElementType getElementType() {
		return elementType;
	}

	@Override
	public WebElement getLabel() {
		return label;
	}

	@Override
	public boolean isInteractive() {
		return webElement != null && webElement.isDisplayed() && webElement.isEnabled();
	}

	@Override
	public void click() {
		webElement.click();
	}

	@Override
	public void setValue(String value) {
		if(webElement == null)
			return;
		String tagname = webElement.getTagName();
		if(tagname == null)
			return;
		switch(tagname.toLowerCase()) {
			case "input":
				String type = webElement.getAttribute("type");
				if(type == null)
					return;
				
				switch(type.toLowerCase()) {
					case "text":
						webElement.sendKeys(value);
						break;
					case "checkbox": case "radio":
						String val = value.trim();
						String checked = webElement.getAttribute("checked");
						if(checked != null) {
							if(val.matches(CHECKED_REGEX) && checked.matches(UNCHECKED_REGEX)) {
								webElement.click();
							}
							else if(val.matches(UNCHECKED_REGEX) && checked.matches(CHECKED_REGEX)) {
								webElement.click();
							}
						}
						break;
				}
				break;
			case "select":
				break;
			case "textarea":
				break;
		}
	}

}
