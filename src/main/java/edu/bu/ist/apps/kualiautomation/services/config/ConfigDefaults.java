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
		
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> Create Proposal");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> Proposals Enroute");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> All My Proposals");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> Create Proposal For S2S Opportunity");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> Lists >>> Search Proposals");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> Lists >>> View S2S Submissions");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> Lists >>> Search Proposal Log");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> Lists >>> Search Institutional Proposals");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Awards >>> All my Awards");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Negotiations >>> All My Negotiations");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Actions >>> Create IRB Protocol");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Actions >>> Amend or Renew IRB Protocol");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Actions >>> Notify IRB on a Protocol");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Actions >>> Request a Status Change on a IRB Protocol");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> Pending Protocols");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> Protocols Pending PI Action");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> Protocols Pending Committee Action");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> Protocols Under Development");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> All My Protocols");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> Search Protocols");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> All My Reviews");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> All My Schedules");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Financial Entities >>> Financial Entity");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Financial Entities >>> View/Edit Financial Entities");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Financial Entities >>> All My Financial Entities");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Master Disclosure");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Create Annual Disclosure");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Create Manual Disclosure");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> New Project Disclosures To Complete");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Update Master Disclosure");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> All My Disclosures");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> All My Disclosure Reviews");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Search Disclosures");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IACUC Protocols >>> Actions >>> Create IACUC Protocol");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IACUC Protocols >>> Lists >>> All My Protocols");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IACUC Protocols >>> Lists >>> Search Protocols");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IACUC Protocols >>> Lists >>> All My Reviews");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> IACUC Protocols >>> Lists >>> All My Schedules");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Quicklinks >>> Pessimistic Lock");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Quicklinks >>> Grants.gov Opportunity Lookup");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Quicklinks >>> Reporting");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Personnel >>> Current & Pending Support");
		addShortcut(cfg, "HYPERLINK", null, "RESEARCHER >>> Workflow >>> Preferences");

		addShortcut(cfg, "HOTSPOT", "icon-plus", "UNIT >>> Pre-Award >>> Proposal Development");
		addShortcut(cfg, "HOTSPOT", "icon-search", "UNIT >>> Pre-Award >>> Proposal Development");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "UNIT >>> Pre-Award >>> Proposal Log");
		addShortcut(cfg, "HOTSPOT", "icon-search", "UNIT >>> Pre-Award >>> Proposal Log");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "UNIT >>> Pre-Award >>> Institutional Proposal");
		addShortcut(cfg, "HOTSPOT", "icon-search", "UNIT >>> Pre-Award >>> Institutional Proposal");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "UNIT >>> Pre-Award >>> Negotiations");
		addShortcut(cfg, "HOTSPOT", "icon-search", "UNIT >>> Pre-Award >>> Negotiations");		
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Pre-Award >>> All My Negotiations");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "UNIT >>> Post-Award >>> Award");
		addShortcut(cfg, "HOTSPOT", "icon-search", "UNIT >>> Post-Award >>> Award");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Post-Award >>> Award Report Tracking");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "UNIT >>> Post-Award >>> Subaward");
		addShortcut(cfg, "HOTSPOT", "icon-search", "UNIT >>> Post-Award >>> Subaward");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Pre-Submission Compliance >>> Conflict of Interest >>> Disclosure");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Pre-Submission Compliance >>> Conflict of Interest >>> Event Disclosures");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Pre-Submission Compliance >>> Conflict of Interest >>> Non Project Event Disclosures");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Pre-Submission Compliance >>> Conflict of Interest >>> Submitted Disclosures");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Pre-Submission Compliance >>> Conflict of Interest >>> Annual Event Disclosures");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Pre-Submission Compliance >>> Conflict of Interest >>> Undisclosed Events");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "UNIT >>> Pre-Submission Compliance >>> Protocols >>> Animals");
		addShortcut(cfg, "HOTSPOT", "icon-search", "UNIT >>> Pre-Submission Compliance >>> Protocols >>> Animals");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "UNIT >>> Pre-Submission Compliance >>> Protocols >>> Human Participants");
		addShortcut(cfg, "HOTSPOT", "icon-search", "UNIT >>> Pre-Submission Compliance >>> Protocols >>> Human Participants");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "UNIT >>> Post-Submission Compliance >>> IRB Committee");
		addShortcut(cfg, "HOTSPOT", "icon-search", "UNIT >>> Post-Submission Compliance >>> IRB Committee");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "UNIT >>> Post-Submission Compliance >>> IACUC Committee");
		addShortcut(cfg, "HOTSPOT", "icon-search", "UNIT >>> Post-Submission Compliance >>> IACUC Committee");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Post-Submission Compliance >>> Protocol Submissions");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Post-Submission Compliance >>> IACUC Submissions");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Post-Submission Compliance >>> IRB Schedules");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Post-Submission Compliance >>> IACUC Schedules");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Pessimistic Lock");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Grants.gov Opportunity Lookup");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Address Book");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Sponsor Lookup");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Keyword Lookup");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Current & Pending Support");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Perform Person Mass Change");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> ISR/SSR Reporting");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Award Subcontracting Goals and Expenditures");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Subcontracting Expenditures Data Generation");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Workflow >>> People Flow");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Workflow >>> Preferences");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Workflow >>> Routing Report");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Workflow >>> Rules");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Workflow >>> Rule QuickLinks");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Business Rules >>> Agenda");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Business Rules >>> Context");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Business Rules >>> Attribute Definition");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Business Rules >>> Term");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Business Rules >>> Term Specification");
		addShortcut(cfg, "HYPERLINK", null, "UNIT >>> Business Rules >>> Category");

		addShortcut(cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Pre-Award >>> Proposal Development");
		addShortcut(cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Pre-Award >>> Proposal Development");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Pre-Award >>> Proposal Log");
		addShortcut(cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Pre-Award >>> Proposal Log");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Pre-Award >>> Institutional Proposal");
		addShortcut(cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Pre-Award >>> Institutional Proposal");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Pre-Award >>> Negotiations");
		addShortcut(cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Pre-Award >>> Negotiations");		
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Pre-Award >>> All My Negotiations");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Post-Award >>> Award");
		addShortcut(cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Post-Award >>> Award");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Post-Award >>> Award Report Tracking");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Post-Award >>> Subaward");
		addShortcut(cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Post-Award >>> Subaward");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Conflict of Interest >>> Disclosure");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Conflict of Interest >>> Event Disclosures");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Conflict of Interest >>> Non Project Event Disclosures");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Conflict of Interest >>> Submitted Disclosures");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Conflict of Interest >>> Annual Event Disclosures");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Conflict of Interest >>> Undisclosed Events");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Protocols >>> Animals");
		addShortcut(cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Protocols >>> Animals");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Protocols >>> Human Participants");
		addShortcut(cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Protocols >>> Human Participants");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Post-Submission Compliance >>> IRB Committee");
		addShortcut(cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Post-Submission Compliance >>> IRB Committee");
		addShortcut(cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Post-Submission Compliance >>> IACUC Committee");
		addShortcut(cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Post-Submission Compliance >>> IACUC Committee");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Post-Submission Compliance >>> Protocol Submissions");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Post-Submission Compliance >>> IACUC Submissions");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Post-Submission Compliance >>> IRB Schedules");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Post-Submission Compliance >>> IACUC Schedules");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Pessimistic Lock");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Grants.gov Opportunity Lookup");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Address Book");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Sponsor Lookup");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Keyword Lookup");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Current & Pending Support");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Perform Person Mass Change");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> ISR/SSR Reporting");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Award Subcontracting Goals and Expenditures");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Subcontracting Expenditures Data Generation");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Workflow >>> People Flow");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Workflow >>> Preferences");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Workflow >>> Routing Report");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Workflow >>> Rules");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Business Rules >>> Agenda");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Business Rules >>> Context");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Business Rules >>> Attribute Definition");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Business Rules >>> Term");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Business Rules >>> Term Specification");
		addShortcut(cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Business Rules >>> Category");
		 
	}

	private static void addShortcut(Config cfg, String elementType, String identifier, String labelHierarchy) {
		ConfigShortcut shortcut = new ConfigShortcut();
		shortcut.setLabelHierarchy(labelHierarchy);
		shortcut.setElementType(elementType);
		shortcut.setIdentifier(identifier);
		cfg.addConfigShortcut(shortcut);
	}
}