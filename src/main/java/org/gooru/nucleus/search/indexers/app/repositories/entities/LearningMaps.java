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
@Table(SchemaConstants.LEARNING_MAP_STATS)
@IdName(SchemaConstants.ID)
public class LearningMaps extends Model {
  public final static String INSERT_QUERY = "insert into learning_map_stats (id,resource,question,collection,assessment,rubric"
          + ") values (?,?,?,?,?,?) ON CONFLICT ON CONSTRAINT lms_pkey DO UPDATE SET resource = ?,question = ?,collection = ?,assessment = ?,rubric = ?";

  public final static String INSERT_CUL_QUERY =
          "insert into learning_map_stats (id,course,unit,lesson, signature_resource, signature_collection, signature_assessment"
                  + ") values (?,?,?,?,?,?,?) ON CONFLICT ON CONSTRAINT lms_pkey DO UPDATE SET course = ?,unit = ?,lesson = ?, signature_resource = ?, signature_collection = ?, signature_assessment = ?";

  public final static String UPDATE_RQCA_QUERY = "UPDATE learning_map_stats SET resource = ?, question = ?, collection = ?, assessment = ?, rubric = ? WHERE id = ?";

  public final static String UPDATE_CUL_QUERY = "UPDATE learning_map_stats SET course = ?, unit = ?, lesson = ?, signature_resource = ?, signature_collection = ?, signature_assessment = ? WHERE id = ?";

  public final static String UPDATE_RQCACUL_QUERY = "UPDATE learning_maps SET resource_count = ?, question_count = ?, collection_count = ?, assessment_count = ?, rubric_count = ?, course_count = ?, unit_count = ?, lesson_count = ?, signature_resource_count = ?, signature_collection_count = ?, signature_assessment_count = ?,"
          + "resource = to_json(?::json), question = to_json(?::json), collection = to_json(?::json), assessment = to_json(?::json), rubric = to_json(?::json), course = to_json(?::json), unit = to_json(?::json), lesson = to_json(?::json) WHERE id = ?";

}
