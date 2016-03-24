package org.gooru.nucleus.search.indexers.app.constants;

import java.util.HashMap;
import java.util.Map;

public class IndexerConstants {

	public static final String TYPE_RESOURCE = "resource";
	
	public static final String TYPE_QUESTION = "question";

	public static final String TYPE_COLLECTION = "collection";

	public static final String TYPE_ASSESSMENT = "assessment";

	public static Map<String, String> metadataReferenceType;
	
	public static String getMetadataIndexAttributeName(String referencetype) {
		return metadataReferenceType.get(referencetype);
	}
	
	public static Map<String, Map<String, Object>> metadataReference;
	
	public static Map<String, Object> getMetadataIndexFieldName(String referencetype) {
		return metadataReference.get(referencetype);
	}

	static {
		putMetadataReferenceType();
		putMetadataSearchSetting();
	}
	
	public enum MetadataReferenceType {
		EDUCATIONAL_USE("educational_use", "educationalUse"),
		MOMENTS_OF_LEARNING("moments_of_learning", "momentsOfLearning"),
		DEPTH_OF_KNOWLEDGE("depth_of_knowledge", "depthOfKnowledge"),
		READING_LEVEL("reading_level", "readingLevel"),
		AUDIENCE("audience", "audience"),
		ADVERTISEMENT_LEVEL("advertisement_level", "advertisementLevel"),
		HAZARD_LEVEL("hazard_level", "hazardLevel"),
		MEDIA_FEATURE("media_feature", "mediaFeature"),
		GRADE("grade", "grade");

		private String attributeName;
		
		private String indexAttributeName;
		
		private MetadataReferenceType(String attributeName, String indexAttributeName) {
			this.attributeName = attributeName;
			this.indexAttributeName = indexAttributeName;
		}

		public String getAttributeName() {
			return attributeName;
		}

		public void setAttributeName(String attributeName) {
			this.attributeName = attributeName;
		}

		public String getIndexAttributeName() {
			return indexAttributeName;
		}

		public void setIndexAttributeName(String indexAttributeName) {
			this.indexAttributeName = indexAttributeName;
		}
	}

	private static void putMetadataReferenceType() {
		metadataReferenceType = new HashMap<>();
		metadataReferenceType.put("educational_use", "educationalUse");
		metadataReferenceType.put("moments_of_learning", "momentsOfLearning");
		metadataReferenceType.put("depth_of_knowledge", "depthOfKnowledge");
		metadataReferenceType.put("reading_level", "readingLevel");
		metadataReferenceType.put("audience", "audience");
		metadataReferenceType.put("advertisement_level", "advertisementLevel");
		metadataReferenceType.put("hazard_level", "hazardLevel");
		metadataReferenceType.put("media_feature", "mediaFeature");
		metadataReferenceType.put("grade", "grade");		
	}
	
	//TODO move to constant file
	private static void putMetadataSearchSetting() {
		metadataReference = new HashMap<>();
		Map<String, Object> educationalUse = new HashMap<>();
		educationalUse.put("key", "educationalUse");
		educationalUse.put("addToFilters", true);
		educationalUse.put("addToAll", false);
		educationalUse.put("addToFilters", true);
		metadataReference.put("educational_use", educationalUse);
		Map<String, Object> momentsOfLearning = new HashMap<>();
		momentsOfLearning.put("key", "momentsOfLearning");
		momentsOfLearning.put("addToFilters", true);
		momentsOfLearning.put("addToAll", false);
		momentsOfLearning.put("addToFilters", true);
		metadataReference.put("moments_of_learning", momentsOfLearning);
		Map<String, Object> depthOfKnowledge = new HashMap<>();
		depthOfKnowledge.put("key", "depthOfKnowledge");
		depthOfKnowledge.put("addToFilters", true);
		depthOfKnowledge.put("addToAll", false);
		depthOfKnowledge.put("addToFilters", true);
		metadataReference.put("depth_of_knowledge", depthOfKnowledge);
		Map<String, Object> readingLevel = new HashMap<>();
		readingLevel.put("key", "readingLevel");
		readingLevel.put("addToFilters", true);
		readingLevel.put("addToAll", false);
		readingLevel.put("addToFilters", true);
		metadataReference.put("reading_level", readingLevel);
		Map<String, Object> audience = new HashMap<>();
		audience.put("key", "audience");
		audience.put("addToFilters", true);
		audience.put("addToAll", false);
		audience.put("addToFilters", "audience");
		metadataReference.put("audience", audience);
		Map<String, Object> advertisementLevel = new HashMap<>();
		advertisementLevel.put("key", "advertisementLevel");
		advertisementLevel.put("addToFilters", true);
		advertisementLevel.put("addToAll", false);
		advertisementLevel.put("addToFilters", true);
		metadataReference.put("advertisement_level", advertisementLevel);
		Map<String, Object> hazardLevel = new HashMap<>();
		hazardLevel.put("key", "hazardLevel");
		hazardLevel.put("addToFilters", true);
		hazardLevel.put("addToAll", false);
		hazardLevel.put("addToFilters", false);
		metadataReference.put("hazard_level", hazardLevel);
		Map<String, Object> mediaFeature = new HashMap<>();
		mediaFeature.put("key", "mediaFeature");
		mediaFeature.put("addToFilters", true);
		mediaFeature.put("addToAll", false);
		mediaFeature.put("addToFilters", false);
		metadataReference.put("media_feature", mediaFeature);
		Map<String, Object> grade = new HashMap<>();
		grade.put("key", "grade");
		grade.put("addToFilters", true);
		grade.put("addToAll", false);
		grade.put("addToFilters", false);
		metadataReference.put("grade", grade);
	}
}
