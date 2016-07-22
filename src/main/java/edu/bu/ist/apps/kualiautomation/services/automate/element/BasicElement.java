package edu.bu.ist.apps.kualiautomation.services.automate.element;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.util.Utils;

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
		switch(elementType) {
		case TEXTAREA: case TEXTBOX: case PASSWORD:
			webElement.sendKeys(value);
			break;
		case CHECKBOX: case RADIO:
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
		case SELECT:
			break;
		case OTHER:
			break;
		case BUTTON:
			break;
		case BUTTONIMAGE:
			break;
		case HOTSPOT:
			break;
		case HYPERLINK:
			break;
		case SHORTCUT:
			break;
		default:
			break;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("webElement=[");
		if(webElement == null) {
			builder.append("null");
		}
		else {
			builder.append(webElement.getTagName());
			if(!Utils.isEmpty(webElement.getText())) {
				builder.append(" (text='").append(webElement.getText()).append("', ")
				.append("enabled=").append(Boolean.valueOf(webElement.isEnabled())).append(", ")
				.append("disabled=").append(Boolean.valueOf(webElement.isDisplayed()));
				for (int i = 0; i < Attribute.DEFAULT_ATTRIBUTES_TO_CHECK.length; i++) {
					if(i == 0) {
						builder.append(", ");
					}
					String attribute = Attribute.DEFAULT_ATTRIBUTES_TO_CHECK[i];
					builder.append(attribute).append("='").append(String.valueOf(webElement.getAttribute(attribute)));
					builder.append("'");
					if((i+1) == Attribute.DEFAULT_ATTRIBUTES_TO_CHECK.length)
						break;
					builder.append(", ");
				}
				builder.append(")");
			}
		}
		builder.append(", label=");
		builder.append(label);
		builder.append(", elementType=");
		builder.append(elementType.name());
		builder.append("]");
		return builder.toString();
	}

}
