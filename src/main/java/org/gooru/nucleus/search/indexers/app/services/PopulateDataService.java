package org.gooru.nucleus.search.indexers.app.services;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public interface PopulateDataService {

  static PopulateDataService instance() {
    return new PopulateDataServiceImpl();
  }
  
  void classifyResourcesToDomain(RoutingContext context, String type, JsonObject body);

  void classifyResourcesToStandards(RoutingContext context, String type, JsonObject body);

}
