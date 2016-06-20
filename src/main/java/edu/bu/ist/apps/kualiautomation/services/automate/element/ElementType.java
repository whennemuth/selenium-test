package edu.bu.ist.apps.kualiautomation.services.automate.element;

public enum ElementType {

	TEXTBOX(
		"Simple text box",
		"input",
		"text",
		"//input[@type='text']"),
	TEXTAREA(
		"Area in which multi-line text can be input",
		"textarea",
		null,
		"//textarea"),
	BUTTON(
		"Something to click that looks like a button",
		"input",
		"button",
		"//input[@type='button']"),
	HYPERLINK(
		"Clickable text or graphic that changes the cursor when hovered over to indicate a hotspot for navigation or function trigger.",
		"a",
		null,
		"//a"), 
	SELECT(
		"A dropdown box or listbox",
		"select",
		null,
		"//select"),
	CHECKBOX(
		"Checkable box",
		"input",
		"checkbox",
		"//input[@type='checkbox']"),
	RADIO(
		"Radio Button",
		"input",
		"radio",
		"//input[@type='radio']"),
	OTHER(
		"None of the above, but clickable",
		null,
		null,
		null);
	
	private String description;
	private String xpathSelector;
	private String tagname;
	private String typeAttribute;
	
	private ElementType(String description, String tagname, String typeAttribute, String xpathSelector) {
		this.description = description;
		this.xpathSelector = xpathSelector;
		this.tagname = tagname;
		this.typeAttribute = typeAttribute;
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
	
}
