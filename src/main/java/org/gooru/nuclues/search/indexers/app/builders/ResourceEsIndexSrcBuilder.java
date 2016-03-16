package org.gooru.nuclues.search.indexers.app.builders;

import java.util.Date;

import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nuclues.search.indexers.app.index.model.ContentEio;

import io.vertx.core.json.JsonObject;

/**
 * @author SearchTeam
 * 
 */
public class ResourceEsIndexSrcBuilder<S extends JsonObject, D extends ContentEio> extends EsIndexSrcBuilder<S, D> {

	// TODO Add logic to build indexable document from JsonObject source
	@Override
	public JsonObject buildJson(JsonObject source, JsonObject destination) {
		destination = source;
		destination.put("id", source.getString("id"));
		destination.put("type", this.getName());
		return destination;
	}
	
	@Override
	public void build(JsonObject source, D contentEo) {
		contentEo.setId(source.getString("id"));
		contentEo.setContentFormat(this.getName());
		contentEo.setDescription(source.getString("description"));
		contentEo.setIndexUpdatedDate(new Date());
	}

	@Override
	public String buildJsonSource(JsonObject source) {
		return buildSource(source, new JsonObject());
	}
	
	@Override
	public String buildSource(JsonObject source) {
		return buildSource(source, (D) new ContentEio());
	}

	@Override
	public String getName() {
		return IndexType.RESOURCE.getType();
	}

}
