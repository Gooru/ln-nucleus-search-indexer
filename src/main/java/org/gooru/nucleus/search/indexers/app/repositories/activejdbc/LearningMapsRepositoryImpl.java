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

}
