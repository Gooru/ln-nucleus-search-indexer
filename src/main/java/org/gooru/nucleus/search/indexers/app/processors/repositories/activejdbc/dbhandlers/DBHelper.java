package org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc.dbhandlers;

public final class DBHelper {

  private DBHelper() {
    // TODO Auto-generated constructor stub
  }

  public static DBHelper getInstance() {
    return Holder.INSTANCE;
  }


  private static final class Holder {
    private static final DBHelper INSTANCE = new DBHelper();
  }

}
