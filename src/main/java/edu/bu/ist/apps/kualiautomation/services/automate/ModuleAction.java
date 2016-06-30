package edu.bu.ist.apps.kualiautomation.services.automate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ModuleAction {
	MODULE("Module", "Find and click a link matching the selected module name"),
	CUSTOM("Custom", "Find and click a link labelled with the custom value entered here"),
	NONE("No Action", "No navigation - begin populating fields on this page");
	
	private String label;
	private String description;

	private ModuleAction(String label, String description) {
		this.label = label;
		this.description = description;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static List<Map<String, String>> toJson() {
		List<Map<String, String>> actions = new ArrayList<Map<String, String>>();
		for(ModuleAction action : ModuleAction.values()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", action.name());
			map.put("label", action.getLabel());
			map.put("description", action.getDescription());
			actions.add(map);
		}
		return actions;
	}

}
