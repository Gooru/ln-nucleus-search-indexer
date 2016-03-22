package org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc;

import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;

public final class ActiveJdbcRepoBuilder {

  public static IndexerRepo buildAJIndexerRepo(ProcessorContext context) {
    return new AJIndexerRepo(context);
  }
   
}
