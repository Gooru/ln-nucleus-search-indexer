package org.gooru.nucleus.search.indexers.app.index.model;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class TenantEio {
  
  private JsonObject tenant = null;

  public TenantEio() {
    this.tenant = new JsonObject();
  }

  public JsonObject getTenantJson() {
    return this.tenant;
  }

  public String getId() {
    return tenant.getString("id", null);
  }

  public void setId(String id) {
    tenant = JsonUtil.set(tenant, "id", id);
  }
  
  public String getIndexType() {
    return tenant.getString("indexType", null);
  }

  public void setIndexType(String indexType) {
    tenant = JsonUtil.set(tenant, "indexType", indexType);
  }

  public String getIndexUpdatedTime() {
    return tenant.getString("indexUpdatedTime", null);
  }

  public void setIndexUpdatedTime(Date indexUpdatedTime) {
    tenant = JsonUtil.set(tenant, "indexUpdatedTime", indexUpdatedTime.toInstant());
  }

  public String getCreatedAt() {
    return tenant.getString("createdAt", null);
  }

  public void setCreatedAt(String createdAt) {
    tenant = JsonUtil.set(tenant, "createdAt", createdAt);
  }

  public String getUpdatedAt() {
    return tenant.getString("updatedAt", null);
  }

  public void setUpdatedAt(String updatedAt) {
    tenant = JsonUtil.set(tenant, "updatedAt", updatedAt);
  }
  public String getName() {
    return tenant.getString("name", null);
  }

  public void setName(String name) {
    tenant = JsonUtil.set(tenant, "name", name);
  }
  
  public String getTenantType() {
    return tenant.getString("tenantType", null);
  }

  public void setTenantType(String tenantType) {
    tenant = JsonUtil.set(tenant, "tenantType", tenantType);
  }
  
  public String getDescription() {
    return tenant.getString("description", null);
  }

  public void setDescription(String description) {
    tenant = JsonUtil.set(tenant, "description", description);
  }

  public String getContentVisibility() {
    return tenant.getString("contentVisibility", null);
  }

  public void setContentVisibility(String contentVisibility) {
    tenant = JsonUtil.set(tenant, "contentVisibility", contentVisibility);
  }

  public String getClassVisibility() {
    return tenant.getString("classVisibility", null);
  }

  public void setClassVisibility(String classVisibility) {
    tenant = JsonUtil.set(tenant, "classVisibility", classVisibility);
  }

  public String getUserVisibility() {
    return tenant.getString("userVisibility", null);
  }

  public void setUserVisibility(String userVisibility) {
    tenant = JsonUtil.set(tenant, "userVisibility", userVisibility);
  }
  
  public String getFCVisibility() {
    return tenant.getString("fcVisibility", null);
  }

  public void setFCVisibility(String fcVisibility) {
    tenant = JsonUtil.set(tenant, "fcVisibility", fcVisibility);
  }
  
  public String getParentTenantFCVisibility() {
    return tenant.getString("parentTenantFCVisibility", null);
  }

  public void setParentTenantFCVisibility(String parentTenantFCVisibility) {
    tenant = JsonUtil.set(tenant, "parentTenantFCVisibility", parentTenantFCVisibility);
  }
  
  public String setParentTenantId() {
    return tenant.getString("parentTenantId", null);
  }

  public void setParentTenantId(String parentTenantId) {
    tenant = JsonUtil.set(tenant, "parentTenantId", parentTenantId);
  }
  
  public JsonArray getParentTenantIds() {
    return tenant.getJsonArray("parentTenantIds", null);
  }

  public void setParentTenantIds(Set<String> parentTenantIds) {
    tenant = JsonUtil.set(tenant, "parentTenantIds", new JsonArray(parentTenantIds.stream().collect(Collectors.toList())));
  }
  
  public String getRootTenantId() {
    return tenant.getString("rootTenantId", null);
  }

  public void setRootTenantId(String rootTenantId) {
    tenant = JsonUtil.set(tenant, "rootTenantId", rootTenantId);
  }

}
