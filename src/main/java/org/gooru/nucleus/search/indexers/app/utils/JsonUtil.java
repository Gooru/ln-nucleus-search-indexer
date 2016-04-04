package org.gooru.nucleus.search.indexers.app.utils;

import java.time.Instant;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class JsonUtil {
	
	public static JsonObject set(JsonObject json, String key, Object value) {
		if(value != null) {
			if(value instanceof String)
				json.put(key, (String) value);
			else if(value instanceof Long)
				json.put(key, (Long) value);
			else if(value instanceof Integer)
				json.put(key, (Integer) value);
			else if(value instanceof Double)
				json.put(key, (Double) value);
			else if(value instanceof Float)
				json.put(key, (Float) value);
			else if(value instanceof Instant)
				json.put(key, (Instant) value);
			else if(value instanceof Boolean)
				json.put(key, (Boolean) value);
			else if(value instanceof JsonObject)
				json.put(key, (JsonObject) value);
			else if(value instanceof JsonArray)
				json.put(key, (JsonArray) value);
		}
		return json;
	}
}
