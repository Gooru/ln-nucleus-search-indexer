package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;

import java.util.Map;

public class BaseIndexHandler {

  protected void handleCount(String resourceId, String field, String operationType, int count, Map<String, Object> scoreValues,
                             Map<String, Object> fieldsMap) {
    Object value =  scoreValues.get(field);
    if (operationType.equalsIgnoreCase(ScoreConstants.OPERATION_TYPE_INCR)) {
      scoreValues.put(field, incrementValue(value));
    } else if (operationType.equalsIgnoreCase(ScoreConstants.OPERATION_TYPE_DECR)) {
      scoreValues.put(field, decrementValue(value));
    } else if (operationType.equalsIgnoreCase(ScoreConstants.OPERATION_TYPE_UPDATE)) {
      scoreValues.put(field, count);
    }
    String fieldName = ScoreConstants.STATISTICS_FIELD + '.' + field;
    fieldsMap.put(fieldName, value);
  }

  private Object incrementValue(Object value) {
    if (value != null) {
      if(value instanceof Integer){
        value = ((Integer)value ) + 1;
      }
      if(value instanceof Long){
        value = ((Long)value) + 1;
      }
      return value;
    }
    return 0;
  }

  private Object decrementValue(Object value) {
    if (value != null) {
      if(value instanceof Integer){
        value = ((Integer)value ) - 1;
      }
      if(value instanceof Long){
        value = ((Long)value) - 1;
      }
      return value;
    }
    return 0;
  }


}
