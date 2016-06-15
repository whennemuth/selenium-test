package edu.bu.ist.apps.kualiautomation.model;

import java.util.Date;

public enum ConfigDefaults {
	CONFIG_FILE_NAME("kualiautomation.cfg"), 
	DEFAULT_ENVIRONMENT("test"),
	ENVIRONMENTS(
		String.join("&&",
				"test",
				"https://kuali-test.bu.edu/kc/portal.do",
				"staging",
				"https://kuali-stg.bu.edu/kc/portal.do")
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
		String[] vals = ENVIRONMENTS.getValues();
		for(int i=0; i< vals.length; i+=2) {
			Environment env = new Environment();
			env.setName(vals[i]);
			env.setURL(vals[i+1]);
			cfg.addEnvironment(env);
			if(env.getName().equalsIgnoreCase(DEFAULT_ENVIRONMENT.getValue())) {
				cfg.setCurrentEnvironment(env);
			}
		}
		cfg.setLastUpdated(new Date(System.currentTimeMillis()).toString());
	}
}