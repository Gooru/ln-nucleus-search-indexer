package org.gooru.nuclues.search.indexers.app.index.model;

public class UserEo {
	
	private String firstName;
	
	private String lastName;
	
	private String usernameDisplay;
		
	private String userId;
	
	private String emailId;
	
	private String fullName;
	
	private Boolean profileVisibility;
	

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setUsernameDisplay(String usernameDisplay) {
		this.usernameDisplay = usernameDisplay;
	}

	public String getUsernameDisplay() {
		return usernameDisplay;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getEmailId() {
		return emailId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Boolean getProfileVisibility() {
		return profileVisibility;
	}

	public void setProfileVisibility(Boolean profileVisibility) {
		this.profileVisibility = profileVisibility;
	}

	
}
