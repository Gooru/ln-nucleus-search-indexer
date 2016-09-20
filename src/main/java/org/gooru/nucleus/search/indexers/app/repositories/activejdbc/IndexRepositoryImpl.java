package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.repositories.entities.Content;
import org.javalite.activejdbc.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexRepositoryImpl extends BaseIndexRepo implements IndexRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexRepositoryImpl.class);

  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getMetadata(String referenceIds) {
    DB db = getDefaultDataSourceDBConnection();
    openConnection(db);
    List<Map> metadataReference = db.findAll(Content.FETCH_METADATA, referenceIds);
    if (metadataReference.size() < 1) {
      LOGGER.warn("Metadata Reference id: {} not present in DB", referenceIds);
    }
    closeDBConn(db);
    return metadataReference;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getLicenseMetadata(int metadataId) {
    DB db = getDefaultDataSourceDBConnection();
    openConnection(db);

    List<Map> metadataReference = db.findAll(Content.FETCH_LICENSE_METADATA, metadataId);
    if (metadataReference.size() < 1) {
      LOGGER.warn("Metadata Reference id: {} not present in DB", metadataId);
    }
    closeDBConn(db);
    return metadataReference;
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getTwentyOneCenturySkill(String referenceIds) {
    DB db = getDefaultDataSourceDBConnection();
    openConnection(db);
    List<Map> metadataReference = db.findAll(Content.FETCH_TWENTY_ONE_CENTURY_SKILL, referenceIds);
    if (metadataReference.size() < 1) {
      LOGGER.warn("Metadata Reference id: {} not present in DB", referenceIds);
    }
    closeDBConn(db);
    return metadataReference;
  }

}
