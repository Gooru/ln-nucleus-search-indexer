package org.gooru.nuclues.search.indexers.app.builders;

import java.util.Date;

import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nuclues.search.indexers.app.index.model.CollectionEio;

import io.vertx.core.json.JsonObject;

/**
 * @author SearchTeam
 * 
 */
public class CollectionEsIndexSrcBuilder<S extends JsonObject, D extends CollectionEio> extends EsIndexSrcBuilder<S, D> {

	// TODO Add logic to build Collection indexable document from JsonObject source
	@Override
	public JsonObject buildJson(JsonObject source, JsonObject destination) {
		destination = source;
		destination.put("id", source.getString("id"));
		destination.put("type", this.getName());
		return destination;
	}
	
	@Override
	public void build(JsonObject source, D collectionEo) {
		collectionEo.setId(source.getString("id"));
		collectionEo.setContentFormat(this.getName());
		collectionEo.setLearningObjective(source.getString("description"));
		collectionEo.setIndexUpdatedDate(new Date());
	}

	@Override
	public String buildJsonSource(JsonObject source) {
		return buildSource(source, new JsonObject());
	}
	
	@Override
	public String buildSource(JsonObject source) {
		return buildSource(source, (D) new CollectionEio());
	}

	
	@Override
	public String getName() {
		return IndexType.SCOLLECTION.getType();
	}

}
