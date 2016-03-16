package org.gooru.nuclues.search.indexers.app.index.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class ContentEio implements Serializable {

	private static final long serialVersionUID = -8299148888835845089L;
	private String id;
	private String type;
	private String url;
	private String title;
	private Date indexUpdatedDate;
	private Date createdAt;
	private Date updatedAt;
	private String originalContentId;
	private String parentContentId;
	private UserEo owner; // creator
	private UserEo creator; // original_creator
	private Date publishDate;
	private String publishStatus;
	private String shortTitle;
	private String narration;
	private String description;
	private String contentFormat; // resource/question
	private String contentSubFormat; // Res : WEB, URL; QUES : MC, TF
	private QuesionEo question; // answer, hint_explanation_detail,
	private Map<String, Object> metadata; // educational_use,reading_level,advertisement_level
	private Object taxonomyDataSet;
	private String thumbnail;
	private String collectionId;
	private Boolean isCopyrightOwner;
	private UserEo copyrightOwner;
	private Boolean visibleOnProfile;
	private StatisticsEo statistics;
	private Set<String> collectionIds;
	private String collectionTitles;
	private String grade;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getOriginalContentId() {
		return originalContentId;
	}

	public void setOriginalContentId(String originalContentId) {
		this.originalContentId = originalContentId;
	}

	public String getParentContentId() {
		return parentContentId;
	}

	public void setParentContentId(String parentContentId) {
		this.parentContentId = parentContentId;
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

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContentFormat() {
		return contentFormat;
	}

	public void setContentFormat(String contentFormat) {
		this.contentFormat = contentFormat;
	}

	public String getContentSubFormat() {
		return contentSubFormat;
	}

	public void setContentSubFormat(String contentSubFormat) {
		this.contentSubFormat = contentSubFormat;
	}

	public QuesionEo getQuestion() {
		return question;
	}

	public void setQuestion(QuesionEo question) {
		this.question = question;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	public Object getTaxonomyDataSet() {
		return taxonomyDataSet;
	}

	public void setTaxonomyDataSet(Object taxonomyDataSet) {
		this.taxonomyDataSet = taxonomyDataSet;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public Boolean getIsCopyrightOwner() {
		return isCopyrightOwner;
	}

	public void setIsCopyrightOwner(Boolean isCopyrightOwner) {
		this.isCopyrightOwner = isCopyrightOwner;
	}

	public UserEo getCopyrightOwner() {
		return copyrightOwner;
	}

	public void setCopyrightOwner(UserEo copyrightOwner) {
		this.copyrightOwner = copyrightOwner;
	}

	public Boolean getVisibleOnProfile() {
		return visibleOnProfile;
	}

	public void setVisibleOnProfile(Boolean visibleOnProfile) {
		this.visibleOnProfile = visibleOnProfile;
	}

	public StatisticsEo getStatistics() {
		return statistics;
	}

	public void setStatistics(StatisticsEo statistics) {
		this.statistics = statistics;
	}


	public Set<String> getCollectionIds() {
		return collectionIds;
	}

	public void setCollectionIds(Set<String> collectionIds) {
		this.collectionIds = collectionIds;
	}

	public String getCollectionTitles() {
		return collectionTitles;
	}

	public void setCollectionTitles(String collectionTitles) {
		this.collectionTitles = collectionTitles;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

}
