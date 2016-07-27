package org.gooru.nucleus.search.indexers.app.constants;

public final class RouteConstants {

  // Helper constants
  private static final String API_VERSION = "v1";
  private static final String API_BASE_ROUTE = "/api/nucleus-indexer/" + API_VERSION + '/';
  // Helper: Entity name constants
  private static final String INDEX = "index";
  private static final String INDEX_INFO = "index/info";
  private static final String MARK_BROKEN = "mark-broken";
  private static final String MARK_UNBROKEN = "mark-unbroken";

  private static final String DELETE = "delete";
  public static final String TYPE_IN_PATH = "/:type";
  // Upload file = /api/nucleus-indexer/{version}/index
  public static final String EP_BUILD_INDEX = API_BASE_ROUTE + INDEX;
  public static final String EP_BUILD_CONTENT_INDEX = API_BASE_ROUTE + INDEX_INFO;
  public static final String EP_MARK_BROKEN_STATUS = API_BASE_ROUTE + MARK_BROKEN;
  public static final String EP_MARK_UNBROKEN_STATUS = API_BASE_ROUTE + MARK_UNBROKEN;
  
  public static final String EP_BUILD_CONTENT_DELETE = API_BASE_ROUTE + DELETE + TYPE_IN_PATH;

  public static final String HTTP_PORT = "http.port";
  public static final String INDEXABLE_IDS = "ids";
  public static final String INDEXABLE_ID = "id";
  public static final String CONTENT_FORMAT = "contentFormat";
  public static final String TYPE = "type";

  private RouteConstants() {
    throw new AssertionError();
  }
}
