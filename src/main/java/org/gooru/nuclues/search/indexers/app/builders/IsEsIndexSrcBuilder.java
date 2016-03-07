package org.gooru.nuclues.search.indexers.app.builders;

import io.vertx.core.json.JsonObject;

/**
 * @author Renuka
 * 
 */
public interface IsEsIndexSrcBuilder<S, D> {

	String buildSource(JsonObject source, JsonObject destination);

	String buildSource(JsonObject body);

	String getName();

}
