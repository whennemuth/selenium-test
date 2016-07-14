package edu.bu.ist.apps.kualiautomation.services.config;

import edu.bu.ist.apps.kualiautomation.entity.Config;
import edu.bu.ist.apps.kualiautomation.entity.ConfigEnvironment;
import edu.bu.ist.apps.kualiautomation.entity.ConfigModule;
import edu.bu.ist.apps.kualiautomation.entity.ConfigShortcut;
import edu.bu.ist.apps.kualiautomation.entity.ConfigTab;

public enum ConfigDefaults {
	CONFIG_FILE_NAME("kualiautomation.cfg"), 
	DEFAULT_ENVIRONMENT("dev"),
	ENVIRONMENTS(
		String.join("&&",
				"TEST",
				"https://kuali-test.bu.edu/kc/portal.do",
				"STAGING",
				"https://kuali-stg.bu.edu/kc/portal.do",
				"DEV",
				"http://ist-kuali-sb1:8080/kc-dev")
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
				+ "Negotiation")),
			SHORTCUTS(String.join("&&",
				"TEST",
				"https://kuali-test.bu.edu/kc/portal.do",
				"STAGING",
				"https://kuali-stg.bu.edu/kc/portal.do",
				"DEV",
				"http://ist-kuali-sb1:8080/kc-dev")
				
	);

	private String value;
	private ConfigDefaults(String value) {
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
		
		cfg.addConfigShortcut(configShortcut)
		ConfigShortcut shortcut = new ConfigShortcut();
		shortcut.setName("RESEARCHER");
		shortcut.setLabelHierarchy("RESEARCHER >>> Proposals >>> Create Proposal");
		shortcut.setLabelHierarchy("RESEARCHER >>> Proposals >>> Proposals Enroute");
		shortcut.setLabelHierarchy("RESEARCHER >>> Proposals >>> All My Proposals");
		shortcut.setLabelHierarchy("RESEARCHER >>> Proposals >>> Create Proposal For S2S Opportunity");
		shortcut.setLabelHierarchy("RESEARCHER >>> Proposals >>> Lists >>> Search Proposals");
		shortcut.setLabelHierarchy("RESEARCHER >>> Proposals >>> Lists >>> View S2S Submissions");
		shortcut.setLabelHierarchy("RESEARCHER >>> Proposals >>> Lists >>> Search Proposal Log");
		shortcut.setLabelHierarchy("RESEARCHER >>> Proposals >>> Lists >>> Search Institutional Proposals");
		shortcut.setLabelHierarchy("RESEARCHER >>> Awards >>> All my Awards");
		shortcut.setLabelHierarchy("RESEARCHER >>> Negotiations >>> All My Negotiations");
		shortcut.setLabelHierarchy("RESEARCHER >>> IRB Protocols >>> Actions >>> Create IRB Protocol");
		shortcut.setLabelHierarchy("RESEARCHER >>> IRB Protocols >>> Actions >>> Amend or Renew IRB Protocol");
		shortcut.setLabelHierarchy("RESEARCHER >>> IRB Protocols >>> Actions >>> Notify IRB on a Protocol");
		shortcut.setLabelHierarchy("RESEARCHER >>> IRB Protocols >>> Actions >>> Request a Status Change on a IRB Protocol");
		shortcut.setLabelHierarchy("RESEARCHER >>> IRB Protocols >>> Lists >>> Pending Protocols");
		shortcut.setLabelHierarchy("RESEARCHER >>> IRB Protocols >>> Lists >>> Protocols Pending PI Action");
		shortcut.setLabelHierarchy("RESEARCHER >>> IRB Protocols >>> Lists >>> Protocols Pending Committee Action");
		shortcut.setLabelHierarchy("RESEARCHER >>> IRB Protocols >>> Lists >>> Protocols Under Development");
		shortcut.setLabelHierarchy("RESEARCHER >>> IRB Protocols >>> Lists >>> All My Protocols");
		shortcut.setLabelHierarchy("RESEARCHER >>> IRB Protocols >>> Lists >>> Search Protocols");
		shortcut.setLabelHierarchy("RESEARCHER >>> IRB Protocols >>> Lists >>> All My Reviews");
		shortcut.setLabelHierarchy("RESEARCHER >>> IRB Protocols >>> Lists >>> All My Schedules");
		shortcut.setLabelHierarchy("RESEARCHER >>> Conflict of Interest >>> My Financial Entities >>> Financial Entity");
		shortcut.setLabelHierarchy("RESEARCHER >>> Conflict of Interest >>> My Financial Entities >>> View/Edit Financial Entities");
		shortcut.setLabelHierarchy("RESEARCHER >>> Conflict of Interest >>> My Financial Entities >>> All My Financial Entities");
		shortcut.setLabelHierarchy("RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Master Disclosure");
		shortcut.setLabelHierarchy("RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Create Annual Disclosure");
		shortcut.setLabelHierarchy("RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Create Manual Disclosure");
		shortcut.setLabelHierarchy("RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> New Project Disclosures To Complete");
		shortcut.setLabelHierarchy("RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Update Master Disclosure");
		shortcut.setLabelHierarchy("RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> All My Disclosures");
		shortcut.setLabelHierarchy("RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> All My Disclosure Reviews");
		shortcut.setLabelHierarchy("RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Search Disclosures");
		shortcut.setLabelHierarchy("RESEARCHER >>> IACUC Protocols >>> Actions >>> Create IACUC Protocol");
		shortcut.setLabelHierarchy("RESEARCHER >>> IACUC Protocols >>> Lists >>> All My Protocols");
		shortcut.setLabelHierarchy("RESEARCHER >>> IACUC Protocols >>> Lists >>> Search Protocols");
		shortcut.setLabelHierarchy("RESEARCHER >>> IACUC Protocols >>> Lists >>> All My Reviews");
		shortcut.setLabelHierarchy("RESEARCHER >>> IACUC Protocols >>> Lists >>> All My Schedules");
		shortcut.setLabelHierarchy("RESEARCHER >>> Quicklinks >>> Pessimistic Lock");
		shortcut.setLabelHierarchy("RESEARCHER >>> Quicklinks >>> Grants.gov Opportunity Lookup");
		shortcut.setLabelHierarchy("RESEARCHER >>> Quicklinks >>> Reporting");
		shortcut.setLabelHierarchy("RESEARCHER >>> Personnel >>> Current & Pending Support");
		shortcut.setLabelHierarchy("RESEARCHER >>> Workflow >>> Preferences");
	}

	private void addShortcut(Config cfg, String name, String labelHierarchy) {
		ConfigShortcut shortcut = new ConfigShortcut();
		shortcut.setName(name);
		shortcut.setLabelHierarchy(labelHierarchy);
	}
}