package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 * This class wraps the {@link org.openqa.selenium.WebElement} in an abstract class of the same type
 * so as to allow subclasses to override methods and to that degree effect the Adapter design pattern.
 * 
 * @author wrh
 *
 */
public class AbstractWebElement implements WebElement {

	private WebElement webElement;
	
	public AbstractWebElement(WebElement webElement) {
		this.webElement = webElement;
	}
	
	@Override public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException { return webElement.getScreenshotAs(target); }
	@Override public void click() { webElement.click(); }
	@Override public void submit() { webElement.submit(); }
	@Override public void sendKeys(CharSequence... keysToSend) { webElement.sendKeys(keysToSend); }
	@Override public void clear() { webElement.clear(); }
	@Override public String getTagName() { return webElement.getTagName(); }
	@Override public String getAttribute(String name) { return webElement.getAttribute(name); }
	@Override public boolean isSelected() { return webElement.isSelected(); }
	@Override public boolean isEnabled() { return webElement.isEnabled(); }
	@Override public String getText() { return webElement.getText(); }
	@Override public List<WebElement> findElements(By by) { return webElement.findElements(by); }
	@Override public WebElement findElement(By by) { return webElement.findElement(by); }
	@Override public boolean isDisplayed() { return webElement.isDisplayed(); }
	@Override public Point getLocation() { return webElement.getLocation(); }
	@Override public Dimension getSize() { return webElement.getSize(); }
	@Override public Rectangle getRect() { return webElement.getRect(); }
	@Override public String getCssValue(String propertyName) { return webElement.getCssValue(propertyName); }

}
