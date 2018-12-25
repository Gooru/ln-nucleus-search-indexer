package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.repositories.entities.CompetencyContentMap;
import org.javalite.activejdbc.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompetencyContentMapRepositoryImpl extends BaseIndexRepo implements CompetencyContentMapRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompetencyContentMapRepositoryImpl.class);

    @Override
    public void insertToCompetencyContentMap(List<Map<String, Object>> dataAsList) {
        DB db = getDefaultDataSourceDBConnection();
        try {
            openDefaultDBConnection(db);
            PreparedStatement ps = db.startBatch(CompetencyContentMap.INSERT_CCM);
            dataAsList.forEach(m -> {
                Map<String, Object> dataMap = (Map<String, Object>) m;
                db.addBatch(ps, dataMap.get("subject"), dataMap.get("course"), dataMap.get("domain"),
                    dataMap.get("competency"), dataMap.get("micro-competency") == null ? null : dataMap.get("micro-competency") , dataMap.get("content_type"),
                    dataMap.get("item_id"), (Integer) dataMap.get("item_count"),  (Boolean) dataMap.get("is_published"),
                    Boolean.valueOf(dataMap.get("is_featured").toString()), (Integer) dataMap.get("primary_language"));
            });
            db.executeBatch(ps);
            db.commitTransaction();
        } catch (Exception ex) {
            LOGGER.error("CCMRL:insertToCompetencyContentMap: Failed to insert competency_content_map : EX : {} ",
                 ex);
        } finally {
            closeDefaultDBConn(db);
        }
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Boolean contentAlreadyExistsInCompetencyContentMap(String id, String contentType) {
        DB db = getDefaultDataSourceDBConnection();
        try {
            openDefaultDBConnection(db);
            List<Map> result = db.findAll(CompetencyContentMap.FETCH_EXISTING_CONTENT, contentType, id);
            if (result != null && result.size() > 0) {
                return true;
            }
        } catch (Exception ex) {
            LOGGER.error("CCMRL:contentAlreadyExistsInCompetencyContentMap: Failed to fetch competency_content_map content for id : {} : EX : {} ",
                id, ex);
        } finally {
            closeDefaultDBConn(db);
        }
        return false;
    }

}
