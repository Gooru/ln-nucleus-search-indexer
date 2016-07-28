package org.gooru.nucleus.search.indexers.bootstrap;

import org.gooru.nucleus.search.indexers.app.constants.RouteConstants;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class IndexBuilderVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexBuilderVerticle.class);

  @Override
  public void start() throws Exception {

    LOGGER.info("Starting IndexBuilderVerticle...");

    final HttpServer httpServer = vertx.createHttpServer();

    final Router router = Router.router(vertx);

    index(router);
    indexContentInfo(router);
    markBrokenStatus(router);
    markUnBrokenStatus(router);
    
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

  private void index(final Router router) {
    router.post(RouteConstants.EP_BUILD_INDEX).handler(context -> vertx.executeBlocking(future -> {
      String indexableIds = context.request().getParam(RouteConstants.INDEXABLE_IDS);
      String contentFormat = context.request().getParam(RouteConstants.CONTENT_FORMAT);
      if (indexableIds != null && contentFormat != null) {
        try {
          IndexService.instance().buildIndex(indexableIds, contentFormat);
          future.complete("Indexed");
        } catch (Exception e) {
          future.fail(e);
        }
      }
    }, result -> {
      if (result.succeeded()) {
        context.response().setStatusCode(200).end();
      } else {
        LOGGER.error("Re-index failed !!!");
        context.response().setStatusCode(500).end();
      }
    }));
  }
  
  private void indexContentInfo(final Router router) {
    router.post(RouteConstants.EP_BUILD_CONTENT_INDEX).handler(context -> vertx.executeBlocking(future -> {
      String indexableId = context.request().getParam(RouteConstants.INDEXABLE_ID);
      if (indexableId != null) {
        try {
          IndexService.instance().buildInfoIndex(indexableId);
          future.complete("Extracted and Indexed");
        } catch (Exception e) {
          future.fail(e);
        }
      }
    }, result -> {
      if (result.succeeded()) {
        context.response().setStatusCode(200).end();
      } else {

        LOGGER.error("Re-index failed !!!", result.cause());
        context.response().setStatusCode(500).end();
      }
    }));
  }
  
  private void markBrokenStatus(final Router router) {
    router.post(RouteConstants.EP_MARK_BROKEN_STATUS).handler(context -> vertx.executeBlocking(future -> {
      String indexableIds = context.request().getParam(RouteConstants.INDEXABLE_IDS);
      if (indexableIds != null) {
        try {
          IndexService.instance().updateBrokenStatus(indexableIds, true);
          future.complete("Indexed");
        } catch (Exception e) {
          future.fail(e);
        }
      }
    }, result -> {
      if (result.succeeded()) {
        context.response().setStatusCode(200).end();
      } else {
        LOGGER.error("Re-index failed !!!");
        context.response().setStatusCode(500).end();
      }
    }));
  }
  
  private void markUnBrokenStatus(final Router router) {
    router.post(RouteConstants.EP_MARK_UNBROKEN_STATUS).handler(context -> vertx.executeBlocking(future -> {
      String indexableIds = context.request().getParam(RouteConstants.INDEXABLE_IDS);
      if (indexableIds != null) {
        try {
          IndexService.instance().updateBrokenStatus(indexableIds, false);
          future.complete("Indexed");
        } catch (Exception e) {
          future.fail(e);
        }
      }
    }, result -> {
      if (result.succeeded()) {
        context.response().setStatusCode(200).end();
      } else {
        LOGGER.error("Re-index failed !!!");
        context.response().setStatusCode(500).end();
      }
    }));
  }
  
}
