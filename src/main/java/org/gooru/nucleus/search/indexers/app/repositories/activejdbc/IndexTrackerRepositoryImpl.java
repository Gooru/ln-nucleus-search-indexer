package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.UUID;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.CollectionIndexDelete;
import org.gooru.nucleus.search.indexers.app.repositories.entities.ResourceIndexDelete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class IndexTrackerRepositoryImpl implements IndexTrackerRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexTrackerRepositoryImpl.class);

  @Override
  public void saveDeletedResource(String id, JsonObject data, List<String> attributes) {
    try {
      ResourceIndexDelete.createIt(EntityAttributeConstants.GOORU_OID, UUID.fromString(data.getString(EntityAttributeConstants.GOORU_OID)), EntityAttributeConstants.INDEX_TYPE, data.getString(EntityAttributeConstants.INDEX_TYPE));
      LOGGER.info("Successfully tracked deleted resource : {}", data.getString(EntityAttributeConstants.GOORU_OID));
    } catch (Exception e) {
      LOGGER.error("Save Failed for ID : {} : Error : {}", data.getString(EntityAttributeConstants.GOORU_OID),  e.getMessage());
    }
  }

  @Override
  public void saveDeletedCollection(String id, JsonObject data, List<String> insertCollectionAllowedFields) {
    try {
      CollectionIndexDelete.createIt(EntityAttributeConstants.GOORU_OID, UUID.fromString(data.getString(EntityAttributeConstants.GOORU_OID)), EntityAttributeConstants.INDEX_TYPE, data.getString(EntityAttributeConstants.INDEX_TYPE));
      LOGGER.info("Successfully tracked deleted collection : {}", data.getString(EntityAttributeConstants.GOORU_OID));
    } catch (Exception e) {
      LOGGER.error("Save Failed for ID : {} : Error : {}", data.getString(EntityAttributeConstants.GOORU_OID),  e.getMessage());
    }
  }
}
