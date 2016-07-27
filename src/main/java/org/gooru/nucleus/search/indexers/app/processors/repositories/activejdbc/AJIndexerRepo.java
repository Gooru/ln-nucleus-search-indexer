package org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc;

import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc.dbhandlers.DBHandlerBuilder;
import org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc.transactions.TransactionExecutor;

import io.vertx.core.json.JsonObject;

public class AJIndexerRepo implements IndexerRepo {

  private final ProcessorContext context;

  public AJIndexerRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public JsonObject getIndexDataContent() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildFetchContentHandler(context));
  }

  @Override
  public JsonObject trackIndexActions() {
    return new TransactionExecutor().executeTransaction(new DBHandlerBuilder().buildIndexTrackerHandler(context));
  }

}
