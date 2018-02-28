package org.gooru.nucleus.search.indexers.app.index.model;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.stream.Collectors;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

public class TaxonomyEo extends JsonObject {

  private JsonObject taxonomy = null;

  public TaxonomyEo() {
    this.taxonomy = new JsonObject();
  }

  public JsonObject getTaxonomyJson() {
    return this.taxonomy;
  }

  public JsonArray getSubject() {
    return taxonomy.getJsonArray("subject", null);
  }

  public void setSubject(JsonArray subject) {
    taxonomy = JsonUtil.set(taxonomy, "subject", new JsonArray(subject.stream().distinct().collect(Collectors.toList())));
  }

  public JsonArray getCourse() {
    return taxonomy.getJsonArray("course", null);
  }

  public void setCourse(JsonArray course) {
    taxonomy = JsonUtil.set(taxonomy, "course", new JsonArray(course.stream().distinct().collect(Collectors.toList())));
  }

  public JsonArray getDomain() {
    return taxonomy.getJsonArray("domain", null);
  }

  public void setDomain(JsonArray domain) {
    taxonomy = JsonUtil.set(taxonomy, "domain", new JsonArray(domain.stream().distinct().collect(Collectors.toList())));
  }

  public JsonArray getStandards() {
    return taxonomy.getJsonArray("standards", null);
  }

  public void setStandards(JsonArray standards) {
    taxonomy = JsonUtil.set(taxonomy, "standards", new JsonArray(standards.stream().distinct().collect(Collectors.toList())));
  }

  public JsonArray getLearningTargets() {
    return taxonomy.getJsonArray("learningTargets", null);
  }

  public void setLearningTargets(JsonArray learningTargets) {
    this.taxonomy = JsonUtil.set(taxonomy, "learningTargets", new JsonArray(learningTargets.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getStandardsDisplay() {
    return taxonomy.getJsonArray("standardsDisplay", null);
  }

  public void setStandardsDisplay(JsonArray standardsDisplay) {
    taxonomy = JsonUtil.set(taxonomy, "standardsDisplay", new JsonArray(standardsDisplay.stream().distinct().collect(Collectors.toList())));
  }

  public JsonArray getLearningTargetsDisplay() {
    return taxonomy.getJsonArray("ltDisplay", null);
  }

  public void setLearningTargetsDisplay(JsonArray ltDisplay) {
    this.taxonomy = JsonUtil.set(taxonomy, "ltDisplay", new JsonArray(ltDisplay.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getLeafInternalCodes() {
    return taxonomy.getJsonArray("leafInternalCodes", null);
  }

  public void setLeafInternalCodes(JsonArray leafInternalCodes) {
    taxonomy = JsonUtil.set(taxonomy, "leafInternalCodes", new JsonArray(leafInternalCodes.stream().distinct().collect(Collectors.toList())));
  }
  
  public String getTaxonomyDataSet() {
    return taxonomy.getString("taxonomyDataSet", null);
  }

  public void setTaxonomyDataSet(String taxonomyDataSet) {
    taxonomy = JsonUtil.set(taxonomy, "taxonomyDataSet", taxonomyDataSet);
  }
  
  public JsonObject getTaxonomySet() {
    return taxonomy.getJsonObject("taxonomySet", null);
  }

  public void setTaxonomySet(JsonObject taxonomySet) {
    taxonomy = JsonUtil.set(taxonomy, "taxonomySet", taxonomySet);
  }

  public String getTaxonomySkills() {
    return taxonomy.getString("taxonomySkills", null);
  }

  public void setTaxonomySkills(String taxonomySkills) {
    taxonomy = JsonUtil.set(taxonomy, "taxonomySkills", taxonomySkills);
  }

  public Integer getHasStandard() {
    return taxonomy.getInteger("hasStandard", 0);
  }

  public void setHasStandard(Integer hasStandard) {
    if (hasStandard == null) {
      hasStandard = 0;
    }
    taxonomy = JsonUtil.set(taxonomy, "hasStandard", hasStandard);
  }
  
  public JsonArray getLeafDisplayCodes() {
    return taxonomy.getJsonArray("leafDisplayCodes", null);
  }

  public void setLeafDisplayCodes(JsonArray leafDisplayCodes) {
    taxonomy = JsonUtil.set(taxonomy, "leafDisplayCodes", new JsonArray(leafDisplayCodes.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getEquivalentCompetencies() {
    return taxonomy.getJsonArray("equivalentCompetencies", null);
  }

  public void setEquivalentCompetencies(JsonArray equivalentCompetencies) {
    taxonomy = JsonUtil.set(taxonomy, "equivalentCompetencies", new JsonArray(equivalentCompetencies.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getAllEquivalentInternalCodes() {
    return taxonomy.getJsonArray("allEquivalentInternalCodes", null);
  }

  public void setAllEquivalentInternalCodes(JsonArray allEquivalentInternalCodes) {
    taxonomy = JsonUtil.set(taxonomy, "allEquivalentInternalCodes", new JsonArray(allEquivalentInternalCodes.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getAllEquivalentDisplayCodes() {
    return taxonomy.getJsonArray("allEquivalentDisplayCodes", null);
  }

  public void setAllEquivalentDisplayCodes(JsonArray allEquivalentDisplayCodes) {
    taxonomy = JsonUtil.set(taxonomy, "allEquivalentDisplayCodes", new JsonArray(allEquivalentDisplayCodes.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getAllEqCourseInternalCodes() {
    return taxonomy.getJsonArray("allEqCourseInternalCodes", null);
  }

  public void setAllEqCourseInternalCodes(JsonArray allEqCourseInternalCodes) {
    taxonomy = JsonUtil.set(taxonomy, "allEqCourseInternalCodes", new JsonArray(allEqCourseInternalCodes.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getAllEqDomainInternalCodes() {
    return taxonomy.getJsonArray("allEqDomainInternalCodes", null);
  }

  public void setAllEqDomainInternalCodes(JsonArray allEqDomainInternalCodes) {
    taxonomy = JsonUtil.set(taxonomy, "allEqDomainInternalCodes", new JsonArray(allEqDomainInternalCodes.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getGutCodes() {
    return taxonomy.getJsonArray("gutCodes", null);
  } 
  
  public void setGutCodes(JsonArray gutCodes) {
    taxonomy = JsonUtil.set(taxonomy, "gutCodes", new JsonArray(gutCodes.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getLeafAggGutCodes() {
    return taxonomy.getJsonArray("leafAggGutCodes", null);
  } 
  
  public void setLeafAggGutCodes(JsonArray leafAggGutCodes) {
    taxonomy = JsonUtil.set(taxonomy, "leafAggGutCodes", new JsonArray(leafAggGutCodes.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getLeafAggInternalCodes() {
    return taxonomy.getJsonArray("leafAggInternalCodes", null);
  } 
  
  public void setLeafAggInternalCodes(JsonArray leafAggInternalCodes) {
    taxonomy = JsonUtil.set(taxonomy, "leafAggInternalCodes", new JsonArray(leafAggInternalCodes.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getRelatedLeafInternalCodes() {
    return taxonomy.getJsonArray("relatedLeafInternalCodes", null);
  } 
  
  public void setRelatedLeafInternalCodes(JsonArray relatedLeafInternalCodes) {
    taxonomy = JsonUtil.set(taxonomy, "relatedLeafInternalCodes", new JsonArray(relatedLeafInternalCodes.stream().distinct().collect(Collectors.toList())));
  }
  
  public JsonArray getRelatedGutCodes() {
    return taxonomy.getJsonArray("relatedGutCodes", null);
  } 
  
  public void setRelatedGutCodes(JsonArray relatedGutCodes) {
    taxonomy = JsonUtil.set(taxonomy, "relatedGutCodes", new JsonArray(relatedGutCodes.stream().distinct().collect(Collectors.toList())));
  } 
  
  public JsonArray getAllEqRelatedInternalCodes() {
    return taxonomy.getJsonArray("allEqRelatedInternalCodes", null);
  }

  public void setAllEqRelatedInternalCodes(JsonArray allEqRelatedInternalCodes) {
    taxonomy = JsonUtil.set(taxonomy, "allEqRelatedInternalCodes", new JsonArray(allEqRelatedInternalCodes.stream().distinct().collect(Collectors.toList())));
  }
  
}
