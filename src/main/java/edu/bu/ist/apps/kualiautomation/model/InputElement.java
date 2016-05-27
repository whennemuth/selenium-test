package edu.bu.ist.apps.kualiautomation.model;

public enum InputElement {
	TEXTBOX("Text Box"), LISTBOX("List Box"), CHECKBOX("Check Box"), RADIOBUTTON("Radio Button");
	private String description;
	private InputElement(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	
}
