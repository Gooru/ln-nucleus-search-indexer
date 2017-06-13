package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEio;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCode;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCodeMapping;

import io.vertx.core.json.JsonObject;

public class TaxonomyEsIndexSrcBuilder<S extends JsonObject, D extends TaxonomyEio> extends EsIndexSrcBuilder<S, D> {

  @SuppressWarnings("unchecked")
  @Override
  public String buildSource(JsonObject source) throws Exception {
    return buildSource(source, (D) new TaxonomyEio());
  }

  @Override
  public String getName() {
    return IndexType.TAXONOMY.getType();
  }

  @SuppressWarnings({ "rawtypes" })
  @Override
  protected JsonObject build(JsonObject source, D taxonomyEo) throws Exception {
    String codeId = source.getString(TaxonomyCode.ID);
    taxonomyEo.setId(codeId);
    taxonomyEo.setIndexType(IndexerConstants.TYPE_TAXONOMY);
    taxonomyEo.setDisplayCode(TaxonomyCode.CODE);
    taxonomyEo.setTitle(source.getString(EntityAttributeConstants.TITLE, null));
    taxonomyEo.setDescription(source.getString(EntityAttributeConstants.DESCRIPTION, null));
    taxonomyEo.setCodeType(source.getString(EntityAttributeConstants.CODE_TYPE, null));
    taxonomyEo.setIndexUpdatedTime(new Date());

    //Set Competency
    JsonObject competencyObject = getTaxonomyCodeRepo().getCode(source.getString(TaxonomyCode.PARENT_TAXONOMY_CODE_ID));
    if (competencyObject != null) {
      TaxonomyEio competencyEo = new TaxonomyEio();
      competencyEo.setId(competencyObject.getString(EntityAttributeConstants.ID));
      competencyEo.setDisplayCode(competencyObject.getString(EntityAttributeConstants.CODE));
      competencyEo.setTitle(competencyObject.getString(EntityAttributeConstants.TITLE));
      competencyEo.setDescription(competencyObject.getString(EntityAttributeConstants.DESCRIPTION));
      competencyEo.setCodeType(competencyObject.getString(EntityAttributeConstants.CODE_TYPE, null));
      taxonomyEo.setCompetency(competencyEo.getTaxonomyJson());
    }
    
    //Set Crosswalk
    Map gdtCodeMap = getTaxonomyRepo().getGDTCode(codeId);
    if (gdtCodeMap != null && !gdtCodeMap.isEmpty()) {
      String gdtCode = (String) gdtCodeMap.get(TaxonomyCodeMapping.SOURCE_TAXONOMY_CODE_ID);
      JsonObject equivalentCompetencies = new JsonObject();
      List<Map> equivalentCompetencyList = getTaxonomyRepo().getEquivalentCompetencies(gdtCode);
      if (equivalentCompetencyList != null && !equivalentCompetencyList.isEmpty()) {
        equivalentCompetencyList.forEach(eqCompetency -> {
          JsonObject equivalentCompetency = getMappedAndEquivalentCompetency(eqCompetency);
          equivalentCompetencies.put(eqCompetency.get(TaxonomyCodeMapping.TARGET_FRAMEWORK_ID).toString(), equivalentCompetency);
        });
      }
      taxonomyEo.setGutCode(gdtCode);
      taxonomyEo.setEquivalentCompetencies(equivalentCompetencies);
    }
    
    //TODO
    /* taxonomyEo.setGrade();
    taxonomyEo.setSubject();
    taxonomyEo.setCourse();
    taxonomyEo.setDomain();*/
    
    return taxonomyEo.getTaxonomyJson();
  }
  
  @SuppressWarnings("rawtypes")
  private JsonObject getMappedAndEquivalentCompetency(Map equivalentCompetency) {
    JsonObject eqCompetency = new JsonObject();
    eqCompetency.put(EntityAttributeConstants.ID, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_TAXONOMY_CODE_ID).toString());
    eqCompetency.put(EntityAttributeConstants.CODE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_DISPLAY_CODE).toString());
    eqCompetency.put(IndexerConstants.FRAMEWORK_CODE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_FRAMEWORK_ID).toString());
    eqCompetency.put(EntityAttributeConstants.TITLE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_TITLE).toString());
    return eqCompetency;
  }

}
