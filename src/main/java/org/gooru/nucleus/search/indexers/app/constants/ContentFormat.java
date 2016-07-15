package org.gooru.nucleus.search.indexers.app.constants;

public enum ContentFormat {

  RESOURCE("resource"),
  COLLECTION("collection"),
  ASSESSMENT("assessment"),
  QUESTION("question"),
  COURSE("course"),
  UNIT("unit");

  final String contentFormat;

  ContentFormat(String contentFormat) {
    this.contentFormat = contentFormat;
  }

  public String getContentFormat() {
    return contentFormat;
  }
}
