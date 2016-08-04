package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;

import java.util.Map;

public class BaseIndexHandler {

  protected void handleCount(String resourceId, String field, String operationType, int count, Map<String, Object> scoreValues,
                             Map<String, Object> fieldsMap) {
    Object value =  scoreValues.get(field);
    String fieldName = ScoreConstants.STATISTICS_FIELD + '.' + field;

    if (operationType.equalsIgnoreCase(ScoreConstants.OPERATION_TYPE_INCR)) {
      Object incrCount = incrementValue(value == null ? 0 : value);
      scoreValues.put(field, incrCount);
      fieldsMap.put(fieldName, incrCount);
    } else if (operationType.equalsIgnoreCase(ScoreConstants.OPERATION_TYPE_DECR)) {
      Object decCount  = decrementValue(value);
      scoreValues.put(field, decCount);
      fieldsMap.put(fieldName, decCount);
    } else if (operationType.equalsIgnoreCase(ScoreConstants.OPERATION_TYPE_UPDATE)) {
      scoreValues.put(field, count);
      fieldsMap.put(fieldName, count);
    }
  }

  protected Object incrementValue(Object value) {
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
