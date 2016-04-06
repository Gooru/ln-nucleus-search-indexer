package org.gooru.nucleus.search.indexers.app.utils;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ValidationUtil {

  public static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtil.class);

  private ValidationUtil() {
    throw new AssertionError();
  }

  private static boolean isNullOrEmpty(String contentId) {
    return contentId == null || contentId.isEmpty();
  }

  public static void rejectIfInvalidEventJson(JsonObject json) throws InvalidRequestException {
    if (json == null || json.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT) == null ||
      json.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT) == null || json.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).isEmpty()) {
      LOGGER.error(ErrorMsgConstants.INVALID_EVENT_JSON + " Event json : " + json);
      throw new InvalidRequestException(ErrorMsgConstants.INVALID_EVENT_JSON);
    } else if (isNullOrEmpty(json.getJsonObject(EventsConstants.EVT_CONTEXT_OBJECT).getString(EventsConstants.EVT_CONTEXT_CONTENT_ID))) {
      LOGGER.error(ErrorMsgConstants.INVALID_CONTENT_ID + " Event json : " + json);
      throw new InvalidRequestException(ErrorMsgConstants.INVALID_CONTENT_ID);
    }
  }

  public static void rejectIfNull(JsonObject json, String msg) throws InvalidRequestException {
    if (json == null) {
      LOGGER.error(msg);
      throw new InvalidRequestException(msg);
    }
  }

  public static void rejectIfInvalidJsonCopyEvent(JsonObject json) throws InvalidRequestException {
    JsonObject targetObj = json.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getJsonObject(EventsConstants.EVT_PAYLOAD_TARGET);
    if (targetObj == null || originalContentIdIsNull(targetObj) || parentContentIdIsNull(targetObj)) {
      LOGGER.error(ErrorMsgConstants.INVALID_COPY_EVENT_JSON + " Event json : " + json);
      throw new InvalidRequestException(ErrorMsgConstants.INVALID_COPY_EVENT_JSON);
    }
  }

  public static void rejectIfInvalidJsonMoveEvent(JsonObject json) throws InvalidRequestException {
    JsonObject targetObj = json.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getJsonObject(EventsConstants.EVT_PAYLOAD_TARGET);
    JsonObject sourceObj = json.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getJsonObject(EventsConstants.EVT_PAYLOAD_SOURCE);

    if (targetObj == null || sourceObj == null || parentGooruIdIsNull(targetObj) || parentGooruIdIsNull(sourceObj)) {
      LOGGER.error(ErrorMsgConstants.INVALID_COPY_EVENT_JSON + " Event json : " + json);
      throw new InvalidRequestException(ErrorMsgConstants.INVALID_COPY_EVENT_JSON);
    }
  }

  public static void rejectIfInvalidJsonItemAddEvent(JsonObject json) throws InvalidRequestException {
    JsonObject targetObj = json.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getJsonObject(EventsConstants.EVT_PAYLOAD_TARGET);
    if (targetObj == null || parentGooruIdIsNull(targetObj)) {
      LOGGER.error(ErrorMsgConstants.INVALID_ITEM_ADD_EVENT_JSON + " Event json : " + json);
      throw new InvalidRequestException(ErrorMsgConstants.INVALID_ITEM_ADD_EVENT_JSON);
    }
  }

  public static void rejectIfInvalidJsonCollaboratorUpdate(JsonObject json) throws InvalidRequestException {
    JsonArray collaborators = json.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getJsonArray(EventsConstants.EVT_PAYLOAD_COLLABORATORS);
    if (collaborators == null || collaborators.size() < 0) {
      LOGGER.error(ErrorMsgConstants.INVALID_COLLABORATORS_EVENT_JSON + " Event json : " + json);
      throw new InvalidRequestException(ErrorMsgConstants.INVALID_COLLABORATORS_EVENT_JSON);
    }
  }

  private static boolean parentContentIdIsNull(JsonObject targetOrSource) {
    return isNullOrEmpty(targetOrSource.getString(EventsConstants.EVT_PAYLOAD_PARENT_CONTENT_ID));
  }

  private static boolean originalContentIdIsNull(JsonObject targetOrSource) {
    return isNullOrEmpty(targetOrSource.getString(EventsConstants.EVT_PAYLOAD_ORIGINAL_CONTENT_ID));
  }

  private static boolean parentGooruIdIsNull(JsonObject targetOrSource) {
    return isNullOrEmpty(targetOrSource.getString(EventsConstants.EVT_PAYLOAD_PARENT_GOORU_ID));
  }


}
