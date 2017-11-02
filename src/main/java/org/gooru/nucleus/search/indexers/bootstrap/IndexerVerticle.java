package org.gooru.nucleus.search.indexers.bootstrap;

import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizers;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializer;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializers;
import org.gooru.nucleus.search.indexers.bootstrap.startup.JobInitializer;
import org.gooru.nucleus.search.indexers.bootstrap.startup.JobInitializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;

public class IndexerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexerVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    LOGGER.info("Starting indexer jobs and components....");
    Future<Void> startApplicationFuture = Future.future();
    deployIndexBuilderVertical();
    Future<Void> startJobFuture = Future.future();

    startApplication(startApplicationFuture);
    startJob(startJobFuture);

    CompositeFuture.all(startApplicationFuture, startJobFuture).setHandler(result -> {
      if (result.succeeded()) {
        LOGGER.info("Successfully initialized Indexer Handler machinery");
        startFuture.complete();
      } else {
        LOGGER.error("Not able to initialize the Indexer Handler machinery properly", result.cause());
        startFuture.fail(result.cause());

        // Not much options now, no point in continuing
        Runtime.getRuntime().halt(1);
      }
    });
  }
  
  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    LOGGER.info("Stopping all indexer components....");
    shutdownApplication(stopFuture);
    super.stop();
  }
  
  private void startApplication(Future<Void> startApplicationFuture) {
    vertx.executeBlocking(future -> {
      Initializers initializers = new Initializers();
      try {
        for (Initializer initializer : initializers) {
          initializer.initializeComponent(vertx, config());
        }
        future.complete();
      } catch (IllegalStateException ie) {
        LOGGER.error("Error initializing application", ie);
        future.fail(ie);
      }
    }, result -> {
      if (result.succeeded()) {
        LOGGER.info("All compenents started successfully");
        startApplicationFuture.complete();
      } else {
        LOGGER.warn("Connections startup failure", result.cause());
        startApplicationFuture.fail(result.cause());
      }
    });
  }
  
  private void startJob(Future<Void> startJobFuture) {
    vertx.executeBlocking(future -> {
      JobInitializers jobInitializers = new JobInitializers();
      try {
        for (JobInitializer jobInitializer : jobInitializers) {
          jobInitializer.deployJob(config());
        }
        future.complete();
      } catch (IllegalStateException ie) {
        LOGGER.error("Error starting jobs", ie);
        future.fail(ie);
      }
    }, result -> {
      if (result.succeeded()) {
        LOGGER.info("All jobs started successfully");
        startJobFuture.complete();
      } else {
        LOGGER.warn("Jobs startup failure", result.cause());
        startJobFuture.fail(result.cause());
      }
    });
  }
  
  private void shutdownApplication(Future<Void> shutdownApplicationFuture) {
    vertx.executeBlocking(future -> {
      Finalizers finalizers = new Finalizers();
      for (Finalizer finalizer : finalizers) {
        finalizer.finalizeComponent();
      }
      future.complete();
    }, result -> {
      if (result.succeeded()) {
        LOGGER.info("Component finalization for application shutdown done successfully");
        shutdownApplicationFuture.complete();
      } else {
        LOGGER.warn("App shutdown failure", result.cause());
        shutdownApplicationFuture.fail(result.cause());
      }
    });
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
