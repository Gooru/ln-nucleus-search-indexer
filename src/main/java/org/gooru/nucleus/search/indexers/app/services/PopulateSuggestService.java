package org.gooru.nucleus.search.indexers.app.services;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public interface PopulateSuggestService {

  static PopulateSuggestService instance() {
    return new PopulateSuggestServiceImpl();
  }
  
  void populateSuggestTable(RoutingContext context, String type, JsonObject body);

}
