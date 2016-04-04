package org.gooru.nucleus.search.indexers.app.processors;

import org.gooru.nucleus.search.indexers.app.constants.ContentFormat;
import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.processors.event.handlers.EventHandlerBuilder;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

class MessageProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);

  // collect all failed event transmissions in this logger....
  private static final Logger TRANSMIT_FAIL_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");

  private final JsonObject eventBody;
  
  public MessageProcessor(JsonObject message) {
    this.eventBody = message;
  }

  @Override
  public void process() {
    try {
      ValidationUtil.rejectIfInvalidEventJson(eventBody);
      String eventName = eventBody.getString(EventsConstants.EVT_OBJECT_EVENT_NAME);
      String contentFormat = eventBody.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getString(EventsConstants.EVT_PAYLOAD_CONTENT_FORMAT);
      LOGGER.debug("Event name : " + eventName + " Content Format : " + contentFormat);
      LOGGER.debug("Event body Json : " +eventBody.toString());
      
      if(contentFormat.equalsIgnoreCase(ContentFormat.QUESTION.name()) || contentFormat.equalsIgnoreCase(ContentFormat.RESOURCE.name())){
    	  processResourceEvents();
      }else if(contentFormat.equalsIgnoreCase(ContentFormat.ASSESSMENT.name()) || contentFormat.equalsIgnoreCase(ContentFormat.COLLECTION.name())){
    	  processCollectionEvents();
      }
      else{
          LOGGER.error("Invalid content type passed in, not able to handle. Event name : " +eventName);
          throw new InvalidRequestException();
      }
    } catch (InvalidRequestException e) {
      TRANSMIT_FAIL_LOGGER.error((eventBody != null ? eventBody : null).toString());
    }
  }

  // This method to handle user related events 
	private void processUserEvents() {
		EventHandlerBuilder.buildUserHandler(eventBody).handleEvents();
	}
	
	private void processCollectionEvents() {
		EventHandlerBuilder.buildCollectionHandler(eventBody).handleEvents();
	}
	
	private void processResourceEvents() {
		EventHandlerBuilder.buildResourceHandler(eventBody).handleEvents();
	}

}
