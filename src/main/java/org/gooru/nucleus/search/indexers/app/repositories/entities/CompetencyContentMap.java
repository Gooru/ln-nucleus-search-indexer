package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.gooru.nucleus.search.indexers.app.constants.SchemaConstants;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author Renuka
 */
@DbName(SchemaConstants.DEFAULT_DATABASE_NAME)
@Table(SchemaConstants.COMPETENCY_CONTENT_MAP)
@IdName(SchemaConstants.ID)
public class CompetencyContentMap extends Model {
    
    public final static String INSERT_CCM = "insert into competency_content_map (subject, course, domain, competency, micro_competency, "
        + "content_type, item_id, item_count, is_published, is_featured, primary_language) values (?,?,?,?,?,?,?,?,?,?,?) ON CONFLICT DO NOTHING";
    
    public final static String FETCH_EXISTING_CONTENT = "select id from competency_content_map where content_type = ? AND item_id = ?";


}
