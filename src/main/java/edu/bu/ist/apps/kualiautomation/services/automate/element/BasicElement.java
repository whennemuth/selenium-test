package edu.bu.ist.apps.kualiautomation.services.automate.element;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.util.Utils;

public class BasicElement implements Element {

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
		ElementValue ev = new ElementValue(driver, value);
		ev.applyTo(this, false);
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
