package org.gooru.nucleus.search.indexers.app.processors.event.handlers;

import io.vertx.core.json.JsonObject;

public final class EventHandlerBuilder {
	
	public static IndexEventHandler buildCollectionHandler(JsonObject eventJson){
		return new CollectionEventsHandler(eventJson);
	}
	
	public static IndexEventHandler buildResourceHandler(JsonObject eventJson){
		return new ResourceEventsHandler(eventJson);
	}

	public static IndexEventHandler buildContentCopyHandler(JsonObject eventJson){
		return new QuestionCopyEventsHandler(eventJson);
	}

	public static IndexEventHandler buildContentCounterHandler(JsonObject eventJson){
		return new ContentCounterEventsHandler(eventJson);
	}

	public static IndexEventHandler buildUserHandler(JsonObject eventJson){
		return new UserEventsHandler(eventJson);
	}

}
