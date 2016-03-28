package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandler;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nuclues.search.indexers.app.utils.ValidationUtil;

import io.vertx.core.json.JsonObject;

public class ResourceEventsHandler extends BaseEventHandler implements IndexEventHandler {

	private final JsonObject eventJson;
    private String eventName;
	private String resourceId;
	private IndexHandler resourceIndexHandler;
	private IndexHandler collectionIndexHandler;
	
	public ResourceEventsHandler(JsonObject eventJson) {
		this.eventJson = eventJson;
		this.collectionIndexHandler = getCollectionIndexHandler();
		this.resourceIndexHandler = getResourceIndexHandler();
	}

	@Override
	public void handleEvents() {
		try{
			rejectIfInvalidEventJson(eventJson);
			eventName = eventJson.getString(EventsConstants.EVT_PAYLOAD_EVENT_NAME);
			LOGGER.debug("REH->handleEvents : Event validation passed, proceding to handle consumed event : " +eventName);
			resourceId = eventJson.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT).getString(EventsConstants.EVT_PAYLOAD_CONTENTID);
			
			if(eventName.equalsIgnoreCase(EventsConstants.EVT_RES_DELETE) || eventName.equalsIgnoreCase(EventsConstants.EVT_QUESTION_DELETE)){
				resourceIndexHandler.deleteIndexedDocument(resourceId);
			    LOGGER.debug("REH->handleEvents : Deleted resource from index! event name : " + eventName + " resource id : " + resourceId);
			    handlePostDelete();
			}
			else {
				resourceIndexHandler.indexDocument(resourceId);
			    LOGGER.debug("REH->handleEvents : Indexed resource! event name : " + eventName + " resource id : " + resourceId);
			}
		}
		catch(Exception ex){
			LOGGER.error("REH->handleEvents : Index failed !! event name : " + eventName +" Event data received : " +(eventJson == null ? eventJson : eventJson.toString()) + " Exception : " +ex);
			INDEX_FAILURES_LOGGER.error("Re-index failed for resource. Event name : " + eventName +" Event json : " + (eventJson == null ? eventJson : eventJson.toString()) + " Exception :" +ex);
		}
	}
	
	private void handlePostDelete(){
		try{
			LOGGER.debug("REH->handlePostDelete : Proceding to index collection/assessment that had mapped to resource : " +resourceId);
			ProcessorContext context = new ProcessorContext(resourceId, ExecuteOperationConstants.GET_COLLECTION_IDS);
			JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
			ValidationUtil.rejectIfNull(result, ErrorMsgConstants.COLLECTION_DATA_NULL);
			LOGGER.debug("REH->handlePostDelete : Fetched collection data from DB, json : "+result.toString()+ " Calling index service !!");
			collectionIndexHandler.indexDocuments(result);
		}
		catch(Exception ex){
			LOGGER.error("REH->handlePostDelete : Failed to re-index associated colelctions/assessments that is mapped to this resource : " + resourceId);
			INDEX_FAILURES_LOGGER.error("Failed to re-index associated colelctions/assessments that is mapped to this resource : " + resourceId);
		}
	}
}
