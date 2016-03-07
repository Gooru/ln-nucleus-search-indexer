package org.gooru.nuclues.search.indexers.app.builders;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nuclues.search.indexers.app.utils.VoidTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import io.vertx.core.json.JsonObject;

/**
 * @author Renuka
 * 
 */
public abstract class EsIndexSrcBuilder<S, D> implements IsEsIndexSrcBuilder<S, D> {

	protected static final Logger LOG = LoggerFactory.getLogger(EsIndexSrcBuilder.class);

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	private static final VoidTransformer VOID_TRANSFORMER = new VoidTransformer();

	private static JSONSerializer serializer;

	private static final Map<String, IsEsIndexSrcBuilder<?, ?>> esIndexSrcBuilders = new HashMap<String, IsEsIndexSrcBuilder<?, ?>>();

	static {
		serializer = initSerializer();
		esIndexSrcBuilders.put(IndexType.RESOURCE.getType(), new ResourceEsIndexSrcBuilder<>());
		esIndexSrcBuilders.put(IndexType.SCOLLECTION.getType(), new CollectionEsIndexSrcBuilder<>());
	}

	public static IsEsIndexSrcBuilder<?, ?> get(String requestBuilderName) {
		if (esIndexSrcBuilders.containsKey(requestBuilderName)) {
			return esIndexSrcBuilders.get(requestBuilderName);
		} else {
			throw new RuntimeException("Oops! Invalid type : " + requestBuilderName);
		}
	}

	@Override
	public String buildSource(JsonObject source, JsonObject destination) {
		build(source, destination);
		return getSerializer().deepSerialize(destination);
	}

	protected abstract void build(JsonObject source, JsonObject destination);

	public JSONSerializer getSerializer() {
		return serializer;
	}

	protected static JSONSerializer initSerializer() {
		JSONSerializer jsonSerializer = new JSONSerializer().transform(new DateTransformer(DATE_FORMAT), Date.class).transform(VOID_TRANSFORMER, void.class).exclude("*.class");
		if (getExcludes() != null) {
			jsonSerializer.exclude(getExcludes());
		}
		return jsonSerializer;
	}

	protected static String[] getExcludes() {
		return null;
	}
}
