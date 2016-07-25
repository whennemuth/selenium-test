package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import edu.bu.ist.apps.kualiautomation.entity.ConfigShortcut;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;

public class ShortcutElementLocator  extends AbstractElementLocator {

	private ConfigShortcut shortcut;
	
	private ShortcutElementLocator() {
		super(null); // Restrict the default constructor
	}
	
	public ShortcutElementLocator(WebDriver driver, ConfigShortcut shortcut) {
		super(driver);
		this.shortcut = shortcut;
	}

	@Override
	protected List<WebElement> customLocate() {
// RESUME NEXT:		
		return null;
	}

}
