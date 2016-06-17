package edu.bu.ist.apps.kualiautomation.services.element;

public enum ElementType {

	TEXTBOX("Simple text box"),
	TEXTAREA("Area in which multi-line text can be input"),
	BUTTON("Something to click that looks like a button"),
	HYPERLINK("Clickable text or graphic that changes the cursor when hovered over to indicate a hotspot for navigation or function trigger."), 
	SELECT("A dropdown box or listbox"),
	CHECKBOX("Checkable box"),
	RADIO("Radio Button"),
	OTHER("None of the above, but clickable");
	
	private String description;
	
	private ElementType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
