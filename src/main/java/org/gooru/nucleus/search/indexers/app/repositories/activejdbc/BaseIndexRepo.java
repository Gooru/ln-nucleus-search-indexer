package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

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
  
  protected String toPostgresArrayString(String[] input) {
    if (input.length == 0) {
      return "{}";
    }

    StringBuilder sb = new StringBuilder();
    sb.append('{');
    int count = 1;
    for (String code : input) {
      sb.append('"').append(code).append('"');
      if (count == input.length) {
        return sb.append('}').toString();
      }
      sb.append(',');
      count++;
    }

    return null;
  }
}
