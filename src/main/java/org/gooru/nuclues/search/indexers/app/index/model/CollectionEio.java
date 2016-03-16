package org.gooru.nuclues.search.indexers.app.index.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class CollectionEio implements Serializable {
	
	private static final long serialVersionUID = -7695456210960325029L;
	private String id;
	private String title;
	private Date indexUpdatedDate;
	private Date createdAt;
	private Date updatedAt;
	private UserEo owner;
	private UserEo creator;
	private UserEo originalCreator;
	private String originalCollectionId;
	private String parentCollectionId;
	private Date publishDate;
	private String publishStatus;
	private String contentFormat;
	private String thumbnail;
	private String learningObjective;
	private Set<String> audience;
	private Set<String> collaboratorIds;
	private Object metadata;
	private Object taxonomyDataSet;
	private String orientation;
	private String url;
	private Boolean visibleOnProfile;
	private String gradingType;
	private StatisticsEo statistics;
	private Set<String> resourceIds;
	private Integer questionCount;
	private Integer resourceCount;
	private List<CollectionContentEo> collectionContents;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getIndexUpdatedDate() {
		return indexUpdatedDate;
	}

	public void setIndexUpdatedDate(Date indexUpdatedDate) {
		this.indexUpdatedDate = indexUpdatedDate;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public UserEo getOwner() {
		return owner;
	}

	public void setOwner(UserEo owner) {
		this.owner = owner;
	}

	public UserEo getCreator() {
		return creator;
	}

	public void setCreator(UserEo creator) {
		this.creator = creator;
	}

	public UserEo getOriginalCreator() {
		return originalCreator;
	}

	public void setOriginalCreator(UserEo originalCreator) {
		this.originalCreator = originalCreator;
	}

	public String getOriginalCollectionId() {
		return originalCollectionId;
	}

	public void setOriginalCollectionId(String originalCollectionId) {
		this.originalCollectionId = originalCollectionId;
	}

	public String getParentCollectionId() {
		return parentCollectionId;
	}

	public void setParentCollectionId(String parentCollectionId) {
		this.parentCollectionId = parentCollectionId;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public String getPublishStatus() {
		return publishStatus;
	}

	public void setPublishStatus(String publishStatus) {
		this.publishStatus = publishStatus;
	}

	public String getContentFormat() {
		return contentFormat;
	}

	public void setContentFormat(String contentFormat) {
		this.contentFormat = contentFormat;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getLearningObjective() {
		return learningObjective;
	}

	public void setLearningObjective(String learningObjective) {
		this.learningObjective = learningObjective;
	}

	public Set<String> getAudience() {
		return audience;
	}

	public void setAudience(Set<String> audience) {
		this.audience = audience;
	}

	public Set<String> getCollaborator() {
		return collaboratorIds;
	}

	public void setCollaborator(Set<String> collaborator) {
		this.collaboratorIds = collaborator;
	}

	public Object getMetadata() {
		return metadata;
	}

	public void setMetadata(Object metadata) {
		this.metadata = metadata;
	}

	public Object getTaxonomyDataSet() {
		return taxonomyDataSet;
	}

	public void setTaxonomyDataSet(Object taxonomyDataSet) {
		this.taxonomyDataSet = taxonomyDataSet;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getVisibleOnProfile() {
		return visibleOnProfile;
	}

	public void setVisibleOnProfile(Boolean visibleOnProfile) {
		this.visibleOnProfile = visibleOnProfile;
	}

	public String getGradingType() {
		return gradingType;
	}

	public void setGradingType(String gradingType) {
		this.gradingType = gradingType;
	}

	public StatisticsEo getStatistics() {
		return statistics;
	}

	public void setStatistics(StatisticsEo statistics) {
		this.statistics = statistics;
	}

	public Set<String> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(Set<String> resourceIds) {
		this.resourceIds = resourceIds;
	}

	public Integer getQuestionCount() {
		return questionCount;
	}

	public void setQuestionCount(Integer questionCount) {
		this.questionCount = questionCount;
	}

	public Integer getResourceCount() {
		return resourceCount;
	}

	public void setResourceCount(Integer resourceCount) {
		this.resourceCount = resourceCount;
	}

	public List<CollectionContentEo> getCollectionContents() {
		return collectionContents;
	}

	public void setCollectionContents(List<CollectionContentEo> collectionContents) {
		this.collectionContents = collectionContents;
	}

}
