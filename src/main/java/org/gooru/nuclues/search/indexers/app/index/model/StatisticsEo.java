package org.gooru.nuclues.search.indexers.app.index.model;

public class StatisticsEo {
	
	private Integer usedInSCollectionCount = 0;
	
	private Long scollectionRemixCount = 0L;
	
	private Long viewsCount = 0L;
	
	private Long resourceUsedUserCount = 0L;
	
	private Long resourceAddedCount = 0L;

	private Long averageTimeSpent = 0L;
	
	private Integer invalidResource = 0;
	
	private Integer hasNoThumbnail = 0;
	
	private Integer hasNoDescription = 0;
	
	private Integer hasFrameBreaker = 0;
	
	private Integer statusIsBroken = 0;
	
	private Double preComputedWeight = 0.0;
		
	private Boolean has21stCenturySkills;
	
	private Boolean hasAdvertisement;
	
	public Long getViewsCount() {
		return viewsCount;
	}

	public void setViewsCount(Long viewsCount) {
		this.viewsCount = viewsCount;
	}

	public Integer getUsedInSCollectionCount() {
		return usedInSCollectionCount;
	}

	public void setUsedInSCollectionCount(Integer usedInSCollectionCount) {
		if(usedInSCollectionCount == null) {
			usedInSCollectionCount = 0;
		}
		this.usedInSCollectionCount = usedInSCollectionCount;
	}


	public Integer getInvalidResource() {
		return invalidResource;
	}

	public void setInvalidResource(Integer invalidResource) {
		if(invalidResource == null) {
			invalidResource = 0;
		}
		this.invalidResource = invalidResource;
	}

	public Integer getHasNoThumbnail() {
		return hasNoThumbnail;
	}

	public void setHasNoThumbnail(Integer hasNoThumbnail) {
		if(hasNoThumbnail == null) {
			hasNoThumbnail = 0;
		}
		this.hasNoThumbnail = hasNoThumbnail;
	}

	public Integer getHasNoDescription() {
		return hasNoDescription;
	}

	public void setHasNoDescription(Integer hasNoDescription) {
		if(hasNoDescription == null) {
			hasNoDescription = 0;
		}
		this.hasNoDescription = hasNoDescription;
	}


	public Integer getHasFrameBreaker() {
		return hasFrameBreaker;
	}

	public void setHasFrameBreaker(Integer hasFrameBreaker) {
		if(hasFrameBreaker == null) {
			hasFrameBreaker = 0;
		}
		this.hasFrameBreaker = hasFrameBreaker;
	}

	public Integer getStatusIsBroken() {
		return statusIsBroken;
	}

	public void setStatusIsBroken(Integer statusIsBroken) {
		if(statusIsBroken == null) {
			statusIsBroken = 0;
		}
		this.statusIsBroken = statusIsBroken;
	}

	public void setPreComputedWeight(Double preComputedWeight) {
		if(preComputedWeight == null) {
			preComputedWeight = 0.0;
		}
		this.preComputedWeight = preComputedWeight;
	}

	public Double getPreComputedWeight() {
		return preComputedWeight;
	}
	
	public Long getAverageTimeSpent() {
		return averageTimeSpent;
	}

	public void setAverageTimeSpent(Long averageTimeSpent) {
		if(averageTimeSpent != null){
			this.averageTimeSpent = averageTimeSpent;
		}
	}
	
	public Long getResourceUsedUserCount() {
		return resourceUsedUserCount;
	}

	public void setResourceUsedUserCount(Long resourceUsedUserCount) {
		this.resourceUsedUserCount = resourceUsedUserCount;
	}

	public Long getResourceAddedCount() {
		return resourceAddedCount;
	}

	public void setResourceAddedCount(Long resourceAddedCount) {
		this.resourceAddedCount = resourceAddedCount;
	}

	public Long getScollectionRemixCount() {
		return scollectionRemixCount;
	}

	public void setScollectionRemixCount(Long scollectionRemixCount) {
		this.scollectionRemixCount = scollectionRemixCount;
	}

	public Boolean getHas21stCenturySkills() {
		return has21stCenturySkills;
	}

	public void setHas21stCenturySkills(Boolean has21stCenturySkills) {
		this.has21stCenturySkills = has21stCenturySkills;
	}

	public Boolean getHasAdvertisement() {
		return hasAdvertisement;
	}

	public void setHasAdvertisement(Boolean hasAdvertisement) {
		this.hasAdvertisement = hasAdvertisement;
	}

}
