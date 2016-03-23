package org.gooru.nucleus.search.indexers.app.processors;

import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.gooru.nuclues.search.indexers.app.utils.IndexNameHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

class MessageProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);

  // collect all failed event transmissions in this logger....
  private static final Logger TRANSMIT_FAIL_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.transmission-errors");

  private final JsonObject eventBody;
  
  private final String eventName;
 
  
  public MessageProcessor(JsonObject message, String operationName) {
    this.eventBody = message;
    // need to revisit once verified sample event data.
    this.eventName = operationName;
  }

  @Override
  public void process() {
    try {
      if (eventBody == null || eventName == null) {
        LOGGER.error("Invalid message received, either null or body of message is not JsonObject ");
        throw new InvalidRequestException();
      }
      
      LOGGER.debug("Event name : " + eventName);
      LOGGER.debug("Event body Json : " +eventBody.toString());

      switch (eventName) {
        case EventsConstants.EVT_RES_CREATE:
        case EventsConstants.EVT_RES_UPDATE:
        case EventsConstants.EVT_RES_COPY:
          processEventResourceCreateUpdateCopy();
          break;

        case EventsConstants.EVT_RES_DELETE:
        	// process resource delete
          break;

        case EventsConstants.EVT_QUESTION_CREATE:
        case EventsConstants.EVT_QUESTION_UPDATE:
        case EventsConstants.EVT_QUESTION_COPY:
        	// process question 
          break;

        case EventsConstants.EVT_QUESTION_DELETE:
        	// process question delete
          break;

        case EventsConstants.EVT_COLLECTION_CREATE:
        case EventsConstants.EVT_COLLECTION_UPDATE:
        case EventsConstants.EVT_COLLECTION_COPY:
        	// process collection 
          break;

        case EventsConstants.EVT_ASSESSMENT_CREATE:
        case EventsConstants.EVT_ASSESSMENT_UPDATE:
        case EventsConstants.EVT_ASSESSMENT_COPY:
        	// process assessment
          break;

        case EventsConstants.EVT_USER_CREATE:
        case EventsConstants.EVT_USER_UPDATE:
        	// process user 
          break;

        default:
          LOGGER.error("Invalid operation type passed in, not able to handle");
          throw new InvalidRequestException();
      }
    } catch (InvalidRequestException e) {
      TRANSMIT_FAIL_LOGGER.error((eventBody != null ? eventBody : null).toString());
    }
  }


  private void processEventResourceCreateUpdateCopy() {
	try{
	    JsonObject payLoad = (JsonObject) eventBody.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT);
	    if (payLoad != null) {
	        String contentId = payLoad.getString(EventsConstants.EVT_PAYLOAD_CONTENTID);
	        LOGGER.debug("processEventResourceCreateUpdateCopy: getResource(Id) :" + contentId);
	        ProcessorContext context = new ProcessorContext(contentId, ExecuteOperationConstants.GET_RESOURCE);
	        JsonObject result = RepoBuilder.buildIndexerRepo(context).getResoure();
	        if (result != null) {
		      LOGGER.debug("processEventResourceCreateUpdateCopy: getResource(Id) returned:" + result);
		      IndexService.instance().indexDocuments(contentId, IndexNameHolder.getIndexName(EsIndex.RESOURCE), IndexerConstants.TYPE_RESOURCE, result);
		      LOGGER.debug("Indexed resource! event name : " + eventName + " resource id : " + contentId);
	        }
	    }
	}  
	catch(Exception ex){
	    LOGGER.error("processEventResourceCreateUpdateCopyIndex: Failed to fetch data for resource!! Input data received: " + eventBody + " Exception : "+ex);
	    TRANSMIT_FAIL_LOGGER.error(eventBody.toString());
	}
  }

}
