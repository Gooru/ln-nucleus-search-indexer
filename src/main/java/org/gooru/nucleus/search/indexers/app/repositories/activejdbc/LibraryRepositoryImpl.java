package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import org.gooru.nucleus.search.indexers.app.repositories.entities.Library;
import org.gooru.nucleus.search.indexers.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class LibraryRepositoryImpl extends BaseIndexRepo implements LibraryRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryRepositoryImpl.class);
    
    @Override
    public JsonObject getLibraryContentById(String contentId) {
      JsonObject returnValue = null;
      DB db = getDefaultDataSourceDBConnection();
      try {
        openDefaultDBConnection(db);
        LazyList<Library> result = Library.findBySQL(Library.FETCH_LIBRARY_CONTENT, contentId);
        if (result != null && result.size() > 0) {
          returnValue =  new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, null).toJson(result.get(0)));
        }
      } catch (Exception e) {
        LOGGER.error("Not able to fetch library content : {} error : {}", contentId, e);
      }
      closeDefaultDBConn(db);
      return returnValue;
    }
  
}
