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
@Table(SchemaConstants.GOORU_LANGUAGE)
@IdName(SchemaConstants.ID)
public class Language extends Model {
        
    public static final String FETCH_LANGUAGE_CODE = "SELECT gooru_language.id, gooru_language.name, STRING_AGG(p_language.code, ',') as code FROM content c inner join gooru_language on c.primary_language = gooru_language.id JOIN  p_language ON p_language.id = ANY(gooru_language.language_id) where gooru_language.id = ? group by gooru_language.id,gooru_language.name";

}
