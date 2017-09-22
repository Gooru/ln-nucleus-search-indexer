package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import org.gooru.nucleus.search.indexers.app.repositories.entities.Unit;
import org.javalite.activejdbc.LazyList;

import io.vertx.core.json.JsonObject;

public interface UnitRepository {

  static UnitRepository instance() {
    return new UnitRepositoryImpl();
  }
  JsonObject getUnit(String unitId);
    
  JsonObject getDeletedUnit(String unitId);
  
  Integer getUnitCount(String courseId);
  
  JsonObject getUnitById(String unitId);
  
  LazyList<Unit> getUnitByCourseId(String courseId);
  
}

