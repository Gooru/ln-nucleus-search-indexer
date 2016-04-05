package org.gooru.nucleus.search.indexers.app.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.ScoreConstants;
import org.gooru.nucleus.search.indexers.app.index.model.ScoreFields;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;

public class PCWeightUtil {
	
    public static final double getResourcePcWeight(final ScoreFields rankingData) throws Exception {
    	try{
			float usedInSCollectionCount = (float) rankingData.getResourceUsedCollectionCount()/ScoreConstants.MAX_RESOURCE_USED_99PERSENT_VAL;
			float frameBreakerScore = (rankingData.getHasFrameBreaker() == 0) ? 1f : ScoreConstants.DEMOTE_FRAME_BREAKER;
			float thumbnailScore = (rankingData.getHasNoThumbnail() == 0) ? 1f : ScoreConstants.DEMOTE_THUMBNAIL;
			float descScore = computeDiscriptionValue(rankingData.getDescription(), rankingData.getHasNoDescription());
			float domainBoost = (rankingData.getDomainBoost() == 1) ? 1f : ScoreConstants.DEMOTE_DOMAIN;
			float standardScore = (rankingData.getHasNoStandard() == 0) ? 1f : 0f;
			float skillScore = (rankingData.getHas21stCenturySkills()) ? 1f : 0f;
			float viewsScore = rankingData.getViewsCount()/ScoreConstants.MAX_RESOURCE_VIEWS_99PERSENT_VAL;
			 
			float usageSignalWeight = (float) ((normalizeValue(usedInSCollectionCount) + normalizeValue(viewsScore))/2 *  0.6);
			float otherSignalWeight = (float) (((descScore + frameBreakerScore + thumbnailScore + standardScore + domainBoost + skillScore)/6) * 0.4);
			 
			double pcWeight = normalizeValue(usageSignalWeight + otherSignalWeight);
			return pcWeight;
    	}
    	catch(Exception e){
    		throw new Exception(e);
    	}
	}

    public static final double getCollectionPCWeight(final ScoreFields rankingData) throws Exception{
    	
    	try{
    		Serializable scollectionScoreCompiled = MVEL.compileExpression(ScoreConstants.COLLECTION_SCORE_EXPRESSION);
    		
    		Map<String, Object> scollectionMvelInputs = new HashMap<String, Object>();
    		scollectionMvelInputs.put("viewsCount", rankingData.getViewsCount());
    		scollectionMvelInputs.put("hasNoDescription", rankingData.getHasNoDescription());
    	//	scollectionMvelInputs.put("descriptionLength", (StringUtils.trimToNull(rankingData.getDescription()) != null) ? rankingData.getDescription().length() : 0);
    		scollectionMvelInputs.put("hasNoThumbnail", rankingData.getHasNoThumbnail());
    		scollectionMvelInputs.put("hasNoStandard", rankingData.getHasNoStandard());
    		scollectionMvelInputs.put("isCopied", rankingData.getIsCopied());
    		scollectionMvelInputs.put("collectionItemCount", rankingData.getQuestionCount() + rankingData.getResouceCount());
    		scollectionMvelInputs.put("questionCount", rankingData.getQuestionCount());
    		scollectionMvelInputs.put("resourceCount", rankingData.getResouceCount());
    		scollectionMvelInputs.put("maxViewCount", ScoreConstants.MAX_COLLECTION_VIEWS_99PERSENT_VAL);
    		
    		VariableResolverFactory inputFactory = new MapVariableResolverFactory(scollectionMvelInputs);
    		Double scPreComputedWeight = 0.0; 
    		scPreComputedWeight = (Double) MVEL.executeExpression(scollectionScoreCompiled, inputFactory);
    		return scPreComputedWeight;
    	}
    	catch(Exception e){
    		throw new Exception(e);
    	}

	}

	protected  static Float computeDiscriptionValue (String description, int hasNoDescription) {
		Float descriptionScore = -5.0f;
		if (hasNoDescription == 0) {
			descriptionScore = 0.8f;
	 	    if (description.length() > 80) {
	 	    	descriptionScore += 0.2f;
	 	    }
	 	}
	    return descriptionScore;
	}
	
    private static Float normalizeValue(Float score) {
		if(score > 1){
			score = 1.0F;
		} 
		else if(score < 0){
			score = 0.0F;
		}
	    return score;
   }



}
