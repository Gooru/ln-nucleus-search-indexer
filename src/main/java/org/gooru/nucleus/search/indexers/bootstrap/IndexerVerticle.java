package org.gooru.nucleus.search.indexers.bootstrap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizers;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializer;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexerVerticle.class);

  @Override
  public void start() throws Exception {
    vertx.executeBlocking(blockingFuture -> {
      startApplication();
      deployIndexBuilderVertical();
      blockingFuture.complete();
    }, future -> {
      if (future.succeeded()) {
        LOGGER.info("Successfully initialized Indexer Handler machinery");
      } else {
        LOGGER.error("Not able to initialize the Indexer Handler machinery properly");
      }
    });
  }

  @Override
  public void stop() throws Exception {
    shutDownApplication();
    super.stop();
  }

  private void startApplication() {
    Initializers initializers = new Initializers();
    try {
      for (Initializer initializer : initializers) {
        initializer.initializeComponent(vertx, config());
      }
    } catch (IllegalStateException ie) {
      LOGGER.error("Error initializing application", ie);
      Runtime.getRuntime().halt(1);
    }
  }

  private void shutDownApplication() {
    Finalizers finalizers = new Finalizers();
    for (Finalizer finalizer : finalizers) {
      finalizer.finalizeComponent();
    }
  }

  private void deployIndexBuilderVertical() {
    DeploymentOptions options = new DeploymentOptions().setConfig(config());
    vertx.deployVerticle("org.gooru.nucleus.search.indexers.bootstrap.IndexBuilderVerticle", options, res -> {
      if (res.succeeded()) {
        LOGGER.info("Deploying IndexBuilderVerticle... " + res.result());
      } else {
        LOGGER.info("Deployment of IndexBuilderVerticle failed !" + res.cause());
      }
    });
  }

}
