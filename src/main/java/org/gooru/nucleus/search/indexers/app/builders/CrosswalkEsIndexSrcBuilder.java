package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.CrosswalkEio;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCodeMapping;

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

  @SuppressWarnings("rawtypes")
  @Override
  protected JsonObject build(JsonObject source, D crosswalkEo) throws Exception {
    String leafDisplayCode = source.getString(TaxonomyCodeMapping.TARGET_DISPLAY_CODE);
    String leafInternalCode = source.getString(TaxonomyCodeMapping.TARGET_TAXONOMY_CODE_ID);
    String gdtCode = source.getString(TaxonomyCodeMapping.SOURCE_TAXONOMY_CODE_ID);
    JsonObject equivalentCompetencies = new JsonObject();
    List<Map> equivalentCompetencyList = getTaxonomyRepo().getEquivalentCompetencies(gdtCode);
    if (equivalentCompetencyList != null && !equivalentCompetencyList.isEmpty()) {
      equivalentCompetencyList.forEach(eqCompetency -> {
        JsonObject equivalentCompetency = getMappedAndEquivalentCompetency(eqCompetency);
        equivalentCompetencies.put(eqCompetency.get(TaxonomyCodeMapping.TARGET_FRAMEWORK_ID).toString(), equivalentCompetency);
      });
    }
    crosswalkEo.setGdtCode(gdtCode);
    crosswalkEo.setId(leafInternalCode);
    crosswalkEo.setIndexType(IndexerConstants.TYPE_CROSSWALK);
    crosswalkEo.setDisplayCode(leafDisplayCode);
    crosswalkEo.setEquivalentCompetencies(equivalentCompetencies);
    crosswalkEo.setIndexUpdatedTime(new Date());
    return crosswalkEo.getCrosswalkJson();
  }
  
  @SuppressWarnings("rawtypes")
  private JsonObject getMappedAndEquivalentCompetency(Map equivalentCompetency) {
    JsonObject eqCompetency = new JsonObject(); 
    eqCompetency.put(EntityAttributeConstants.ID, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_TAXONOMY_CODE_ID).toString());
    eqCompetency.put(EntityAttributeConstants.CODE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_DISPLAY_CODE).toString());
    eqCompetency.put(IndexerConstants.FRAMEWORK_CODE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_FRAMEWORK_ID).toString());
    eqCompetency.put(EntityAttributeConstants.TITLE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_TITLE).toString());
    //eqCompetency.put(IndexerConstants.PARENT_TITLE, equivalentCompetency.get(Taxonomy.TARGET_PARENT_TITLE).toString());
    return eqCompetency;
  }

}
