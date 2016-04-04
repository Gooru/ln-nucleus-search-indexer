package org.gooru.nucleus.search.indexers.app.index.model;

import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class TaxonomyEo {

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
		taxonomy = JsonUtil.set(taxonomy, "subject", subject);
	}

	public JsonArray getCourse() {
		return taxonomy.getJsonArray("course", null);
	}

	public void setCourse(JsonArray course) {
		taxonomy = JsonUtil.set(taxonomy, "course", course);
	}

	public JsonArray getDomain() {
		return taxonomy.getJsonArray("domain", null);
	}

	public void setDomain(JsonArray domain) {
		taxonomy = JsonUtil.set(taxonomy, "domain", domain);
	}

	public JsonArray getStandards() {
		return taxonomy.getJsonArray("standards", null);
	}

	public void setStandards(JsonArray standards) {
		taxonomy = JsonUtil.set(taxonomy, "standards", standards);
	}

	public String getTaxonomyDataSet() {
		return taxonomy.getString("taxonomyDataSet", null);
	}

	public void setTaxonomyDataSet(String taxonomyDataSet) {
		taxonomy = JsonUtil.set(taxonomy, "taxonomyDataSet", taxonomyDataSet);
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
		this.taxonomy = JsonUtil.set(taxonomy, "hasStandard", hasStandard);
	}

	public JsonArray getLearningTargets() {
		return taxonomy.getJsonArray("learningTargets", null);
	}

	public void setLearningTargets(JsonArray learningTargets) {
		this.taxonomy = JsonUtil.set(taxonomy, "learningTargets", learningTargets);
	}

}
