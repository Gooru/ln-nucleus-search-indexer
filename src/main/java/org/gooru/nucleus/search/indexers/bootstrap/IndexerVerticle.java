package org.gooru.nucleus.search.indexers.bootstrap;

import org.gooru.nucleus.search.indexers.app.services.EsIndexServiceImpl;
import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.search.indexers.bootstrap.shutdown.Finalizers;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializer;
import org.gooru.nucleus.search.indexers.bootstrap.startup.Initializers;
import org.gooru.nuclues.search.indexers.app.repositories.activejdbc.ContentRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class IndexerVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndexerVerticle.class);

	@Override
	public void start() throws Exception {

		vertx.executeBlocking(blockingFuture -> {
			startApplication();
			// testFetchFromDBAndIndex();
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

	private void testFetchFromDBAndIndex() {
		String id = "16ba9509-03d2-45d9-94e9-23cd3a90fa56";
		ContentRepositoryImpl cr = new ContentRepositoryImpl();
		JsonObject jsonBody = cr.getResource(id);
		EsIndexServiceImpl eis = new EsIndexServiceImpl();
		eis.indexDocuments(id, "gooru_local_resource_v2", "resource", jsonBody);
	}
}
