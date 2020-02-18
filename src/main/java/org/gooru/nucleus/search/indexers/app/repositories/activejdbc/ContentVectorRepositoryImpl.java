package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.ContentVectors;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class ContentVectorRepositoryImpl extends BaseIndexRepo implements ContentVectorRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentVectorRepositoryImpl.class);
  
  @Override
  public JsonObject getContentVectorsByContentId(String contentId, String contentType) {
    JsonObject returnValue = null;
    String query = null;
    query = fetchQueryForContentType(contentType, query);

    if (query == null) {
      return returnValue;
    }
    DB db = getDSDataSourceDBConnection();
    openDSDBConnection(db);

    LazyList<ContentVectors> contents = ContentVectors.findBySQL(query, contentId);
    if (contents.size() < 1) {
      LOGGER.warn("CVRI::Content id: {} not present in DB", contentId);
      closeDefaultDBConn(db);
      return returnValue;
    }
    ContentVectors content = contents.get(0);
    if (content != null) {
      returnValue = new JsonObject(content.toJson(false));
    }
    closeDBConn(db);
    return returnValue;
  }

  private String fetchQueryForContentType(String contentType, String query) {
    switch (contentType) {
    case IndexerConstants.TYPE_ASSESSMENT:
    case IndexerConstants.TYPE_COLLECTION:
    case IndexerConstants.TYPE_OFFLINE_ACTIVITY:
    case IndexerConstants.ASSESSMENT_EXTERNAL:
    case IndexerConstants.COLLECTION_EXTERNAL:
      query = ContentVectors.FETCH_COLLECTION_VECTORS;
      break;
    case IndexerConstants.TYPE_RESOURCE:
    case IndexerConstants.TYPE_QUESTION:
      query = ContentVectors.FETCH_CONTENT_VECTORS;
      break;
    case IndexerConstants.TYPE_COURSE:
      query = ContentVectors.FETCH_COURSE_VECTORS;
      break;
    case IndexerConstants.TYPE_UNIT:
      query = ContentVectors.FETCH_UNIT_VECTORS;
      break;
    case IndexerConstants.TYPE_LESSON:
      query = ContentVectors.FETCH_LESSON_VECTORS;
      break;
    }
    return query;
  }
}
