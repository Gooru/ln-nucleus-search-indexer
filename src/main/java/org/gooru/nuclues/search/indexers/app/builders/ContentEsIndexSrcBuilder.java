package org.gooru.nuclues.search.indexers.app.builders;

import java.util.Date;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nuclues.search.indexers.app.index.model.AnswerEo;
import org.gooru.nuclues.search.indexers.app.index.model.ContentEio;
import org.gooru.nuclues.search.indexers.app.index.model.HintEo;
import org.gooru.nuclues.search.indexers.app.index.model.QuestionEo;
import org.gooru.nuclues.search.indexers.app.index.model.UserEo;
import org.gooru.nuclues.search.indexers.app.repositories.activejdbc.UserRepositoryImpl;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author SearchTeam
 * 
 */
public class ContentEsIndexSrcBuilder<S extends JsonObject, D extends ContentEio> extends EsIndexSrcBuilder<S, D> {

	@Override
	public JsonObject build(JsonObject source, D contentEo) {
		contentEo.setId(source.getString(EntityAttributeConstants.ID));
		contentEo.setUrl(source.getString(EntityAttributeConstants.URL, null));
		contentEo.setTitle(source.getString(EntityAttributeConstants.TITLE, null));
		contentEo.setDescription(source.getString(EntityAttributeConstants.DESCRIPTION, null));
		contentEo.setContentFormat(this.getName());
		contentEo.setIndexUpdatedTime(new Date(System.currentTimeMillis()));
		contentEo.setCreatedAt(source.getString(EntityAttributeConstants.CREATED_AT));
		contentEo.setUpdatedAt(source.getString(EntityAttributeConstants.UPDATED_AT));
		contentEo.setOriginalContentId(source.getString(EntityAttributeConstants.ORIGINAL_CONTENT_ID, null));
		contentEo.setParentContentId(source.getString(EntityAttributeConstants.PARENT_CONTENT_ID, null));
		contentEo.setPublishDate(source.getString(EntityAttributeConstants.PUBLISH_DATE, null));
		contentEo.setPublishStatus(source.getString(EntityAttributeConstants.PUBLISH_STATUS, null));
		contentEo.setShortTitle(source.getString(EntityAttributeConstants.SHORT_TITLE, null));
		contentEo.setNarration(source.getString(EntityAttributeConstants.NARRATION, null));
		contentEo.setThumbnail(source.getString(EntityAttributeConstants.THUMBNAIL, null));
		contentEo.setCollectionId(source.getString(EntityAttributeConstants.COLLECTION_ID, null));
		contentEo.setIsCopyrightOwner(source.getBoolean(EntityAttributeConstants.IS_COPYRIGHT_OWNER, null));
		contentEo.setVisibleOnProfile(source.getBoolean(EntityAttributeConstants.VISIBLE_ON_PROFILE, null));
		contentEo.setContentFormat(source.getString(EntityAttributeConstants.CONTENT_FORMAT, null));
		try {
			String originalCreatorId = source.getString(EntityAttributeConstants.ORIGINAL_CREATOR_ID, null);
			if (originalCreatorId != null) {
				UserEo orginalCreator = new UserEo();
				setUser(getUserRepo().getUser(originalCreatorId), orginalCreator);
				contentEo.setOriginalCreator(orginalCreator.getUser());
			}

			String creatorId = source.getString(EntityAttributeConstants.CREATOR_ID, null);
			if (creatorId != null) {
				UserEo creator = new UserEo();
				setUser(getUserRepo().getUser(creatorId), creator);
				contentEo.setCreator(creator.getUser());
			}

			String contentFormat = source.getString(EntityAttributeConstants.CONTENT_SUB_FORMAT, null);
			contentEo.setContentSubFormat(contentFormat);
			String contentSubTypeEscaped = null;
			if (contentFormat.contains("-")) {
				contentSubTypeEscaped = contentFormat.replace("-", "");
			} else if (contentFormat.contains("_")) {
				contentSubTypeEscaped = contentFormat.replace("_", "");
			} else {
				contentSubTypeEscaped = contentFormat.replace("/", "");
			}
			contentEo.setContentSubFormatEscaped(contentSubTypeEscaped);

			String copyrightOwnerId = source.getString(EntityAttributeConstants.COPYRIGHT_OWNER, null);
			if (copyrightOwnerId != null) {
				UserEo copyrightQwner = new UserEo();
				setUser(getUserRepo().getUser(copyrightOwnerId), copyrightQwner);
				contentEo.setCopyrightOwner(copyrightQwner.getUser());
			}

			QuestionEo questionEo = new QuestionEo();
			JsonArray answerArray = new JsonArray(source.getString(EntityAttributeConstants.ANSWER, null));
			if (answerArray != null && answerArray.size() > 0) {
				AnswerEo answerEo = new AnswerEo();
				StringBuilder answerText = new StringBuilder();
				for (int index = 0; index < answerArray.size(); index++) {
					JsonObject answerObject = answerArray.getJsonObject(index);
					if (answerObject.getString(EntityAttributeConstants.ANSWER_TEXT, null) != null) {
						if (answerText.length() > 0) {
							answerText.append(" ~~ ");
						}
						answerText.append(answerObject.getString(EntityAttributeConstants.ANSWER_TEXT, null));
					}
				}
				answerEo.setAnswerText(answerText.toString());
				questionEo.setAnswer(answerEo.getAnswer());
			}
			JsonObject hintExplanationDetail = source.getJsonObject(EntityAttributeConstants.HINT_EXPLANATION_DETAIL, null);
			if (hintExplanationDetail != null) {
				JsonArray hintArray = hintExplanationDetail.getJsonArray(EntityAttributeConstants.HINTS, null);
				if (hintArray != null && hintArray.size() > 0) {
					HintEo hintEo = new HintEo();
					StringBuilder hintText = new StringBuilder();
					int hintCount = 0;
					for (int index = 0; index < answerArray.size(); index++) {
						JsonObject hintObject = hintArray.getJsonObject(index);
						if (hintObject.getString(EntityAttributeConstants.HINT, null) != null) {
							hintCount++;
							if (hintText.length() > 0) {
								hintText.append(" ~~ ");
							}
							hintText.append(hintObject.getString(EntityAttributeConstants.HINT, null));
						}
					}
					hintEo.setHintText(hintText.toString());
					hintEo.setHintCount(hintCount);
					questionEo.setHint(hintEo.getHint());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*//TODO Add logic to store below details
		contentEo.setMetadata(metadata);
		contentEo.setStatistics(statistics);
		contentEo.setCollectionIds(collectionIds);
		contentEo.setCollectionTitles(collectionTitles);
		contentEo.setGrade(grade);
		JsonArray sourceTaxonomy = source.getJsonArray(EntityAttributeConstants.TAXONOMY, null);
		if (sourceTaxonomy != null && sourceTaxonomy.size() > 0) {
			TaxonomyEo taxonomyEo = new TaxonomyEo();
			contentEo.setTaxonomy(taxonomyEo.getTaxonomyJson());
		}*/
		return contentEo.getContentJson();
	}
	
	private void setUser(JsonObject user, UserEo userEo) {
		userEo.setUsernameDisplay(user.getString("username", null));
		userEo.setUserId(user.getString("id"));
		userEo.setLastName(user.getString("lastname", null));
		userEo.setFirstName(user.getString("firstname", null));
		userEo.setFullName(user.getString("firstname") + " " + user.getString("lastname"));
		userEo.setEmailId(user.getString("lastname", null));
		userEo.setProfileVisibility(user.getBoolean("profileVisibility", false));
	}
	
	@Override
	public String getName() {
		return IndexType.RESOURCE.getType();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String buildSource(JsonObject source) {
		return buildSource(source, (D) new ContentEio());
	}

	private static final class Repository {
		private static final UserRepositoryImpl USER_REPO = new UserRepositoryImpl();
	}
	
	private UserRepositoryImpl getUserRepo() {
		return Repository.USER_REPO;
	}
	
}
