package org.gooru.nucleus.search.indexers.app.repositories.entities;

import org.gooru.nucleus.search.indexers.app.constants.SchemaConstants;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author Renuka
 */
@DbName(SchemaConstants.DEFAULT_DATASCOPE_DATABASE_NAME)
@Table(SchemaConstants.CONTENT_VECTORS)
@IdName(SchemaConstants.ID)
public class ContentVectors extends Model {

  public static final String FETCH_CONTENT_VECTORS =
    "SELECT efficacy, engagement, relevance from content_vectors WHERE content_id = ?::uuid ";

  public static final String FETCH_COLLECTION_VECTORS =
    "SELECT efficacy, engagement, relevance from collection_vectors WHERE content_id = ?::uuid ";

  public static final String FETCH_COURSE_VECTORS =
    "SELECT efficacy, engagement, relevance from course_vectors WHERE content_id = ?::uuid ";

  public static final String FETCH_UNIT_VECTORS =
    "SELECT efficacy, engagement, relevance from unit_vectors WHERE content_id = ?::uuid ";

  public static final String FETCH_LESSON_VECTORS =
    "SELECT efficacy, engagement, relevance from lesson_vectors WHERE content_id = ?::uuid ";

}
