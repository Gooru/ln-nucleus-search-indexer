package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.CrosswalkEio;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCodeMapping;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CrosswalkEsIndexSrcBuilder<S extends JsonObject, D extends CrosswalkEio> extends EsIndexSrcBuilder<S, D> {

  @SuppressWarnings("unchecked")
  @Override
  public String buildSource(JsonObject source) throws Exception {
    return buildSource(source, (D) new CrosswalkEio());
  }

  @Override
  public String getName() {
    return IndexType.CROSSWALK.getType();
  }

  @Override
  protected JsonObject build(JsonObject source, D crosswalkEo) throws Exception {
    String gdtCode = source.getString(EntityAttributeConstants.ID);
    crosswalkEo.setId(gdtCode);
    crosswalkEo.setIndexType(IndexerConstants.TYPE_CROSSWALK);
    crosswalkEo.setIndexUpdatedTime(new Date());
    
    JsonArray cwSource = source.getJsonArray(IndexerConstants.TYPE_CROSSWALK);
    if (cwSource != null) {
      JsonArray crosswalkCodes = new JsonArray();
      cwSource.stream().forEach(eqCompetency -> {
        JsonObject equivalentCompetency = (JsonObject) eqCompetency;
        JsonObject crosswalkCode = setCrosswalkObject(equivalentCompetency);
        crosswalkCodes.add(crosswalkCode);
        if (crosswalkEo.getCodeType() == null || crosswalkEo.getCode() == null) {
          crosswalkEo.setCodeType(equivalentCompetency.getString(TaxonomyCodeMapping.TARGET_CODE_TYPE));
          crosswalkEo.setCode(equivalentCompetency.getString(TaxonomyCodeMapping.SOURCE_DISPLAY_CODE));
        }
      });
      crosswalkEo.setCrosswalkCodes(crosswalkCodes);
    }
    return crosswalkEo.getCrosswalkJson();
  }
  
  private JsonObject setCrosswalkObject(JsonObject equivalentCompetency) {
    JsonObject eqCompetency = new JsonObject(); 
    eqCompetency.put(EntityAttributeConstants.ID, equivalentCompetency.getString(TaxonomyCodeMapping.TARGET_TAXONOMY_CODE_ID));
    eqCompetency.put(EntityAttributeConstants.CODE, equivalentCompetency.getString(TaxonomyCodeMapping.TARGET_DISPLAY_CODE));
    eqCompetency.put(IndexFields.FRAMEWORK_CODE, equivalentCompetency.getString(TaxonomyCodeMapping.TARGET_FRAMEWORK_ID));
    eqCompetency.put(EntityAttributeConstants.TITLE, equivalentCompetency.getString(TaxonomyCodeMapping.TARGET_TITLE));
    return eqCompetency;
  }

}