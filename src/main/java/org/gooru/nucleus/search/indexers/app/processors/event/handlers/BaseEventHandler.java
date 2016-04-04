package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import org.gooru.nucleus.search.indexers.app.constants.EventsConstants;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandler;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandlerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class BaseEventHandler {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(IndexEventHandler.class);
	public static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");

	protected IndexHandler getResourceIndexHandler(){
		return IndexHandlerBuilder.buildResourceIndexHandler();
	}

	protected IndexHandler getCollectionIndexHandler(){
		return IndexHandlerBuilder.buildCollectionIndexHandler();
	}
    
	protected static JsonObject getPayLoadObj(JsonObject json){
		return json.getJsonObject(EventsConstants.EVT_PAYLOAD_OBJECT);
	}

	protected static String getPayLoadObjContentFormat(JsonObject json){
		return getPayLoadObj(json).getString(EventsConstants.EVT_PAYLOAD_CONTENT_FORMAT);
	}

	protected static JsonObject getPayLoadTargetObj(JsonObject json){
		return getPayLoadObj(json).getJsonObject(EventsConstants.EVT_PAYLOAD_TARGET);
	}

	protected static JsonObject getPayLoadSourceObj(JsonObject json){
		return getPayLoadObj(json).getJsonObject(EventsConstants.EVT_PAYLOAD_SOURCE);
	}
	
	protected static String getParentGooruIdSourceObj(JsonObject json){
		return getPayLoadSourceObj(json).getString(EventsConstants.EVT_PAYLOAD_PARENT_GOORU_ID);
	}

	protected static String getParentGooruIdTargetObj(JsonObject json){
		return getPayLoadTargetObj(json).getString(EventsConstants.EVT_PAYLOAD_PARENT_GOORU_ID);
	}
	
	protected static String getOriginalContentIdSourceObj(JsonObject json){
		return getPayLoadSourceObj(json).getString(EventsConstants.EVT_PAYLOAD_ORIGINAL_CONTENT_ID);
	}

	protected static String getOriginalContentIdTargetObj(JsonObject json){
		return getPayLoadTargetObj(json).getString(EventsConstants.EVT_PAYLOAD_ORIGINAL_CONTENT_ID);
	}
	
	protected static String getParentContentIdSourceObj(JsonObject json){
		return getPayLoadSourceObj(json).getString(EventsConstants.EVT_PAYLOAD_PARENT_CONTENT_ID);
	}

	protected static String getParentContentIdTargetObj(JsonObject json){
		return getPayLoadTargetObj(json).getString(EventsConstants.EVT_PAYLOAD_PARENT_CONTENT_ID);
	}

	protected static JsonArray getCollaborators(JsonObject json){
		return getPayLoadObj(json).getJsonArray(EventsConstants.EVT_PAYLOAD_COLLABORATORS);
	}
}
