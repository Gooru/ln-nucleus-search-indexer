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
@Table(SchemaConstants.GUT_COMPETENCY_PREREQUISITE)
@IdName(SchemaConstants.GUT_COMPETENCY_ID)
public class GutCompetencyPrerequisite extends Model {
  public static final String SELECT_GUT_COMPETENCY_PREREQUISITE = "gut_competency_id = ?";
  public static final String PREREQUISITE_GUT_COMPETENCY_ID = "prerequisite_gut_competency_id";
}
