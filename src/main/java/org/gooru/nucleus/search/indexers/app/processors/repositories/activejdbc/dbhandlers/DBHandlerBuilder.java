package org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;

public class DBHandlerBuilder {

  public DBHandler buildFetchContentHandler(ProcessorContext context) {
    return new FetchContentHandler(context);
  }

}
