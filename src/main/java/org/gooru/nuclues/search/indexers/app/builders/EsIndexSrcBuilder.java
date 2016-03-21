package org.gooru.nuclues.search.indexers.app.builders;

import java.util.HashMap;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * @author Renuka
 * 
 */
public abstract class EsIndexSrcBuilder<S, D> implements IsEsIndexSrcBuilder<S, D> {

	protected static final Logger LOGGER = LoggerFactory.getLogger(EsIndexSrcBuilder.class);
	protected static final String dateInputPatterns[] = {"yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss.SSS", "yyyy/MM/dd", "yyyy-MM" };
	protected static final String dateOutputPattern = "yyyy/MM/dd HH:mm:ss";
	
	private static final Map<String, IsEsIndexSrcBuilder<?, ?>> esIndexSrcBuilders = new HashMap<>();

	static {
		registerESIndexSrcBuilders();
	}

	private static void registerESIndexSrcBuilders() {
		esIndexSrcBuilders.put(IndexType.RESOURCE.getType(), new ContentEsIndexSrcBuilder<>());
		esIndexSrcBuilders.put(IndexType.COLLECTION.getType(), new CollectionEsIndexSrcBuilder<>());		
	}
	
	public static IsEsIndexSrcBuilder<?, ?> get(String requestBuilderName) {
		if (esIndexSrcBuilders.containsKey(requestBuilderName)) {
			return esIndexSrcBuilders.get(requestBuilderName);
		} else {
			throw new RuntimeException("Oops! Invalid type : " + requestBuilderName);
		}
	}
	
	@Override
	public String buildSource(JsonObject source, D destination) {
		return build(source, destination).toString();
	}

	protected abstract JsonObject build(JsonObject source, D destination);

}
