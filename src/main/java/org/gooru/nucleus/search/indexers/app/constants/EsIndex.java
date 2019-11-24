/**
 *
 */
package org.gooru.nucleus.search.indexers.app.constants;

/**
 * @author SearchTeam
 */
public enum EsIndex {

  RESOURCE("resource"),
  COLLECTION("collection"),
  TAXONOMY("taxonomy"),
  LIBRARY("library"),
  SEARCH_QUERY("searchquery"),
  DICTIONARY("dictionary"),
  USER("user"),
  CONTENT_PROVIDER("content_provider", new String[]{"publisher"}),
  CONTENT_INFO("contentinfo"),
  COURSE("course"),
  CROSSWALK("crosswalk"),
  UNIT("unit"),
  LESSON("lesson"), 
  RUBRIC("rubric"), 
  QUERY("query", new String[]{"keyword"}), 
  TENANT("tenant"), 
  GUT("gut");

  private final String name;

  private final String[] types;

  /**
   *
   */
  EsIndex(String name, String[] types) {
    this.name = name;
    this.types = types;
  }

  EsIndex(String name) {
    this.name = name;
    this.types = new String[]{name};
  }

  public String getName() {
    return name;
  }

  public String[] getTypes() {
    return types;
  }
}
