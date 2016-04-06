package org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonObject;

class AJResponseJsonTransformer {

  public JsonObject transform(String ajResult) {
    return new JsonObject(ajResult);
  }
}
