package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@SuppressWarnings("rawtypes")
public interface TaxonomyRepository {

  static TaxonomyRepository instance() {
    return new TaxonomyRepositoryImpl();
  }
  List<Map> getTaxonomyData(String codeId, String label);
  
  JsonObject getGdtMapping(String targetCodeId);
  
  JsonObject getCrosswalkCodes(String sourceCodeId);
  
  JsonArray getGDTCode(String targetCodeId);
  
  JsonArray getGutPrerequisites(String gutCompetencyId);
  
  String getCourseCodeByTitleAndFw(String courseTitle, String framework);
  
  String getGutSubjectCodeByTitle(String subjectTitle);

}
