package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.DefaultTaxonomy;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaxonomyRepositoryImpl implements TaxonomyRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaxonomyRepositoryImpl.class);

  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getDefaultTaxonomyData(String codeId, String level) {
    Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
    String query = null;
    switch(level) {
    case IndexerConstants.SUBJECT :
      query = DefaultTaxonomy.GET_SUBJECT_QUERY;
      break;
    case IndexerConstants.COURSE :
      query = DefaultTaxonomy.GET_COURSE_QUERY;
      break;
    case IndexerConstants.DOMAIN :
      query = DefaultTaxonomy.GET_DOMAIN_QUERY;
      break;
    case IndexerConstants.STANDARD :
      query = DefaultTaxonomy.GET_CODE;
      break;
    case IndexerConstants.LEARNING_TARGET :
      query = DefaultTaxonomy.GET_CODE;
      break;
    }
    List<Map> defaultTaxMetaList = null;
    if (query != null) {
      defaultTaxMetaList = Base.findAll(query, codeId);
      if (defaultTaxMetaList.size() < 1) {
        LOGGER.warn("Default Taxonomy info for {} level for id : {} not present in DB", level, codeId);
      }
    }
    Base.close();
    return defaultTaxMetaList;
  }
}
