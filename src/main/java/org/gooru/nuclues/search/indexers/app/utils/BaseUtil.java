package org.gooru.nuclues.search.indexers.app.utils;

import java.util.Map;

public class BaseUtil {

	public static String checkNullAndGetString(Map map, String key) {
		if (map.containsKey(key) && map.get(key) != null) {
			return map.get(key).toString();
		}
		return null;
	}
}
