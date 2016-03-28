package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandler;
import org.gooru.nucleus.search.indexers.app.processors.index.handlers.IndexHandlerBuilder;
import org.gooru.nuclues.search.indexers.app.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class BaseEventHandler {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(IndexEventHandler.class);
	public static final Logger INDEX_FAILURES_LOGGER = LoggerFactory.getLogger("org.gooru.nucleus.index.failures");

	protected static void rejectIfInvalidEventJson(JsonObject eventJson) {
		ValidationUtil.rejectIfInvalidEventJson(eventJson);
	}
	
	protected IndexHandler getResourceIndexHandler(){
		return IndexHandlerBuilder.buildResourceIndexHandler();
	}

	protected IndexHandler getCollectionIndexHandler(){
		return IndexHandlerBuilder.buildCollectionIndexHandler();
	}

	
}
