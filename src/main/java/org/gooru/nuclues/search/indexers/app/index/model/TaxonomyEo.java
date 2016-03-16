package org.gooru.nuclues.search.indexers.app.index.model;

import java.util.Set;

public class TaxonomyEo {

	private Set<CodeEo> subject;
	
    private Set<CodeEo> course;

    private Set<String> standards;
    
    private Set<String> learningTargets;

	private String taxonomyDataSet;

	private String taxonomySkills;

	private Integer hasNoTaxonomy;

	private Integer hasNoStandard;
	
	public Set<CodeEo> getSubject() {
		return subject;
	}

	public void setSubject(Set<CodeEo> subject) {
		this.subject = subject;
	}

	public Set<CodeEo> getCourse() {
		return course;
	}

	public void setCourse(Set<CodeEo> course) {
		this.course = course;
	}

	public Set<String> getStandards() {
		return standards;
	}

	public void setStandards(Set<String> standards) {
		this.standards = standards;
	}

	public String getTaxonomyDataSet() {
		return taxonomyDataSet;
	}

	public void setTaxonomyDataSet(String taxonomyDataSet) {
		this.taxonomyDataSet = taxonomyDataSet;
	}

	public String getTaxonomySkills() {
		return taxonomySkills;
	}

	public void setTaxonomySkills(String taxonomySkills) {
		this.taxonomySkills = taxonomySkills;
	}

	public Integer getHasNoTaxonomy() {
		return hasNoTaxonomy;
	}

	public void setHasNoTaxonomy(Integer hasNoTaxonomy) {
		this.hasNoTaxonomy = hasNoTaxonomy;
	}

	public Integer getHasNoStandard() {
		return hasNoStandard;
	}

	public void setHasNoStandard(Integer hasNoStandard) {
		this.hasNoStandard = hasNoStandard;
	}

	public Set<String> getLearningTargets() {
		return learningTargets;
	}

	public void setLearningTargets(Set<String> learningTargets) {
		this.learningTargets = learningTargets;
	}
	
}
