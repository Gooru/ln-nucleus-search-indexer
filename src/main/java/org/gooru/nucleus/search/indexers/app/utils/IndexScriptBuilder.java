package org.gooru.nucleus.search.indexers.app.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public final class IndexScriptBuilder {

  protected static final String CTX_SOURCE = "ctx._source.";

  protected static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  protected static final String SEMICOLON = ";";

  protected static final String EQUALS = "=";

  protected static final String INDEX_UPDATED_DATE = "indexUpdatedDate";

  protected static final String INDEX_UPDATED =
    "if (ctx._source.containsKey(\"indexUpdatedDate\")) {ctx._source.indexUpdatedDate=indexUpdatedDate} else ({ctx._source" +
      ".indexUpdatedDate=indexUpdatedDate})";

  protected static final String IF_EXISTS_FIELD = "if (ctx._source.";

  protected static final String ELSE_EXISTS_FIELD = " else {ctx._source.";

  protected static final String CONTAINS_KEY = "containsKey";

  protected static final String CLOSE_CURLY_BRACKET = "}";

  protected static final String OPEN_CURLY_BRACKET = "{";

  protected static final String DOT = ".";

  protected static final String CLOSE_BRACKET_DOUBLE_QUOTE = "(\"";

  protected static final String DOUBLE_QUOTES_CLOSE_BRACKETS = "\")) ";

  protected static final Logger LOG = LoggerFactory.getLogger(IndexScriptBuilder.class);

  private IndexScriptBuilder() {
    throw new AssertionError();
  }


  public static void buildScript(final String id, Map<String, Object> paramsField, StringBuffer scriptQuery,
                                 final Map<String, Object> fieldValueMap) {
    int i = 0;
    for (String field : fieldValueMap.keySet()) {
      String paramKey = field.replaceAll("\\.", "");
      Object value = fieldValueMap.get(field);
      if (value != null) {
        if (i == 0) {
          paramsField.put(INDEX_UPDATED_DATE, new SimpleDateFormat(DATE_FORMAT).format(new Date()));
          scriptQuery.append(INDEX_UPDATED);
        }
        paramsField.put(paramKey, value);
        createScript(field, scriptQuery);
        ++i;
      }
    }

  }

  private static void createScript(String key, StringBuffer scriptQuery) {

    String paramKey = key.replaceAll("\\.", "");

    scriptQuery.append(SEMICOLON);
    scriptQuery.append(IF_EXISTS_FIELD);
    String childField = key;
    String parentField = "";
    if (key.lastIndexOf(DOT) != -1) {
      parentField = key.substring(0, key.lastIndexOf(DOT));
      childField = key.substring(key.lastIndexOf(DOT) + 1, key.length());
    }
    if (parentField.trim().length() > 0) {
      scriptQuery.append(parentField + DOT);
    }
    scriptQuery.append(CONTAINS_KEY);
    scriptQuery.append(CLOSE_BRACKET_DOUBLE_QUOTE);
    scriptQuery.append(childField);
    scriptQuery.append(DOUBLE_QUOTES_CLOSE_BRACKETS);
    scriptQuery.append(OPEN_CURLY_BRACKET + CTX_SOURCE + key + EQUALS + paramKey + CLOSE_CURLY_BRACKET);
    scriptQuery.append(ELSE_EXISTS_FIELD);
    scriptQuery.append(key + EQUALS + paramKey + CLOSE_CURLY_BRACKET);
  }


}
