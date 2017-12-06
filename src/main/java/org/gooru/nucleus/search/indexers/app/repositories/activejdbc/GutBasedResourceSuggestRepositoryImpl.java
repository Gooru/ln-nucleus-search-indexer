package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.Arrays;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.GutBasedResourceSuggest;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class GutBasedResourceSuggestRepositoryImpl extends BaseIndexRepo implements GutBasedResourceSuggestRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(GutBasedResourceSuggestRepositoryImpl.class);

  @Override
  public Boolean hasSuggestion(String codeId) {
    DB db = getDefaultDataSourceDBConnection();
    Boolean returnValue = false;
    try {
      openConnection(db);
      LazyList<GutBasedResourceSuggest> result =
              GutBasedResourceSuggest.where(GutBasedResourceSuggest.FETCH_SUGGESTION_BY_C_OR_MC, codeId, codeId);

      if (result != null && result.size() > 0) {
        returnValue = true;
      }
    } catch (Exception ex) {
      LOGGER.error("GBCSRI:hasSuggestion: Failed to fetch resource suggestions ", ex);
    } finally {
      closeDBConn(db);
    }
    return returnValue;
  }

  @Override
  public void saveSuggestions(String id, JsonObject data) {
   DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);
      db.openTransaction();
      db.exec(GutBasedResourceSuggest.INSERT_QUERY, data.getString(EntityAttributeConstants.COMPETENCY_INTERNAL_CODE), data.getString(EntityAttributeConstants.MICRO_COMPETENCY_INTERNAL_CODE),
              data.getString(EntityAttributeConstants.PERFORMANCE_RANGE), toPostgresArrayString(Arrays.asList(data.getString(EntityAttributeConstants.IDS_TO_SUGGEST).split(","))));
      db.commitTransaction();
      LOGGER.info("Successfully populated resource suggestion for code : {}", id);
    } catch (Throwable e) {
      db.rollbackTransaction();
      LOGGER.error("GBRSRI:saveSuggestions: Caught exception. need to rollback and abort", e);
    } finally {
      db.close();
    }
  }

}
