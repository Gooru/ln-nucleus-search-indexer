package org.gooru.nucleus.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.gooru.nucleus.search.indexers.app.repositories.entities.Course;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CourseRepositoryImpl implements CourseRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(CourseRepositoryImpl.class);

  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> getCourse(String courseId) {
    List<Map> resultSet = null;
    Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
    resultSet = Base.findAll(Course.GET_COURSE_QUERY, courseId);
    if (resultSet.size() < 1) {
      LOGGER.warn("Course info for id : {} not present in DB", courseId);
    }
    Base.close();
    return resultSet;
  }

}
