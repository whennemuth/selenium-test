package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * This locator has no custom location logic and only invokes the default logic of the abstract locator class.
 * 
 * @author wrh
 *
 */
public class BasicElementLocator extends AbstractElementLocator {

	public BasicElementLocator(WebDriver driver) {
		super(driver);
	}

	@Override
	protected void customLocate(List<WebElement> located) {
		super.defaultLocate(located);
	}

}
