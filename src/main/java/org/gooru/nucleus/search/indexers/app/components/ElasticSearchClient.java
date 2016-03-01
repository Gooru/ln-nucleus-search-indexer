package org.gooru.nucleus.search.indexers.app.components;

import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializer;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class ElasticSearchClient implements Finalizer, Initializer {

  @Override
  public void initializeComponent(Vertx vertx, JsonObject config) {
    // TODO Auto-generated method stub

  }

  @Override
  public void finalizeComponent() {
    // TODO Auto-generated method stub

  }

}
