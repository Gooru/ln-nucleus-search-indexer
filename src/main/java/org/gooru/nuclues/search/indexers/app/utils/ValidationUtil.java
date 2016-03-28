package org.gooru.nuclues.search.indexers.app.utils;

import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class ValidationUtil {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtil.class);

	  private static boolean isNullOrEmpty(String contentId) {
		    if (contentId == null || contentId.isEmpty()) {
		      return true;
		    }
		return false;    
	  }
	  
	  public static boolean rejectIfInvalidEventJson(JsonObject json) throws InvalidRequestException {
		  if(json.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT) == null || json.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).isEmpty()) {
		    	LOGGER.error(ErrorMsgConstants.INVALID_EVENT_JSON  + " Event json : " + json);
		    	throw new InvalidRequestException(ErrorMsgConstants.INVALID_EVENT_JSON);
		  }
		  else if(isNullOrEmpty(json.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getString(EventsConstants.EVT_PAYLOAD_CONTENTID))){
		    	LOGGER.error(ErrorMsgConstants.INVALID_CONTENT_ID +" Event json : " + json);
		    	throw new InvalidRequestException(ErrorMsgConstants.INVALID_CONTENT_ID);
		  }
		  return false;
	  }
	  
	  public static void rejectIfNull(JsonObject json, String msg) throws InvalidRequestException {
		  if(json == null){
			  LOGGER.error(msg);
			  throw new InvalidRequestException(msg);
		  }
	  }
}
