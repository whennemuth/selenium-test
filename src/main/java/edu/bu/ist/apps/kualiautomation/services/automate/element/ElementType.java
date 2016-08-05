package edu.bu.ist.apps.kualiautomation.services.automate.element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.util.Utils;

public enum ElementType {

	TEXTBOX(
		"Simple text box",
		"input",
		"text",
		"//input[@type='text'] | //input[not(@type)]",
		true,
		false,
		false),
	PASSWORD(
		"Password text box",
		"input",
		"password",
		"//input[@type='password']",
		true,
		false,
		false),
	TEXTAREA(
		"Area in which multi-line text can be input",
		"textarea",
		null,
		"//textarea",
		true,
		false,
		false),
	BUTTON(
		"Something to click that looks like a button",
		"input",
		"button",
		"//input[@type='button'] | //input[@type='submit'] | //button",
		false,
		true,
		false),
	BUTTONIMAGE(
		"Something to click that looks like an image",
		"input",
		"image",
		"//input[@type='image']",
		false,
		true,
		false),	
	HYPERLINK(
		"Clickable text that changes the cursor when hovered over to indicate a hyperlink.",
		"a",
		null,
		"//a[text()]",
		false,
		true,
		false), 
	HOTSPOT(
		"Clickable graphic that changes the cursor when hovered over to indicate a hotspot for navigation or function trigger.",
		"a",
		null,
		"//a[not(text())] | //a[text() and not(text())]",
		false,
		true,
		false), 
	SELECT(
		"A dropdown box or listbox",
		"select",
		null,
		"//select",
		false,
		false,
		false),
	CHECKBOX(
		"Checkable box",
		"input",
		"checkbox",
		"//input[@type='checkbox']",
		false,
		false,
		true),
	RADIO(
		"Radio Button",
		"input",
		"radio",
		"//input[@type='radio']",
		false,
		false,
		true),
	SHORTCUT(
		"A hyperlink or hotspot that is located by more than one label heading" ,
		null,
		null,
		null,
		false,
		true,
		false
	),
	OTHER(
		"None of the above, but clickable",
		null,
		null,
		"//*",
		false,
		false,
		false);
	
	private String description;
	private String xpathSelector;
	private String tagname;
	private String typeAttribute;
	private boolean acceptsKeystrokes;
	private boolean canNavigate;
	private boolean checkable;
	
	private ElementType(String description, String tagname, String typeAttribute, String xpathSelector, boolean acceptsKeystrokes, boolean canNavigate, boolean checkable) {
		this.description = description;
		this.xpathSelector = xpathSelector;
		this.tagname = tagname;
		this.typeAttribute = typeAttribute;
		this.acceptsKeystrokes = acceptsKeystrokes;
		this.canNavigate = canNavigate;
		this.checkable = checkable;
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

	public boolean canNavigate() {
		return canNavigate;
	}

	public boolean isCheckable() {
		return checkable;
	}

	public List<WebElement> findAll(SearchContext ctx) {
		boolean global = true;
		if(ctx instanceof WebElement)
			global = false;
		List<WebElement> flds = ctx.findElements(By.xpath(getXpath(global)));
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
		String type = we.getAttribute("type");
		if(we != null && !Utils.isEmpty(we.getTagName())) {
			for(ElementType et : ElementType.values()) {
				if(we.getTagName().equalsIgnoreCase(et.getTagname())) {
					if(Utils.isEmpty(type) && Utils.isEmpty(et.getTypeAttribute())) {
						return et;
					}
					if(!Utils.isEmpty(type) && !Utils.isEmpty(et.getTypeAttribute())) {
						if(type.equalsIgnoreCase(et.getTypeAttribute())) {
							return et;
						}
					}
				}
			}
		}
		if("select".equalsIgnoreCase(we.getTagName())) {
			if("select-one".equalsIgnoreCase(type) || "select-multiple".equalsIgnoreCase(type)){
				return SELECT;
			}
		}
		if("textarea".equalsIgnoreCase(we.getTagName())) {
			if("textarea".equalsIgnoreCase(type)) {
				return TEXTAREA;
			}
		}
		if("input".equalsIgnoreCase(we.getTagName())) {
			if("submit".equalsIgnoreCase(type)) {
				return BUTTON;
			}
			else if(Utils.isEmpty(type)) {
				return TEXTBOX;	// The default type of input is text
			}
		}
		if("button".equalsIgnoreCase(we.getTagName())) {
			return BUTTON;
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
