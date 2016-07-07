package org.gooru.nucleus.search.indexers.app.services;

import org.elasticsearch.client.Client;
import org.gooru.nucleus.search.indexers.app.components.ElasticSearchRegistry;

public class BaseIndexService {

  protected static int getInteger(Object value) {
    return value == null ? 0 : (int) value;
  }

  protected static long getLong(Object value) {
    long views = 0L;
    if (value != null) {
      if (value instanceof Integer) {
        views = (long) (int) value;
      } else if (value instanceof Long) {
        views = (long) value;
      }

    }
    return views;
  }

  protected Client getClient() {
    return ElasticSearchRegistry.getFactory().getClient();
  }

}
