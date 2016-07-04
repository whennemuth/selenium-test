package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public enum ElementType {

	TEXTBOX(
		"Simple text box",
		"input",
		"text",
		"//input[@type='text']",
		true),
	TEXTAREA(
		"Area in which multi-line text can be input",
		"textarea",
		null,
		"//textarea",
		true),
	BUTTON(
		"Something to click that looks like a button",
		"input",
		"button",
		"//input[@type='button'] | //input[@type='submit'] | //button",
		false),
	BUTTONIMAGE(
		"Something to click that looks like an image",
		"input",
		"image",
		"//input[@type='image']",
		false),
	HYPERLINK(
		"Clickable text or graphic that changes the cursor when hovered over to indicate a hotspot for navigation or function trigger.",
		"a",
		null,
		"//a",
		false), 
	SELECT(
		"A dropdown box or listbox",
		"select",
		null,
		"//select",
		false),
	CHECKBOX(
		"Checkable box",
		"input",
		"checkbox",
		"//input[@type='checkbox']",
		false),
	RADIO(
		"Radio Button",
		"input",
		"radio",
		"//input[@type='radio']",
		false),
	OTHER(
		"None of the above, but clickable",
		null,
		null,
		"//*",
		false);
	
	private String description;
	private String xpathSelector;
	private String tagname;
	private String typeAttribute;
	private boolean acceptsKeystrokes;
	
	private ElementType(String description, String tagname, String typeAttribute, String xpathSelector, boolean acceptsKeystrokes) {
		this.description = description;
		this.xpathSelector = xpathSelector;
		this.tagname = tagname;
		this.typeAttribute = typeAttribute;
		this.acceptsKeystrokes = acceptsKeystrokes;
	}

	public String getDescription() {
		return description;
	}

	public String getXpathSelector() {
		return xpathSelector;
	}

	public String getTagname() {
		return tagname;
	}

	public String getTypeAttribute() {
		return typeAttribute;
	}
	
	public boolean acceptsKeystrokes() {
		return acceptsKeystrokes;
	}

	public List<WebElement> findAll(WebDriver driver) {
		List<WebElement> flds = driver.findElements(By.xpath(getXpath(true)));
		return flds;
	}
	
	public List<WebElement> findFrom(WebElement element) {
		List<WebElement> flds = element.findElements(By.xpath(getXpath(false)));
		return flds;
	}
	
	private String getXpath(boolean global) {
		String xpath = global ? "//" : ".//";
		if(xpathSelector != null) {
			xpath = xpathSelector.replaceAll("//", xpath);
		}
		return xpath;
	}
	
	public static ElementType getInstance(WebElement we) {
		if(we != null && we.getTagName() != null) {
			for(ElementType et : ElementType.values()) {
				if(we.getTagName().equalsIgnoreCase(et.getTagname())) {
					String type = we.getAttribute("type");
					if(type == null && et.getTypeAttribute() == null) {
						return et;
					}
					if(type != null && et.getTypeAttribute() != null) {
						if(type.equalsIgnoreCase(et.getTypeAttribute())) {
							return et;
						}
					}
				}
			}
		}
		return OTHER;
	}

	public static Map<String, String> toJson() {
		Map<String, String> map = new HashMap<String, String>();
		for(ElementType typ : ElementType.values()) {
			map.put(typ.name(), typ.getDescription());
		}
		return map;
	}
}
