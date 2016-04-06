package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Content;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class IndexRepositoryImpl implements IndexRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexRepositoryImpl.class);

  @Override
  public List<Map> getMetadata(String referenceIds) {
    Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
    List<Map> metadataReference = Base.findAll(Content.FETCH_METADATA, referenceIds);
    if (metadataReference.size() < 1) {
      LOGGER.warn("Metadata Reference id: {} not present in DB", referenceIds);
    }
    Base.close();
    return metadataReference;
  }

}
