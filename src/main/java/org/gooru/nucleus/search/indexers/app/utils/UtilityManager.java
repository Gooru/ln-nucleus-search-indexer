package org.gooru.nucleus.search.indexers.app.utils;

import java.util.HashMap;
import java.util.Map;

import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializer;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * This is a manager class to initialize the utilities, Utilities initialized
 * may depend on the DB or application state. Thus their initialization sequence
 * may matter. It is advisable to keep the utility initialization for last.
 */
public final class UtilityManager implements Initializer, Finalizer {

  private static Map<String, Object> cacheMap = null;

  public static UtilityManager getInstance() {
    return Holder.INSTANCE;
  }

  @Override
  public void finalizeComponent() {

  }

  @Override
  public void initializeComponent(Vertx vertx, JsonObject config) {
    cacheMap = new HashMap<String, Object>();
  }

  public static Map<String, Object> getCache() {
    return cacheMap;
  }

  private static final class Holder {
    private static final UtilityManager INSTANCE = new UtilityManager();
  }
}
