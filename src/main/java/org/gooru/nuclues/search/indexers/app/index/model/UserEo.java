package org.gooru.nuclues.search.indexers.app.index.model;

public class UserEo {
	
	private String firstName;
	
	private String lastName;
	
	private String usernameDisplay;
	
	private String organizationUId;
	
	private String userId;
	
	private String emailId;
	
	private String fullName;
	
	private String profileVisibility;
	

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

	public void setOrganizationUId(String organizationUId) {
		this.organizationUId = organizationUId;
	}

	public String getOrganizationUId() {
		return organizationUId;
	}

	public void setGooruUId(String gooruUId) {
		this.userId = gooruUId;
	}

	public String getGooruUId() {
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

	public String getProfileVisibility() {
		return profileVisibility;
	}

	public void setProfileVisibility(String profileVisibility) {
		this.profileVisibility = profileVisibility;
	}

	
}
