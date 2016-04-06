package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;

import java.util.Map;

public class BaseIndexHandler {

  protected void handleCount(String resourceId, String field, String operationType, int count, Map<String, Object> scoreValues,
                             Map<String, Object> fieldsMap) {
    int value = (int) scoreValues.get(field);
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

  private int incrementValue(Integer value) {
    if (value != null) {
      return value + 1;
    }
    return 0;
  }

  private int decrementValue(Integer value) {
    if (value != null) {
      return value - 1;
    }
    return 0;
  }


}
