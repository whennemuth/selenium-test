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
				+ "Negotiation")
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
	public static void populate(Config cfg) {
		populate(cfg, ENVIRONMENTS.getValues(), DEFAULT_ENVIRONMENT.getValue(), MODULES.getValues());
	}
	/**
	 * Populate an empty Config instance with the enums values.
	 * @param cfg
	 */
	public static void populate(Config cfg, String[] environments, String defaultEnvironment, String[] modules) {
		
		// Add the default environments
		for(int i=0; i< environments.length; i+=2) {
			ConfigEnvironment env = new ConfigEnvironment();
			env.setName(environments[i]);
			env.setUrl(environments[i+1]);
			cfg.addConfigEnvironment(env);
			if(env.getName().equalsIgnoreCase(defaultEnvironment)) {
				cfg.setCurrentEnvironment(env);
			}
		}
		
		// Add the default modules
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
		
		int sequence = 1;
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> Create Proposal");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> Proposals Enroute");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> All My Proposals");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> Create Proposal For S2S Opportunity");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> Lists >>> Search Proposals");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> Lists >>> View S2S Submissions");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> Lists >>> Search Proposal Log");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Proposals >>> Lists >>> Search Institutional Proposals");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Awards >>> All my Awards");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Negotiations >>> All My Negotiations");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Actions >>> Create IRB Protocol");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Actions >>> Amend or Renew IRB Protocol");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Actions >>> Notify IRB on a Protocol");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Actions >>> Request a Status Change on a IRB Protocol");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> Pending Protocols");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> Protocols Pending PI Action");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> Protocols Pending Committee Action");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> Protocols Under Development");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> All My Protocols");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> Search Protocols");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> All My Reviews");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IRB Protocols >>> Lists >>> All My Schedules");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Financial Entities >>> Financial Entity");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Financial Entities >>> View/Edit Financial Entities");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Financial Entities >>> All My Financial Entities");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Master Disclosure");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Create Annual Disclosure");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Create Manual Disclosure");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> New Project Disclosures To Complete");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Update Master Disclosure");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> All My Disclosures");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> All My Disclosure Reviews");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Conflict of Interest >>> My Disclosures >>> Search Disclosures");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IACUC Protocols >>> Actions >>> Create IACUC Protocol");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IACUC Protocols >>> Lists >>> All My Protocols");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IACUC Protocols >>> Lists >>> Search Protocols");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IACUC Protocols >>> Lists >>> All My Reviews");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> IACUC Protocols >>> Lists >>> All My Schedules");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Quicklinks >>> Pessimistic Lock");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Quicklinks >>> Grants.gov Opportunity Lookup");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Quicklinks >>> Reporting");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Personnel >>> Current & Pending Support");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "RESEARCHER >>> Workflow >>> Preferences");

		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "UNIT >>> Pre-Award >>> Proposal Development");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "UNIT >>> Pre-Award >>> Proposal Development");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "UNIT >>> Pre-Award >>> Proposal Log");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "UNIT >>> Pre-Award >>> Proposal Log");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "UNIT >>> Pre-Award >>> Institutional Proposal");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "UNIT >>> Pre-Award >>> Institutional Proposal");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "UNIT >>> Pre-Award >>> Negotiations");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "UNIT >>> Pre-Award >>> Negotiations");		
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Pre-Award >>> All My Negotiations");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "UNIT >>> Post-Award >>> Award");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "UNIT >>> Post-Award >>> Award");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Post-Award >>> Award Report Tracking");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "UNIT >>> Post-Award >>> Subaward");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "UNIT >>> Post-Award >>> Subaward");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Pre-Submission Compliance >>> Conflict of Interest >>> Disclosure");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Pre-Submission Compliance >>> Conflict of Interest >>> Event Disclosures");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Pre-Submission Compliance >>> Conflict of Interest >>> Non Project Event Disclosures");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Pre-Submission Compliance >>> Conflict of Interest >>> Submitted Disclosures");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Pre-Submission Compliance >>> Conflict of Interest >>> Annual Event Disclosures");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Pre-Submission Compliance >>> Conflict of Interest >>> Undisclosed Events");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "UNIT >>> Pre-Submission Compliance >>> Protocols >>> Animals");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "UNIT >>> Pre-Submission Compliance >>> Protocols >>> Animals");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "UNIT >>> Pre-Submission Compliance >>> Protocols >>> Human Participants");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "UNIT >>> Pre-Submission Compliance >>> Protocols >>> Human Participants");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "UNIT >>> Post-Submission Compliance >>> IRB Committee");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "UNIT >>> Post-Submission Compliance >>> IRB Committee");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "UNIT >>> Post-Submission Compliance >>> IACUC Committee");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "UNIT >>> Post-Submission Compliance >>> IACUC Committee");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Post-Submission Compliance >>> Protocol Submissions");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Post-Submission Compliance >>> IACUC Submissions");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Post-Submission Compliance >>> IRB Schedules");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Post-Submission Compliance >>> IACUC Schedules");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Pessimistic Lock");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Grants.gov Opportunity Lookup");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Address Book");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Sponsor Lookup");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Keyword Lookup");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Current & Pending Support");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Perform Person Mass Change");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> ISR/SSR Reporting");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Award Subcontracting Goals and Expenditures");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Quicklinks >>> Subcontracting Expenditures Data Generation");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Workflow >>> People Flow");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Workflow >>> Preferences");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Workflow >>> Routing Report");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Workflow >>> Rules");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Workflow >>> Rule QuickLinks");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Business Rules >>> Agenda");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Business Rules >>> Context");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Business Rules >>> Attribute Definition");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Business Rules >>> Term");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Business Rules >>> Term Specification");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "UNIT >>> Business Rules >>> Category");

		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Pre-Award >>> Proposal Development");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Pre-Award >>> Proposal Development");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Pre-Award >>> Proposal Log");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Pre-Award >>> Proposal Log");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Pre-Award >>> Institutional Proposal");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Pre-Award >>> Institutional Proposal");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Pre-Award >>> Negotiations");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Pre-Award >>> Negotiations");		
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Pre-Award >>> All My Negotiations");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Post-Award >>> Award");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Post-Award >>> Award");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Post-Award >>> Award Report Tracking");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Post-Award >>> Subaward");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Post-Award >>> Subaward");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Conflict of Interest >>> Disclosure");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Conflict of Interest >>> Event Disclosures");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Conflict of Interest >>> Non Project Event Disclosures");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Conflict of Interest >>> Submitted Disclosures");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Conflict of Interest >>> Annual Event Disclosures");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Conflict of Interest >>> Undisclosed Events");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Protocols >>> Animals");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Protocols >>> Animals");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Protocols >>> Human Participants");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Pre-Submission Compliance >>> Protocols >>> Human Participants");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Post-Submission Compliance >>> IRB Committee");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Post-Submission Compliance >>> IRB Committee");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-plus", "CENTRAL ADMIN >>> Post-Submission Compliance >>> IACUC Committee");
		addShortcut(sequence++, cfg, "HOTSPOT", "icon-search", "CENTRAL ADMIN >>> Post-Submission Compliance >>> IACUC Committee");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Post-Submission Compliance >>> Protocol Submissions");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Post-Submission Compliance >>> IACUC Submissions");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Post-Submission Compliance >>> IRB Schedules");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Post-Submission Compliance >>> IACUC Schedules");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Pessimistic Lock");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Grants.gov Opportunity Lookup");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Address Book");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Sponsor Lookup");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Keyword Lookup");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Current & Pending Support");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Perform Person Mass Change");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> ISR/SSR Reporting");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Award Subcontracting Goals and Expenditures");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Quicklinks >>> Subcontracting Expenditures Data Generation");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Workflow >>> People Flow");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Workflow >>> Preferences");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Workflow >>> Routing Report");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Workflow >>> Rules");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Business Rules >>> Agenda");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Business Rules >>> Context");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Business Rules >>> Attribute Definition");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Business Rules >>> Term");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Business Rules >>> Term Specification");
		addShortcut(sequence++, cfg, "HYPERLINK", null, "CENTRAL ADMIN >>> Business Rules >>> Category");
		 
	}

	private static void addShortcut(int sequence, Config cfg, String elementType, String identifier, String labelHierarchy) {
		ConfigShortcut shortcut = new ConfigShortcut();
		shortcut.setLabelHierarchy(labelHierarchy);
		shortcut.setElementType(elementType);
		shortcut.setIdentifier(identifier);
		shortcut.setSequence(sequence);
		cfg.addConfigShortcut(shortcut);
	}
}