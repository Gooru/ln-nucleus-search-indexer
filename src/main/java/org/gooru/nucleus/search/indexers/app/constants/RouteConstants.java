package org.gooru.nucleus.search.indexers.app.constants;

public final class RouteConstants {

  // Helper constants
  private static final String API_VERSION = "v1";
  private static final String API_BASE_ROUTE = "/api/nucleus-indexer/" + API_VERSION + '/';
  // Helper: Entity name constants
  private static final String INDEX = "index";
  private static final String INDEX_INFO = "index/info";
  // Upload file = /api/nucleus-indexer/{version}/index
  public static final String EP_BUILD_INDEX = API_BASE_ROUTE + INDEX;
  
  public static final String EP_BUILD_CONTENT_INDEX = API_BASE_ROUTE + INDEX_INFO;
  
  public static final String HTTP_PORT = "http.port";
  public static final String INDEXABLE_IDS = "ids";
  public static final String INDEXABLE_ID = "id";
  public static final String CONTENT_FORMAT = "contentFormat";

  private RouteConstants() {
    throw new AssertionError();
  }
}
