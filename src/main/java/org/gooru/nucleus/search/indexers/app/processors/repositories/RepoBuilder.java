package org.gooru.nucleus.search.indexers.app.processors.repositories;

import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc.ActiveJdbcRepoBuilder;
import org.gooru.nucleus.search.indexers.app.processors.repositories.activejdbc.IndexerRepo;


public final class RepoBuilder {

  private RepoBuilder() {
    throw new AssertionError();
  }

  public static IndexerRepo buildIndexerRepo(ProcessorContext context) {
    return ActiveJdbcRepoBuilder.buildAJIndexerRepo(context);
  }
}
