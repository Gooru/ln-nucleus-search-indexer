package org.gooru.nucleus.search.indexers.app.constants;

public enum IndexType {

  COLLECTION("collection"),
  QUESTION("question"),
  RESOURCE_REFERENCE("resource-reference"),
  RESOURCE("resource"),
  USER("user"),
  SEARCH_QUERY("searchquery"),
  TAXONOMY("taxonomy"),
  LIBRARY("library"),
  DICTIONARY("dictionary"),
  PUBLISHER("publisher"),
  AGGREGATOR("aggregator"),
  COURSE("course"),
  CROSSWALK("crosswalk"),
  UNIT("unit"),
  LESSON("lesson"),
  RUBRIC("rubric"),
  KEYWORD("keyword"),
  TENANT("tenant"),
  GUT("gut");

  final String type;

  IndexType(String type) {
    this.type = type;
  }

  public String getType() {
    return this.type;
  }

}
