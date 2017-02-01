package edu.bu.ist.apps.kualiautomation.services.automate;

public class KerberosLoginParms {
	
	private String username;
	private String password;
	private String usernameLabel;
	private String passwordLabel;
	private String usernameOtherIdentifier;
	private String passwordOtherIdentifier;
	private String submitButtonLabel;
	private Integer configEnvironmentId = -1;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsernameLabel() {
		return usernameLabel == null ? "username" : usernameLabel;
	}
	public void setUsernameLabel(String usernameLabel) {
		this.usernameLabel = usernameLabel;
	}
	public String getPasswordLabel() {
		return passwordLabel == null ? "password" : passwordLabel;
	}
	public void setPasswordLabel(String passwordLabel) {
		this.passwordLabel = passwordLabel;
	}
	public String getUsernameOtherIdentifier() {
		if(usernameLabel == null)
			return usernameOtherIdentifier == null ? "j_username" : usernameOtherIdentifier;
		return usernameOtherIdentifier;
	}
	public void setUsernameOtherIdentifier(String usernameOtherIdentifier) {
		this.usernameOtherIdentifier = usernameOtherIdentifier;
	}
	public String getPasswordOtherIdentifier() {
		if(passwordLabel == null)
			return passwordOtherIdentifier == null ? "j_password" : passwordOtherIdentifier;
		return passwordOtherIdentifier;
	}
	public void setPasswordOtherIdentifier(String passwordOtherIdentifier) {
		this.passwordOtherIdentifier = passwordOtherIdentifier;
	}
	public String getSubmitButtonLabel() {
		return submitButtonLabel == null ? "continue" : submitButtonLabel;
	}
	public void setSubmitButtonLabel(String submitButtonLabel) {
		this.submitButtonLabel = submitButtonLabel;
	}
	public Integer getConfigEnvironmentId() {
		return configEnvironmentId;
	}
	public void setConfigEnvironmentId(Integer configEnvironmentId) {
		this.configEnvironmentId = configEnvironmentId;
	}
	
}
