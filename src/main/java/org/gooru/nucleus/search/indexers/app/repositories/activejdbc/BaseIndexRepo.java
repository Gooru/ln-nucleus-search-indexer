package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.javalite.activejdbc.DB;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseIndexRepo {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseIndexRepo.class);

  protected DB getDefaultDataSourceDBConnection(){
    return new DB(DataSourceRegistry.getInstance().getDefaultDatabase());
  }
  
  protected void openDefaultDBConnection(DB db){
    db.open(DataSourceRegistry.getInstance().getDefaultDataSource());
  }
  
  protected void closeDefaultDBConn(DB db){
    db.close();
  }

    protected DB getTrackerDataSourceDBConnection() {
        return new DB(DataSourceRegistry.getInstance().getIndexTrackerDatabase());
    }

    protected void openTrackerDBConnection(DB db) {
        db.open(DataSourceRegistry.getInstance().getIndexTrackerDataSource());
    }

    protected void closeTrackerDBConn(DB db) {
        db.close();
    }
    
  public static String toPostgresArrayString(Collection<String> input) {
      int approxSize = ((input.size() + 1) * 36); // Length of UUID is around
                                                  // 36 chars
      Iterator<String> it = input.iterator();
      if (!it.hasNext()) {
          return "{}";
      }

      StringBuilder sb = new StringBuilder(approxSize);
      sb.append('{');
      for (;;) {
          String s = it.next();
          sb.append('"').append(s).append('"');
          if (!it.hasNext()) {
              return sb.append('}').toString();
          }
          sb.append(',');
      }
  }
  
  public PGobject getPGObject(String field, String type, String value) {
    PGobject pgObject = new PGobject();
    pgObject.setType(type);
    try {
      pgObject.setValue(value);
      return pgObject;
    } catch (SQLException e) {
      LOGGER.error("Not able to set value for field: {}, type: {}, value: {}", field, type, value);
      return null;
    }
  }
}
