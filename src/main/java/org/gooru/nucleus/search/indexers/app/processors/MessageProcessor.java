package org.gooru.nucleus.search.indexers.app.processors;

import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.processors.event.handlers.EventHandlerBuilder;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

class MessageProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);

  // collect all failed event transmissions in this logger....
  private static final Logger TRANSMIT_FAIL_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");

  private final JsonObject eventBody;
  
  private final String operationName;
 
  
  public MessageProcessor(JsonObject message, String operationName) {
    this.eventBody = message;
    // need to revisit once verified sample event data.
    this.operationName = operationName;
  }

  @Override
  public void process() {
    try {
      if (eventBody == null || operationName == null) {
        LOGGER.error("Invalid message received, either null or body of message is not JsonObject ");
        throw new InvalidRequestException();
      }
      String eventName = eventBody.getString(EventsConstants.EVT_PAYLOAD_EVENT_NAME);
      LOGGER.debug("Event name : " + operationName);
      LOGGER.debug("Event body Json : " +eventBody.toString());

      switch (eventName) {
        case EventsConstants.EVT_RES_CREATE:
        case EventsConstants.EVT_RES_UPDATE:
        case EventsConstants.EVT_RES_DELETE:
        case EventsConstants.EVT_QUESTION_CREATE:
        case EventsConstants.EVT_QUESTION_UPDATE:
        case EventsConstants.EVT_QUESTION_DELETE:        	
        	processResourceEvents();
        	break;
          
        case EventsConstants.EVT_COLLECTION_CREATE:
        case EventsConstants.EVT_COLLECTION_UPDATE:
        case EventsConstants.EVT_ASSESSMENT_CREATE:
        case EventsConstants.EVT_ASSESSMENT_UPDATE:
        case EventsConstants.EVT_COLLECTION_DELETE:	
        case EventsConstants.EVT_ASSESSMENT_DELETE:	
        	processCollectionEvents();
        	break;
        	
        case EventsConstants.EVT_QUESTION_COPY:
        case EventsConstants.EVT_COLLECTION_COPY:
        case EventsConstants.EVT_ASSESSMENT_COPY:
        	processContentCopyEvents();
			break;	
        
        case EventsConstants.EVT_USER_CREATE:
        case EventsConstants.EVT_USER_UPDATE:
        	processUserEvents(); 
            break;
          
        case EventsConstants.EVT_COLLABORATOR_UPDATE_ASSESSMENT:
        case EventsConstants.EVT_COLLABORATOR_UPDATE_COLLECTION:
        case EventsConstants.EVT_ASSESSMENT_QUESTION_ADD:
        case EventsConstants.EVT_COLLECTION_CONTENT_ADD:	
        	processContentCopyEvents();
        	break;
    
        default:
          LOGGER.error("Invalid operation type passed in, not able to handle");
          throw new InvalidRequestException();
      }
    } catch (InvalidRequestException e) {
      TRANSMIT_FAIL_LOGGER.error((eventBody != null ? eventBody : null).toString());
    }
  }

	private void processUserEvents() {
		EventHandlerBuilder.buildUserHandler(eventBody).handleEvents();
	}
	
	private void processCollectionEvents() {
		EventHandlerBuilder.buildCollectionHandler(eventBody).handleEvents();
	}
	
	private void processContentCopyEvents() {
		EventHandlerBuilder.buildContentCopyHandler(eventBody).handleEvents();
	}
	
	private void processResourceEvents() {
		EventHandlerBuilder.buildResourceHandler(eventBody).handleEvents();
	}

}
