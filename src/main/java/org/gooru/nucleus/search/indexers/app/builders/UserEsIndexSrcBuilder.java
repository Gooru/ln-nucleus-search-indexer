package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.UserEio;

import io.vertx.core.json.JsonObject;

public class UserEsIndexSrcBuilder<S extends JsonObject, D extends UserEio> extends EsIndexSrcBuilder<S, D> {

  @SuppressWarnings("unchecked")
  @Override
  public String buildSource(JsonObject source) throws Exception {
    return buildSource(source, (D) new UserEio());
  }

  @Override
  public String getName() {
    return IndexType.USER.getType();
  }

  @Override
  protected JsonObject build(JsonObject source, D userEio) throws Exception {
    try {
      userEio.setUserId(source.getString(EntityAttributeConstants.ID));
      userEio.setIndexId(userEio.getUserId());
      userEio.setFirstName(source.getString(EntityAttributeConstants.FIRSTNAME));
      userEio.setLastName(source.getString(EntityAttributeConstants.LASTNAME));
      userEio.setProfileImage(source.getString(EntityAttributeConstants.THUMBNAIL_PATH));
      userEio.setAboutMe(source.getString(EntityAttributeConstants.ABOUT_ME));
      userEio.setCreatedAt(source.getString(EntityAttributeConstants.CREATED_AT));
      userEio.setUpdatedAt(source.getString(EntityAttributeConstants.UPDATED_AT));
      userEio.setEmailId(source.getString(EntityAttributeConstants.EMAIL_ID));
      userEio.setGrade(source.getString(EntityAttributeConstants.GRADE));
      userEio.setIndexType(getName());
      userEio.setIndexUpdatedTime(new Date());
      userEio.setIsDeleted(source.getBoolean(EntityAttributeConstants.IS_DELETED));
      JsonObject school = new JsonObject();
      if(source.getString(EntityAttributeConstants.SCHOOL) != null) school.put(IndexerConstants.NAME, source.getString(EntityAttributeConstants.SCHOOL));
      if(source.getString(EntityAttributeConstants.SCHOOL_ID) != null) school.put(IndexerConstants.ID, source.getString(EntityAttributeConstants.SCHOOL_ID));
      if(source.getString(EntityAttributeConstants.SCHOOL_DISTRICT) != null) school.put(IndexerConstants.DISTRICT, source.getString(EntityAttributeConstants.SCHOOL_DISTRICT));
      if(source.getString(EntityAttributeConstants.SCHOOL_DISTRICT_ID) != null) school.put(IndexerConstants.DISTRICT_ID, source.getString(EntityAttributeConstants.SCHOOL_DISTRICT_ID));
      if (!school.isEmpty())
        userEio.setSchool(school);
      JsonObject stateProvince = new JsonObject();
      if(source.getString(EntityAttributeConstants.STATE_ID) != null) stateProvince.put(IndexerConstants.STATE_ID, source.getString(EntityAttributeConstants.STATE_ID));
      if(source.getString(EntityAttributeConstants.STATE) != null) stateProvince.put(IndexerConstants.STATE, source.getString(EntityAttributeConstants.STATE));
      if(source.getString(EntityAttributeConstants.COUNTRY) != null) stateProvince.put(IndexerConstants.COUNTRY, source.getString(EntityAttributeConstants.COUNTRY));
      if(source.getString(EntityAttributeConstants.COUNTRY_ID) != null) stateProvince.put(IndexerConstants.COUNTRY_ID, source.getString(EntityAttributeConstants.COUNTRY_ID));
      if (!stateProvince.isEmpty())
        userEio.setStateProvince(stateProvince);
      userEio.setUsername(source.getString(EntityAttributeConstants.USERNAME));
      userEio.setDisplayName(source.getString(EntityAttributeConstants.USERNAME)); 
      userEio.setUserCategory(source.getString(EntityAttributeConstants.USER_CATEGORY));
      userEio.setProfileVisibility(source.getBoolean(EntityAttributeConstants.PROFILE_VISIBILITY, true));
      /* userEio.setParentAccountUserName(parentAccountUserName);
       userEio.setChildAccountCount(childAccountCount);
       userEio.setUserPublishedCollectionCount(userPublishedCollectionCount);
       */
    } catch (Exception e) {
      LOGGER.error("UserEISB->build : User re-index failed : exception :", e);
      throw new Exception(e);
    }
    return userEio.getUser();
  }
}
