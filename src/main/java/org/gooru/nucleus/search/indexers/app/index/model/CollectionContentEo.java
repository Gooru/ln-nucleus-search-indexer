package org.gooru.nucleus.search.indexers.app.index.model;

import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.search.indexers.app.utils.JsonUtil;

public class CollectionContentEo {

  private JsonObject collectionContents;

  public CollectionContentEo() {
    this.collectionContents = new JsonObject();
  }

  public JsonObject getCollectionContentJson() {
    return this.collectionContents;
  }

  public String getTitle() {
    return collectionContents.getString("title", null);
  }

  public void setTitle(String title) {
    this.collectionContents = JsonUtil.set(collectionContents, "title", title);
  }

  public String getShortTitle() {
    return collectionContents.getString("shortTitle", null);
  }

  public void setShortTitle(String shortTitle) {
    this.collectionContents = JsonUtil.set(collectionContents, "shortTitle", shortTitle);
  }

  public Integer getSequenceId() {
    return collectionContents.getInteger("sequenceId", null);
  }

  public void setSequenceId(Integer sequenceId) {
    this.collectionContents = JsonUtil.set(collectionContents, "sequenceId", sequenceId);
  }

  public String getDescription() {
    return collectionContents.getString("sequenceId", null);
  }

  public void setDescription(String description) {
    this.collectionContents = JsonUtil.set(collectionContents, "description", description);
  }

  public String getText() {
    return collectionContents.getString("text", null);
  }

  public void setText(String text) {
    this.collectionContents = JsonUtil.set(collectionContents, "text", text);
  }

  public String getContentSubFormat() {
    return collectionContents.getString("contentSubFormat", null);
  }

  public void setContentSubFormat(String contentSubFormat) {
    this.collectionContents = JsonUtil.set(collectionContents, "contentSubFormat", contentSubFormat);
  }

  public String getUrl() {
    return collectionContents.getString("url", null);
  }

  public void setUrl(String url) {
    this.collectionContents = JsonUtil.set(collectionContents, "url", url);
  }

  public String getContentFormat() {
    return collectionContents.getString("contentFormat", null);
  }

  public void setContentFormat(String contentFormat) {
    this.collectionContents = JsonUtil.set(collectionContents, "contentFormat", contentFormat);
  }

  public String getId() {
    return collectionContents.getString("id", null);
  }

  public void setId(String id) {
    this.collectionContents = JsonUtil.set(collectionContents, "id", id);
  }

  public String getThumbnail() {
    return collectionContents.getString("thumbnail", null);
  }

  public void setThumbnail(String thumbnail) {
    this.collectionContents = JsonUtil.set(collectionContents, "thumbnail", thumbnail);
  }

}
