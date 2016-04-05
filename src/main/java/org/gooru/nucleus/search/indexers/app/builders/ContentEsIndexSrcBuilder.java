package org.gooru.nucleus.search.indexers.app.builders;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.index.model.AnswerEo;
import org.gooru.nucleus.search.indexers.app.index.model.ContentEio;
import org.gooru.nucleus.search.indexers.app.index.model.HintEo;
import org.gooru.nucleus.search.indexers.app.index.model.QuestionEo;
import org.gooru.nucleus.search.indexers.app.index.model.ScoreFields;
import org.gooru.nucleus.search.indexers.app.index.model.StatisticsEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEo;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;
import org.gooru.nucleus.search.indexers.app.utils.PCWeightUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author SearchTeam
 * 
 */
public class ContentEsIndexSrcBuilder<S extends JsonObject, D extends ContentEio> extends EsIndexSrcBuilder<S, D> {
	
	@Override
	public JsonObject build(JsonObject source, D contentEo) throws Exception{
		try{
			LOGGER.debug("CEISB->build : source " + source.toString());
			String id = source.getString(EntityAttributeConstants.ID);
			contentEo.setId(id);
			contentEo.setContentFormat(source.getString(EntityAttributeConstants.CONTENT_FORMAT, null));
			contentEo.setUrl(source.getString(EntityAttributeConstants.URL, null));
			contentEo.setTitle(source.getString(EntityAttributeConstants.TITLE, null));
			String description = source.getString(EntityAttributeConstants.DESCRIPTION, null);
			contentEo.setDescription(description);
			contentEo.setIndexUpdatedTime(new Date(System.currentTimeMillis()));
			contentEo.setCreatedAt(source.getString(EntityAttributeConstants.CREATED_AT));
			contentEo.setUpdatedAt(source.getString(EntityAttributeConstants.UPDATED_AT));
			contentEo.setOriginalContentId(source.getString(EntityAttributeConstants.ORIGINAL_CONTENT_ID, null));
			contentEo.setParentContentId(source.getString(EntityAttributeConstants.PARENT_CONTENT_ID, null));
			contentEo.setPublishDate(source.getString(EntityAttributeConstants.PUBLISH_DATE, null));
			contentEo.setPublishStatus(source.getString(EntityAttributeConstants.PUBLISH_STATUS, null));
			contentEo.setShortTitle(source.getString(EntityAttributeConstants.SHORT_TITLE, null));
			contentEo.setNarration(source.getString(EntityAttributeConstants.NARRATION, null));
			String thumbnail = source.getString(EntityAttributeConstants.THUMBNAIL, null);
			contentEo.setThumbnail(thumbnail);
			contentEo.setCollectionId(source.getString(EntityAttributeConstants.COLLECTION_ID, null));
			contentEo.setIsCopyrightOwner(source.getBoolean(EntityAttributeConstants.IS_COPYRIGHT_OWNER, null));
			contentEo.setVisibleOnProfile(source.getBoolean(EntityAttributeConstants.VISIBLE_ON_PROFILE, null));
			
			//Set Original Creator
			String originalCreatorId = source.getString(EntityAttributeConstants.ORIGINAL_CREATOR_ID, null);
			if (originalCreatorId != null) {
				UserEo orginalCreatorEo = new UserEo();
				JsonObject orginalCreator = getUserRepo().getUser(originalCreatorId);
				if (orginalCreator != null) {
					setUser(orginalCreator, orginalCreatorEo);
					contentEo.setOriginalCreator(orginalCreatorEo.getUser());
				}
			}
			//Set Creator
			String creatorId = source.getString(EntityAttributeConstants.CREATOR_ID, null);
			if (creatorId != null) {
				UserEo creatorEo = new UserEo();
				JsonObject creator = getUserRepo().getUser(creatorId);
				if (creator != null) {
					setUser(creator, creatorEo);
					contentEo.setCreator(creatorEo.getUser());
				}
			}
			//Set ContentSubFormat type escaped
			String contentSubFormat = source.getString(EntityAttributeConstants.CONTENT_SUB_FORMAT, null);
			contentEo.setContentSubFormat(contentSubFormat);
			String contentSubTypeEscaped = null;
			if (contentSubFormat.contains("-")) {
				contentSubTypeEscaped = contentSubFormat.replace("-", "");
			} else if (contentSubFormat.contains("_")) {
				contentSubTypeEscaped = contentSubFormat.replace("_", "");
			} else {
				contentSubTypeEscaped = contentSubFormat.replace("/", "");
			}
			contentEo.setContentSubFormatEscaped(contentSubTypeEscaped);
			//Set CopyrightOwner
			String copyrightOwner = source.getString(EntityAttributeConstants.COPYRIGHT_OWNER, null);
			if (copyrightOwner != null) {
				JsonObject copyrightOwnerJson = new JsonObject(copyrightOwner);
				if (copyrightOwnerJson != null) {
					contentEo.setCopyrightOwner(copyrightOwnerJson);
				}
			}
			//Set Question
			QuestionEo questionEo = new QuestionEo();
			String answerJson = source.getString(EntityAttributeConstants.ANSWER, null);
			if (answerJson != null) {
				JsonArray answerArray = new JsonArray(answerJson);
				if (answerArray != null && answerArray.size() > 0) {
					AnswerEo answerEo = new AnswerEo();
					StringBuilder answerText = new StringBuilder();
					for (int index = 0; index < answerArray.size(); index++) {
						JsonObject answerObject = answerArray.getJsonObject(index);
						String answerString = answerObject.getString(EntityAttributeConstants.ANSWER_TEXT, null);
						if (answerString != null) {
							if (answerText.length() > 0) {
								answerText.append(" ~~ ");
							}
							answerText.append(answerString);
						}
					}
					answerEo.setAnswerText(answerText.toString());
					questionEo.setAnswer(answerEo.getAnswer());
				}
			}
			String hintDetail = source.getString(EntityAttributeConstants.HINT_EXPLANATION_DETAIL, null);
			if (hintDetail != null) {
				JsonObject hintExplanationDetail = new JsonObject(hintDetail);
				if (hintExplanationDetail != null) {
					JsonArray hintArray = hintExplanationDetail.getJsonArray(EntityAttributeConstants.HINTS, null);
					if (hintArray != null && hintArray.size() > 0) {
						HintEo hintEo = new HintEo();
						StringBuilder hintText = new StringBuilder();
						int hintCount = 0;
						for (int index = 0; index < hintArray.size(); index++) {
							JsonObject hintObject = hintArray.getJsonObject(index);
							String hint = hintObject.getString(EntityAttributeConstants.HINT, null);
							if (hint != null) {
								hintCount++;
								if (hintText.length() > 0) {
									hintText.append(" ~~ ");
								}
								hintText.append(hint);
							}
						}
						hintEo.setHintText(hintText.toString());
						hintEo.setHintCount(hintCount);
						questionEo.setHint(hintEo.getHint());
					}
				}
			}
			if (!questionEo.getQuestion().isEmpty()) {
				contentEo.setQuestion(questionEo.getQuestion());
			}
			//Set Metadata
			String metadataString = source.getString(EntityAttributeConstants.METADATA, null);
			if (metadataString != null) {
				JsonObject metadata = new JsonObject(metadataString);
				if (metadata != null) {
					JsonObject metadataAsMap = new JsonObject();
					for (String fieldName : metadata.fieldNames()) {
						String key = IndexerConstants.getMetadataIndexAttributeName(fieldName);
						JsonArray value = new JsonArray();
						JsonArray references = metadata.getJsonArray(fieldName);
						if (references != null) {
							String referenceIds = references.toString();
							List<Map> metacontent = getIndexRepo().getMetadata(referenceIds.substring(1, referenceIds.length() - 1));
							for (Map metaMap : metacontent) {
								value.add(metaMap.get(EntityAttributeConstants.LABEL).toString().toLowerCase().replaceAll("[^\\dA-Za-z]", "_"));
							}
							metadataAsMap.put(key, value);
							contentEo.setMetadata(metadataAsMap);
						}
					}
				}
			}
			//Set Collection info of content
			JsonArray collectionIds = new JsonArray();
			JsonArray collectionTitles = new JsonArray();
			String collectionId = source.getString(EntityAttributeConstants.COLLECTION_ID, null);
			if (collectionId != null) {
				collectionIds.add(collectionId);
			}
			List<Map> collectionMetaAsList = getContentRepo().getCollectionMeta(id);
			if (collectionMetaAsList != null && collectionMetaAsList.size() > 0) {
				for (Map collectionMetaMap : collectionMetaAsList) {
					String usedCollectionId = collectionMetaMap.get(EntityAttributeConstants.ID).toString();
					collectionIds.add(usedCollectionId);
					collectionTitles.add(collectionMetaMap.get(EntityAttributeConstants.TITLE));
				}
				contentEo.setCollectionIds(collectionIds);
				contentEo.setCollectionTitles(new JsonArray(collectionTitles.stream().distinct().collect(Collectors.toList())));
			}
			
			
			String taxonomy = source.getString(EntityAttributeConstants.TAXONOMY, null);
	 		if (taxonomy != null) {
	 			JsonArray taxonomyArray = new JsonArray(taxonomy);
	 			TaxonomyEo taxonomyEo = new TaxonomyEo();
	 			if (taxonomyArray.size() > 0) {
	 				addTaxnomy(taxonomyArray, taxonomyEo);
	 			}
	 			contentEo.setTaxonomy(taxonomyEo.getTaxonomyJson());
	 		}
	 		
			// Set Statistics
			StatisticsEo statisticsEo = new StatisticsEo();
			statisticsEo.setHasNoThumbnail(thumbnail != null ? 0 : 1);
			statisticsEo.setHasNoDescription(description != null ? 0 : 1);
			statisticsEo.setUsedInCollectionCount(collectionIds.size());
            
			long viewsCount = source.getLong(ScoreConstants.VIEW_COUNT);
			
			if(source.getBoolean(IS_BUILD_INDEX) != null && source.getBoolean(IS_BUILD_INDEX)){
				statisticsEo.setViewsCount(viewsCount);
			}
			
			// Set ranking fields 
			Map<String, Object> rankingFields = new HashMap<>();
			rankingFields.put(ScoreConstants.USED_IN_COLLECTION_COUNT, statisticsEo.getUsedInCollectionCount());
			rankingFields.put(ScoreConstants.VIEW_COUNT, viewsCount);
			rankingFields.put(ScoreConstants.HAS_FRAME_BREAKER, statisticsEo.getHasFrameBreaker());
			rankingFields.put(ScoreConstants.HAS_NO_THUMBNAIL, statisticsEo.getHasNoThumbnail());
			rankingFields.put(ScoreConstants.DESCRIPTION_FIELD, description);
			rankingFields.put(ScoreConstants.SATS_HAS_NO_DESC, statisticsEo.getHasNoDescription());
			rankingFields.put(ScoreConstants.RESOURCE_URL_FIELD, contentEo.getUrl());
			rankingFields.put(ScoreConstants.HAS_21ST_CENTURY_SKILL,  statisticsEo.getHas21stCenturySkills());
			
			JsonObject taxJson  = contentEo.getTaxonomy();
			int hasStandard = 0;
			if(taxJson != null){
				hasStandard = taxJson.getInteger(EntityAttributeConstants.TAXONOMY_HAS_STD);
			}
			
			rankingFields.put(ScoreConstants.TAX_HAS_STANDARD, hasStandard);
			
			double pcWeight = PCWeightUtil.getResourcePcWeight(new ScoreFields(rankingFields));
			LOGGER.debug("CEISB->build : PC weight : " +pcWeight);
			statisticsEo.setPreComputedWeight(pcWeight);
			
			
			contentEo.setStatistics(statisticsEo.getStatistics());

			/*//TODO Add logic to store below details
			statisticsEo.setHasFrameBreaker(hasFrameBreaker);
			statisticsEo.setInvalidResource(invalidResource);
			statisticsEo.setHasAdvertisement(hasAdvertisement);
			statisticsEo.setHas21stCenturySkills(has21stCenturySkills);
			statisticsEo.setStatusIsBroken(statusIsBroken);
			statisticsEo.setViewsCount(viewsCount);
			JsonArray sourceTaxonomy = source.getJsonArray(EntityAttributeConstants.TAXONOMY, null);
			if (sourceTaxonomy != null && sourceTaxonomy.size() > 0) {
				TaxonomyEo taxonomyEo = new TaxonomyEo();
				contentEo.setTaxonomy(taxonomyEo.getTaxonomyJson());
			}*/
			
			LOGGER.debug("CEISB -> build : content Eo source : " + contentEo.getContentJson().toString());
		}
		catch(Exception e){
			LOGGER.error("CEISB->build : Failed to build source : Exception" , e);
			throw new Exception(e);
		}
		return contentEo.getContentJson();
	}
	
	@Override
	public String getName() {
		return IndexType.RESOURCE.getType();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String buildSource(JsonObject source) throws Exception {
		return buildSource(source, (D) new ContentEio());
	}
	
}

