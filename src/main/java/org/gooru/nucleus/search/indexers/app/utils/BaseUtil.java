package org.gooru.nucleus.search.indexers.app.utils;

import java.util.Map;

public final class BaseUtil {

  private BaseUtil() {
    throw new AssertionError();
  }

  @SuppressWarnings("rawtypes")
  public static String checkNullAndGetString(Map map, String key) {
    if (map.containsKey(key) && map.get(key) != null && map.get(key).toString().trim() != null) {
      return map.get(key).toString();
    }
    return null;
  }
  
  public static Boolean isNotNull(Map<String, Object> map, String key) {
    if (map.containsKey(key) && map.get(key) != null && map.get(key).toString().trim() != null) {
      return true;
    }
    return false;
  }
}
