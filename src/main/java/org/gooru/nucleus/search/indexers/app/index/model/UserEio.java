package org.gooru.nucleus.search.indexers.app.index.model;

import java.util.Date;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonObject;

public class UserEio {

  private JsonObject user;

  public UserEio() {
    this.user = new JsonObject();
  }

  public JsonObject getUser() {
    return this.user;
  }

  public String getUserId() {
    return user.getString("userId", null);
  }

  public void setUserId(String userId) {
    user = JsonUtil.set(user, "userId", userId);
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
  
  public String getDisplayName() {
    return user.getString("displayName", null);
  }

  public void setDisplayName(String displayName) {
    user = JsonUtil.set(user, "displayName", displayName);
  }
  
  public String getEmailId() {
    return user.getString("emailId", null);
  }

  public void setEmailId(String emailId) {
    user = JsonUtil.set(user, "emailId", emailId);
  }

  public String getProfileImage() {
    return user.getString("profileImage", null);
  }

  public void setProfileImage(String profileImg) {
    user = JsonUtil.set(user, "profileImage", profileImg);
  }
  
  public String getGrade() {
    return user.getString("grade", null);
  }

  public void setGrade(String grade) {
    user = JsonUtil.set(user, "grade", grade);
  }
  
  public String getIndexUpdatedTime() {
    return user.getString("indexUpdatedTime", null);
  }

  public void setIndexUpdatedTime(Date indexUpdatedTime) {
    user = JsonUtil.set(user, "indexUpdatedTime", indexUpdatedTime.toInstant());
  }

  public String getCreatedAt() {
    return user.getString("createdAt", null);
  }

  public void setCreatedAt(String createdAt) {
    user = JsonUtil.set(user, "createdAt", createdAt);
  }

  public String getUpdatedAt() {
    return user.getString("updatedAt", null);
  }

  public void setUpdatedAt(String updatedAt) {
    user = JsonUtil.set(user, "updatedAt", updatedAt);
  }
  
  public String getIndexType() {
    return user.getString("indexType", null);
  }

  public void setIndexType(String indexType) {
    user = JsonUtil.set(user, "indexType", indexType);
  }
  
  public Boolean isDeleted() {
    return user.getBoolean("isDeleted", false);
  }

  public void setIsDeleted(Boolean isDeleted) {
    user = JsonUtil.set(user, "isDeleted", isDeleted);
  }
  
  public String getAboutMe() {
    return user.getString("aboutMe", null);
  }

  public void setAboutMe(String aboutMe) {
    user = JsonUtil.set(user, "aboutMe", aboutMe);
  }
  
  public String getParentAccountUserName() {
    return user.getString("parentAccountUserName", null);
  }

  public void setParentAccountUserName(String parentAccountUserName) {
    user = JsonUtil.set(user, "parentAccountUserName", parentAccountUserName);
  }
  
  public Integer getChildAccountCount() {
    return user.getInteger("childAccountCount", 0);
  }

  public void setChildAccountCount(Integer childAccountCount) {
    user = JsonUtil.set(user, "childAccountCount", childAccountCount);
  }
  
  public JsonObject getSchool() {
    return user.getJsonObject("school", null);
  }

  public void setSchool(JsonObject school) {
    user = JsonUtil.set(user, "school", school);
  }
  
  public JsonObject getStateProvince() {
    return user.getJsonObject("stateProvince", null);
  }

  public void setStateProvince(JsonObject stateProvince) {
    user = JsonUtil.set(user, "stateProvince", stateProvince);
  }
  
  public String getUserCategory() {
    return user.getString("userCategory", null);
  }
  
  public void setUserCategory(String userCategory) {
    user = JsonUtil.set(user, "userCategory", userCategory);
  }
  
  public String getIndexId() {
    return user.getString("indexId", null);
  }
  
  public void setIndexId(String indexId) {
    user = JsonUtil.set(user, "indexId", indexId);
  }
  
  public Boolean getProfileVisibility() {
    return user.getBoolean("profileVisibility", false);
  }

  public void setProfileVisibility(Boolean profileVisibility) {
    user = JsonUtil.set(user, "profileVisibility", profileVisibility);
  }
  
  public Long getUserPublishedCollectionCount() {
    return user.getLong("userPublishedCollectionCount", null);
  }

  public void setUserPublishedCollectionCount(Long userPublishedCollectionCount) {
    user = JsonUtil.set(user, "userPublishedCollectionCount", userPublishedCollectionCount);
  }
}
