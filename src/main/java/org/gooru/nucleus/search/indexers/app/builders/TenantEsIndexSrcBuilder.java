package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.TenantEio;

import io.vertx.core.json.JsonObject;
/**
 * @author Renuka
 */
public class TenantEsIndexSrcBuilder<S extends JsonObject, D extends TenantEio> extends EsIndexSrcBuilder<S, D> {

  @SuppressWarnings("unchecked")
  @Override
  public String buildSource(JsonObject source) throws Exception {
    return buildSource(source, (D) new TenantEio());
  }

  @Override
  public String getName() {
    return IndexType.TENANT.getType();
  }

  @Override
  protected JsonObject build(JsonObject source, D tenantEo) throws Exception {

    String id = source.getString(EntityAttributeConstants.ID);
    tenantEo.setId(id);
    tenantEo.setIndexType(IndexerConstants.TYPE_TENANT);
    tenantEo.setCreatedAt(source.getString(EntityAttributeConstants.CREATED_AT));
    tenantEo.setUpdatedAt(source.getString(EntityAttributeConstants.UPDATED_AT));
    tenantEo.setName(source.getString(EntityAttributeConstants.NAME));
    tenantEo.setDescription(source.getString(EntityAttributeConstants.DESCRIPTION));
    tenantEo.setTenantType(source.getString(EntityAttributeConstants.TENANT_TYPE));
    tenantEo.setIndexUpdatedTime(new Date());
    tenantEo.setContentVisibility(source.getString(EntityAttributeConstants.CONTENT_VISIBILITY));
    tenantEo.setClassVisibility(source.getString(EntityAttributeConstants.CLASS_VISIBILITY));
    tenantEo.setUserVisibility(source.getString(EntityAttributeConstants.USER_VISIBILITY));
    // Set Tenant Setting
    String fcVisibility = getTenantRepo().fetchTenantSetting(id, EntityAttributeConstants.FC_VISIBILITY);
    tenantEo.setFCVisibility(fcVisibility);

    String parentTenantId = source.getString(EntityAttributeConstants.PARENT_TENANT);
    tenantEo.setParentTenantId(source.getString(EntityAttributeConstants.PARENT_TENANT));
    // tenantEo.setRootTenantId(rootTenantId);

    // Set Parent
    if (StringUtils.isNotBlank(parentTenantId)) {
      Set<String> parentTenantList = new HashSet<>();
      parentTenantList.add(parentTenantId);
      findParentTenantIds(parentTenantId, parentTenantList);
      tenantEo.setParentTenantIds(parentTenantList);
      String parentFcVisibility = getTenantRepo().fetchTenantSetting(parentTenantId, EntityAttributeConstants.FC_VISIBILITY);
      tenantEo.setParentTenantFCVisibility(parentFcVisibility);
    }

    return tenantEo.getTenantJson();
  }

  private void findParentTenantIds(String parentTenantId, Set<String> parentTenantList) {
    if (parentTenantId == null) {
      return;
    }
    JsonObject tenantEo = getTenantRepo().findByTenantId(parentTenantId);
    if (tenantEo != null && !tenantEo.isEmpty()) {
      String parentId = tenantEo.getString(EntityAttributeConstants.PARENT_TENANT);
      if (parentId != null) {
        parentTenantList.add(parentId);
        findParentTenantIds(parentId, parentTenantList);
      } else {
        LOGGER.error("parentTenant is null");
      }
    } else {
      LOGGER.error("Parent for tenant id :" + parentTenantId + " doesn't exist");
      return;
    }
  }
}
