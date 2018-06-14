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
@Table(SchemaConstants.MACHINE_CLASSIFIED_CONTENTS)
@IdName(SchemaConstants.ID)
public class MachineClassifiedContents extends Model {
  
  public final static String INSERT_QUERY_DOMAINS = "insert into machine_classified_content(id, machine_classified_domains) values (?, ?::text[]) ON CONFLICT (id) DO UPDATE SET  machine_classified_domains = ?::text[]";

  public final static String INSERT_QUERY_TAGS = "insert into machine_classified_content(id, machine_classified_tags) values (?, ?::text[]) ON CONFLICT (id) DO UPDATE SET  machine_classified_tags = ?::text[]";

  public final static String FETCH_DOMAIN_CLASSIFICATION = "id = ?::uuid AND (machine_classified_domains is not null AND machine_classified_domains <> '{}')";

  public final static String FETCH_STANDARD_CLASSIFICATION = "id = ?::uuid AND (machine_classified_tags is not null AND machine_classified_tags <> '{}')";

}
