package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

public interface CompetencyContentMapRepository {

    static CompetencyContentMapRepository instance() {
        return new CompetencyContentMapRepositoryImpl();
    }
        
    void insertToCompetencyContentMap(List<Map<String, Object>> dataAsList);

    Boolean contentAlreadyExistsInCompetencyContentMap(String id, String contentType);

}
