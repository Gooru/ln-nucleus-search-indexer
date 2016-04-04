package org.gooru.nucleus.search.indexers.app.utils;

import java.util.HashMap;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.EsIndex;

public final class IndexNameHolder {
	
	public static Map<EsIndex, String> indexNames = new HashMap<EsIndex, String>();
	
	public static void registerIndex(EsIndex key, String value){
		synchronized (indexNames) {
			indexNames.put(key, value);
		}
	}
	
	public static String getIndexName(EsIndex key){
		return indexNames.get(key);
	}
}
