package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

public final class IndexHandlerBuilder {

  private IndexHandlerBuilder() {
    throw new AssertionError();
  }

  public static IndexHandler buildResourceIndexHandler() {
    return new ResourceIndexHandler();
  }

  public static IndexHandler buildCollectionIndexHandler() {
    return new CollectionIndexHandler();
  }
}
