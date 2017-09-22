package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class UserEo {

  private JsonObject user;

  public UserEo() {
    this.user = new JsonObject();
  }

  public JsonObject getUser() {
    return this.user;
  }

  public String getFirstName() {
    return user.getString("firstName", null);
  }

  public void setFirstName(String firstName) {
    user = JsonUtil.set(user, "firstName", firstName);
  }

  public String getLastName() {
    return user.getString("lastName", null);
  }

  public void setLastName(String lastName) {
    user = JsonUtil.set(user, "lastName", lastName);
  }

  public String getUsername() {
    return user.getString("username", null);
  }

  public void setUsername(String username) {
    user = JsonUtil.set(user, "username", username);
  }
  
  public String getUsernameDisplay() {
    return user.getString("usernameDisplay", null);
  }

  public void setUsernameDisplay(String usernameDisplay) {
    user = JsonUtil.set(user, "usernameDisplay", usernameDisplay);
  }

  public String getUserId() {
    return user.getString("userId", null);
  }

  public void setUserId(String userId) {
    user = JsonUtil.set(user, "userId", userId);
  }

  public String getEmailId() {
    return user.getString("emailId", null);
  }

  public void setEmailId(String emailId) {
    user = JsonUtil.set(user, "emailId", emailId);
  }

  public String getFullName() {
    return user.getString("fullName", null);
  }

  public void setFullName(String fullName) {
    user = JsonUtil.set(user, "fullName", fullName);
  }

  public Boolean getProfileVisibility() {
    return user.getBoolean("profileVisibility", null);
  }

  public void setProfileVisibility(Boolean profileVisibility) {
    user = JsonUtil.set(user, "profileVisibility", profileVisibility);
  }
  
  public String getProfileImage() {
    return user.getString("profileImage", null);
  }

  public void setProfileImage(String profileImg) {
    user = JsonUtil.set(user, "profileImage", profileImg);
  }
  
  public JsonObject getTenant() {
    return user.getJsonObject("tenant", null);
  }

  public void setTenant(JsonObject tenant) {
    user = JsonUtil.set(user, "tenant", tenant);
  }

}
