package org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc.dbhandlers;

import java.util.Collection;
import java.util.Set;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.ModelDelegate;
import org.javalite.common.Escape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DBHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(DBHelper.class);

  private DBHelper() {
    // TODO Auto-generated constructor stub
  }

  public static DBHelper getInstance() {
    return Holder.INSTANCE;
  }


  private static final class Holder {
    private static final DBHelper INSTANCE = new DBHelper();
  }
  
  public <T extends Model> void escapeSplChars(T model) {
    String[] names;
    Set<String> attributeNamesAll = ModelDelegate.attributeNames(model.getClass());
    names = lowerCased(attributeNamesAll);
    
    for (int i = 0; i < names.length; i++) {
      String name = names[i];
      Object v = model.get(name);
      if (v instanceof String){
        try {
          model.set(name, Escape.json(String.valueOf(v)));
        } catch (Exception e) {
            LOGGER.warn("Failed to parse value of field '{}', will use default string without conversion ", name);
            model.set(name, String.valueOf(v));
        }
      } 
    }
  }

private static String[] lowerCased(Collection<String> collection) {
  String[] array = new String[collection.size()];
  int i = 0;
  for (String elem : collection) {
    array[i++] = elem.toLowerCase();
  }
  return array;
}


}
