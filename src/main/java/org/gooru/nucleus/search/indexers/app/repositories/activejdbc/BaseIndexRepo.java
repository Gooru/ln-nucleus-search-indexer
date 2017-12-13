package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.Collection;
import java.util.Iterator;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.javalite.activejdbc.DB;

public class BaseIndexRepo {

  protected DB getDefaultDataSourceDBConnection(){
    return new DB(DataSourceRegistry.getInstance().getDefaultDatabase());
  }
  
  protected void openConnection(DB db){
    db.open(DataSourceRegistry.getInstance().getDefaultDataSource());
  }
  
  protected void closeDBConn(DB db){
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
}
