package org.gooru.nucleus.search.indexers.bootstrap;

import org.gooru.nucleus.search.indexers.app.constants.RouteConstants;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.ext.web.Router;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;

public class IndexBuilderVerticle extends AbstractVerticle {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexBuilderVerticle.class);
	
	@Override
	public void start() throws Exception {

	    LOGGER.info("Starting IndexBuilderVerticle...");

	    final HttpServer httpServer = vertx.createHttpServer();

	    final Router router = Router.router(vertx);
        
	    router.post(RouteConstants.EP_BUILD_INDEX).handler(context -> vertx.executeBlocking(future -> {
	        String indexableIds = context.request().getParam(RouteConstants.INDEXABLE_IDS);
	        String contentFormat = context.request().getParam(RouteConstants.CONTENT_FORMAT);
	        if(indexableIds != null && contentFormat != null){
	        	try{
		        	IndexService.instance().buildIndex(indexableIds, contentFormat);
		        	future.complete("Indexed");
	        	}
	        	catch(Exception e){
	        		future.fail(e);
	        	}
	        }
	    }, result -> {
	    	if(result.succeeded()){
	    		context.response().setStatusCode(200).end();
	    	}
	    	else {
	    		LOGGER.error("Re-index failed !!!");
	    		context.response().setStatusCode(500).end();
	    	}
	    }));
	    
	    // If the port is not present in configuration then we end up
	    // throwing as we are casting it to int. This is what we want.
	    final int port = config().getInteger(RouteConstants.HTTP_PORT);
	    LOGGER.info("Http server starting on port {}", port);
	    httpServer.requestHandler(router::accept).listen(port, result -> {
	      if (result.succeeded()) {
	        LOGGER.info("HTTP Server started successfully");
	      } else {
	        // Can't do much here, Need to Abort. However, trying to exit may have us blocked on other threads that we may have spawned, so we need to use
	        // brute force here
	    	LOGGER.error("Not able to start HTTP Server", result.cause());
	        Runtime.getRuntime().halt(1);
	      }
	    });

	}
}
