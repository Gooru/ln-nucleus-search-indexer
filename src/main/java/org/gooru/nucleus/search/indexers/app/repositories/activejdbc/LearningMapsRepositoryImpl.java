package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.repositories.entities.LearningMaps;
import org.javalite.activejdbc.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LearningMapsRepositoryImpl extends BaseIndexRepo implements LearningMapsRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(LearningMapsRepositoryImpl.class);

  @Override
  public void updateRQCALearningMaps(List<Map<String, Object>> array) {
    array.forEach(dat -> {
      Map<String, Object> data = (Map<String, Object>) dat;
      DB db = getDefaultDataSourceDBConnection();
      try {
        openConnection(db);
        db.openTransaction();
        db.exec(LearningMaps.UPDATE_RQCA_QUERY, 
                data.containsKey("resource") ? data.get("resource") : 0,
                data.containsKey("question") ? data.get("question") : 0, 
                data.containsKey("collection") ? data.get("collection") : 0,
                data.containsKey("assessment") ? data.get("assessment") : 0, 
                data.containsKey("rubric") ? data.get("rubric") : 0,
                data.get(EntityAttributeConstants.ID));
        db.commitTransaction();
        LOGGER.info("Successfully populated RQCA stats for code :{}", data.get(EntityAttributeConstants.ID));
      } catch (Throwable e) {
        db.rollbackTransaction();
        LOGGER.error("LMRI:saveRQCALearningMaps: Caught exception. need to rollback and abort", e);
      } finally {
        db.close();
      }
    });
  }
  
  @Override
  public void updateCULForLearningMaps(Map<String, Object> data) {

    DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);
      db.openTransaction();
      db.exec(LearningMaps.UPDATE_CUL_QUERY, 
              data.containsKey("course") ? data.get("course") : 0, 
              data.containsKey("unit") ? data.get("unit") : 0,
              data.containsKey("lesson") ? data.get("lesson") : 0, 
              data.containsKey("signature_resource") ? data.get("signature_resource") : 0,
              data.containsKey("signature_collection") ? data.get("signature_collection") : 0,
              data.containsKey("signature_assessment") ? data.get("signature_assessment") : 0, 
              data.get(EntityAttributeConstants.ID));
      db.commitTransaction();
      LOGGER.info("Successfully populated CUL stats for code : {}", data.get(EntityAttributeConstants.ID));
    } catch (Throwable e) {
      db.rollbackTransaction();
      LOGGER.error("LMRI:saveCULForLearningMaps: Caught exception. need to rollback and abort", e);
    } finally {
      db.close();
    }
  }
  
  @Override
  public void updateRQCACULLearningMaps(Map<String, Object> data) {

    DB db = getDefaultDataSourceDBConnection();
    try {
      openConnection(db);
      db.openTransaction();
      db.exec(LearningMaps.UPDATE_RQCACUL_QUERY, 
              data.containsKey("resource_count") ? data.get("resource_count") : 0,
              data.containsKey("question_count") ? data.get("question_count") : 0, 
              data.containsKey("collection_count") ? data.get("collection_count") : 0,
              data.containsKey("assessment_count") ? data.get("assessment_count") : 0, 
              data.containsKey("rubric_count") ? data.get("rubric_count") : 0,
              data.containsKey("course_count") ? data.get("course_count") : 0, 
              data.containsKey("unit_count") ? data.get("unit_count") : 0,
              data.containsKey("lesson_count") ? data.get("lesson_count") : 0,
              data.containsKey("signature_resource_count") ? data.get("signature_resource_count") : 0,
              data.containsKey("signature_collection_count") ? data.get("signature_collection_count") : 0,
              data.containsKey("signature_assessment_count") ? data.get("signature_assessment_count") : 0, 
              data.containsKey("resource") ? data.get("resource") : null,
              data.containsKey("question") ? data.get("question") : null, 
              data.containsKey("collection") ? data.get("collection") : null,
              data.containsKey("assessment") ? data.get("assessment") : null, 
              data.containsKey("rubric") ? data.get("rubric") : null,
              data.containsKey("course") ? data.get("course") : null, 
              data.containsKey("unit") ? data.get("unit") : null,
              data.containsKey("lesson") ? data.get("lesson") : null,
              data.get(EntityAttributeConstants.ID));
      db.commitTransaction();
      LOGGER.info("Successfully populated LM for code : {}", data.get(EntityAttributeConstants.ID));
    } catch (Throwable e) {
      db.rollbackTransaction();
      LOGGER.error("LMRI:updateRQCACULLearningMaps: Caught exception. need to rollback and abort", e);
    } finally {
      db.close();
    }
  }
  
}
