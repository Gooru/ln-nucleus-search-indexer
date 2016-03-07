package org.gooru.nuclues.search.indexers.app.builders;

import org.gooru.nucleus.search.indexers.app.constants.IndexType;

import io.vertx.core.json.JsonObject;

/**
 * @author SearchTeam
 * 
 */
public class ResourceEsIndexSrcBuilder<S extends JsonObject, D extends JsonObject> extends EsIndexSrcBuilder<S, D> {

	// TODO Add logic to build indexable document from JsonObject source
	@Override
	public void build(JsonObject source, JsonObject destination) {

	}

	@Override
	public String buildSource(JsonObject source) {
		return buildSource(source, new JsonObject());
	}

	@Override
	public String getName() {
		return IndexType.RESOURCE.getType();
	}

}
