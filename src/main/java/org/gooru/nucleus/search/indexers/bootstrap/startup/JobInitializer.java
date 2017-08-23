package org.gooru.nucleus.search.indexers.bootstrap.startup;

import io.vertx.core.json.JsonObject;

public interface JobInitializer {

  void deployJob(JsonObject config);

}
