package org.gooru.nucleus.search.indexers.app.utils;

import org.gooru.nucleus.search.indexers.app.constants.EsIndex;

import java.util.HashMap;
import java.util.Map;

public final class IndexNameHolder {

  public static Map<EsIndex, String> indexNames = new HashMap<EsIndex, String>();

  private IndexNameHolder() {
    throw new AssertionError();
  }

  public static void registerIndex(EsIndex key, String value) {
    synchronized (indexNames) {
      indexNames.put(key, value);
    }
  }

  public static String getIndexName(EsIndex key) {
    return indexNames.get(key);
  }
}
