package org.gooru.nucleus.search.indexers.app.constants;

public enum ContentFormat {

  RESOURCE("resource"),
  COLLECTION("collection"),
  ASSESSMENT("assessment"),
  EXTERNAL_ASSESSMENT("assessment-external"),
  COLLECTION_ASSESSMENT("collection-external"),
  QUESTION("question"),
  COURSE("course"),
  UNIT("unit"),
  RUBRIC("rubric");

  final String contentFormat;

  ContentFormat(String contentFormat) {
    this.contentFormat = contentFormat;
  }

  public String getContentFormat() {
    return contentFormat;
  }
}
