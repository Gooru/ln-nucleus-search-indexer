package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexFields;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.CourseEo;
import org.gooru.nucleus.search.indexers.app.index.model.DomainEo;
import org.gooru.nucleus.search.indexers.app.index.model.SubjectEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEio;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCode;
import org.gooru.nucleus.search.indexers.app.repositories.entities.TaxonomyCodeMapping;

import io.vertx.core.json.JsonArray;
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
    taxonomyEo.setDisplayCode(source.getString(EntityAttributeConstants.CODE, null));
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
    JsonObject gutCodeObject = getTaxonomyRepo().getGDTCode(codeId);
    if (gutCodeObject != null && !gutCodeObject.isEmpty()) {
      String gutCode = gutCodeObject.getString(TaxonomyCodeMapping.SOURCE_TAXONOMY_CODE_ID);
      List<Map> equivalentCompetencyList = getTaxonomyRepo().getEquivalentCompetencies(gutCode);
      if (equivalentCompetencyList != null && !equivalentCompetencyList.isEmpty()) {
        JsonArray crosswalkCodes = new JsonArray();
        equivalentCompetencyList.forEach(eqCompetency -> {
          if (!eqCompetency.get(TaxonomyCode.TARGET_TAXONOMY_CODE_ID).toString().equalsIgnoreCase(codeId)) {
            JsonObject crosswalkCode = setCrosswalkObject(eqCompetency);
            crosswalkCodes.add(crosswalkCode);
          }
        });
        if (!crosswalkCodes.isEmpty()) taxonomyEo.setCrosswalkCodes(crosswalkCodes);
      }
      taxonomyEo.setGutCode(gutCode);
    }
    
    String subjectCode = null;
    String courseCode = null;
    String domainCode = null;
    String[] codes = codeId.split(IndexerConstants.HYPHEN_SEPARATOR);
    if (codes.length > 0) {
      if (codes.length == 1) {
        subjectCode = codeId;
      } else if (codes.length > 1) {
        subjectCode = codeId.substring(0, StringUtils.ordinalIndexOf(codeId, "-", 1));
      }
      if (codes.length == 2) {
        courseCode = codeId;
      } else if (codes.length > 2) {
        courseCode = codeId.substring(0, StringUtils.ordinalIndexOf(codeId, "-", 2));
      }
      if (codes.length == 3) {
        domainCode = codeId;
      } else if (codes.length > 3) {
        domainCode = codeId.substring(0, StringUtils.ordinalIndexOf(codeId, "-", 3));
      }
    }
    if (subjectCode != null) {
      SubjectEo subject = new SubjectEo();
      List<Map> subjectData = getTaxonomyRepo().getTaxonomyData(subjectCode, IndexerConstants.SUBJECT);
      if (subjectData != null && subjectData.size() > 0) {
        String subjectTitle = subjectData.get(0).get(EntityAttributeConstants.TITLE).toString();
        subject.setTitle(subjectTitle);
      }
      subject.setId(subjectCode);
      taxonomyEo.setSubject(subject.getSubjectJson());
    }
    if (courseCode != null) {
      CourseEo course = new CourseEo();
      List<Map> courseData = getTaxonomyRepo().getTaxonomyData(courseCode, IndexerConstants.COURSE);
      if (courseData != null && courseData.size() > 0) {
        String courseTitle = courseData.get(0).get(EntityAttributeConstants.TITLE).toString();
        course.setTitle(courseTitle);
      }
      course.setId(courseCode);
      taxonomyEo.setCourse(course.getCourseJson());
    }
    if (domainCode != null) {
      DomainEo domain = new DomainEo();
      List<Map> domainData = getTaxonomyRepo().getTaxonomyData(domainCode, IndexerConstants.DOMAIN);
      if (domainData != null && domainData.size() > 0) {
        String domainTitle = domainData.get(0).get(EntityAttributeConstants.TITLE).toString();
        domain.setTitle(domainTitle);
      }
      domain.setId(domainCode);
      taxonomyEo.setDomain(domain.getDomainJson());
    }
    
    //TODO
    // taxonomyEo.setGrade();
    
    return taxonomyEo.getTaxonomyJson();
  }
  
  @SuppressWarnings("rawtypes")
  private JsonObject setCrosswalkObject(Map equivalentCompetency) {
    JsonObject eqCompetency = new JsonObject(); 
    eqCompetency.put(EntityAttributeConstants.ID, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_TAXONOMY_CODE_ID).toString());
    eqCompetency.put(EntityAttributeConstants.CODE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_DISPLAY_CODE).toString());
    eqCompetency.put(IndexFields.FRAMEWORK_CODE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_FRAMEWORK_ID).toString());
    eqCompetency.put(EntityAttributeConstants.TITLE, equivalentCompetency.get(TaxonomyCodeMapping.TARGET_TITLE).toString());
    return eqCompetency;
  }

}
