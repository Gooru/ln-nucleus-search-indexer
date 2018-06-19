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
@Table(SchemaConstants.LIBRARY)
@IdName(SchemaConstants.ID)
public class Library extends Model {

    public static final String FETCH_LIBRARY_CONTENT = "select * from library l inner join library_content lc on l.id = lc.library_id where lc.content_id = ?::uuid";
    
}
