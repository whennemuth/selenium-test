package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.List;

import org.openqa.selenium.SearchContext;
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

	public BasicElementLocator(WebDriver driver, SearchContext searchContext) {
		super(driver, searchContext);
	}

	@Override
	protected List<WebElement> customLocate() {
		return super.defaultLocate();
	}

}
