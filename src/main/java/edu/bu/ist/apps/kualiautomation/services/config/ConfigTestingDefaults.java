package edu.bu.ist.apps.kualiautomation.services.config;

import edu.bu.ist.apps.kualiautomation.entity.Config;
import edu.bu.ist.apps.kualiautomation.entity.ConfigEnvironment;
import edu.bu.ist.apps.kualiautomation.entity.ConfigModule;
import edu.bu.ist.apps.kualiautomation.entity.ConfigTab;

public enum ConfigTestingDefaults {
	CONFIG_FILE_NAME("kualiautomation.cfg"), 
	DEFAULT_ENVIRONMENT("dev"),
	ENVIRONMENTS(
		String.join("&&",
				"TEST",
				"https://kuali-test.bu.edu/kc/portal.do",
				"STAGING",
				"https://kuali-stg.bu.edu/kc/portal.do",
				"DEV",
				"http://ist-kuali-sb1:8080/kc-dev",
				"LOGIN",
				"file:///C:/Users/wrh/Desktop/welcome/login.htm",
				"WELCOME",
				"file:///C:/Users/wrh/Desktop/welcome/welcome.htm")
	),
	MODULES(String.join("&&",
			"Proposal Log"
			+ ": ",
			"Institutional Proposal"
				+ ":Award"
				+ ":Contacts"
				+ ":Commitments"
				+ ":Budget Versions"
				+ ":Payment, Reports & Terms"
				+ ":Special Review"
				+ ":Custom Data"
				+ ":Comments, Notes, & Attachments"
				+ ":Award Actions"
				+ ":History"
				+ ":Medusa",
			"Proposal Development Document"
				+ ":Proposal"
				+ ":S2S"
				+ ":Key Personnel"
				+ ":Special Review"
				+ ":Custom Data"
				+ ":Abstracts and Attachments"
				+ ":Questions"
				+ ":Budget Versions"
				+ ":Permissions"
				+ ":Proposal Summary"
				+ ":Proposal Actions"
				+ ":Medusa",
			"Award"
				+ ":Award"
				+ ":Contacts"
				+ ":Commitments"
				+ ":Budget Versions"
				+ ":Payment, Reports & Terms"
				+ ":Special Review"
				+ ":Custom Data"
				+ ":Comments, Notes, & Attachments"
				+ ":Award Actions"
				+ ":History"
				+ ":Medusa",
			"Negotiations:"
				+ "Negotiation")
	);

	private String value;
	private ConfigTestingDefaults(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public String[] getValues() {
		return value.split("&&");
	}
	/**
	 * Populate an empty Config instance with the enums values.
	 * @param cfg
	 */
	public static void populate(Config cfg) {
		
		// Add the default environments
		String[] envs = ENVIRONMENTS.getValues();
		for(int i=0; i< envs.length; i+=2) {
			ConfigEnvironment env = new ConfigEnvironment();
			env.setName(envs[i]);
			env.setUrl(envs[i+1]);
			cfg.addConfigEnvironment(env);
			cfg.addConfigEnvironment(env);
			if(env.getName().equalsIgnoreCase(DEFAULT_ENVIRONMENT.getValue())) {
				cfg.setCurrentEnvironment(env);
			}
		}
		
		// Add the default modules
		String[] modules = MODULES.getValues();
		for(int i=0; i<modules.length; i++) {
			String[] parts = modules[i].split(":");
			String label = parts[0];
			ConfigModule m = new ConfigModule();
			m.setLabel(label);
			if(parts.length > 1) {
				for(int x=1; x<parts.length; x++) {
					ConfigTab tab = new ConfigTab();
					tab.setLabel(parts[x].trim());
					m.addConfigTab(tab);
				}
			}
			cfg.addConfigModule(m);
		}
	}
}