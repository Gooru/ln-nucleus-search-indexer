package org.gooru.nuclues.search.indexers.app.builders;

import java.util.Date;

import org.gooru.nucleus.search.indexers.app.constants.EntityAttributeConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nuclues.search.indexers.app.index.model.CollectionEio;
import org.gooru.nuclues.search.indexers.app.index.model.TaxonomyEo;
import org.gooru.nuclues.search.indexers.app.index.model.UserEo;
import org.gooru.nuclues.search.indexers.app.repositories.activejdbc.UserRepositoryImpl;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author SearchTeam
 * 
 */
public class CollectionEsIndexSrcBuilder<S extends JsonObject, D extends CollectionEio> extends EsIndexSrcBuilder<S, D> {
	
	@Override
	public JsonObject build(JsonObject source, D collectionEio) {
		collectionEio.setId(source.getString(EntityAttributeConstants.ID));
		collectionEio.setUrl(source.getString(EntityAttributeConstants.URL, null));
		collectionEio.setTitle(source.getString(EntityAttributeConstants.TITLE, null));
		collectionEio.setContentFormat(this.getName());
		collectionEio.setIndexUpdatedTime(new Date());
		collectionEio.setCollaboratorIds(source.getJsonArray(EntityAttributeConstants.COLLABORATOR));
		collectionEio.setCreatedAt(source.getString(EntityAttributeConstants.CREATED_AT));
		collectionEio.setCreatedAt(source.getString(EntityAttributeConstants.UPDATED_AT));
		collectionEio.setOriginalCollectionId(source.getString(EntityAttributeConstants.ORIGINAL_COLLECTION_ID, null));
		collectionEio.setParentCollectionId(source.getString(EntityAttributeConstants.PARENT_COLLECTION_ID, null));
		collectionEio.setPublishDate(source.getString(EntityAttributeConstants.PUBLISH_DATE, null));
		collectionEio.setPublishStatus(source.getString(EntityAttributeConstants.PUBLISH_STATUS, null));
		collectionEio.setContentFormat(source.getString(EntityAttributeConstants.CONTENT_FORMAT, null));
		collectionEio.setThumbnail(source.getString(EntityAttributeConstants.THUMBNAIL, null));
		collectionEio.setLearningObjective(source.getString(EntityAttributeConstants.LEARNING_OBJECTIVE, null));
		collectionEio.setAudience(source.getJsonArray(EntityAttributeConstants.AUDIENCE, null));
		collectionEio.setOrientation(source.getString(EntityAttributeConstants.ORIENTATION, null));
		collectionEio.setVisibleOnProfile(source.getBoolean(EntityAttributeConstants.VISIBLE_ON_PROFILE, null));
		collectionEio.setGradingType(source.getString(EntityAttributeConstants.GRADING, null));
		try {
			String originalCreatorId = source.getString(EntityAttributeConstants.ORIGINAL_CREATOR_ID, null);
			if (originalCreatorId != null) {
				UserEo orginalCreator = new UserEo();
				setUser(getUserRepo().getUser(originalCreatorId), orginalCreator);
				collectionEio.setOriginalCreator(orginalCreator.getUser());
			}
			String creatorId = source.getString(EntityAttributeConstants.CREATOR_ID, null);
			if (creatorId != null) {
				UserEo creator = new UserEo();
				setUser(getUserRepo().getUser(creatorId), creator);
				collectionEio.setCreator(creator.getUser());
			}
			String ownerId = source.getString(EntityAttributeConstants.OWNER_ID, null);
			if (ownerId != null) {
				UserEo owner = new UserEo();
				setUser(getUserRepo().getUser(ownerId), owner);
				collectionEio.setCreator(owner.getUser());
			}

			JsonArray sourceTaxonomy = source.getJsonArray(EntityAttributeConstants.TAXONOMY);
			if (sourceTaxonomy != null && sourceTaxonomy.size() > 0) {
				TaxonomyEo taxonomyEo = new TaxonomyEo();
				taxonomyEo.setHasStandard(1);
				collectionEio.setTaxonomy(taxonomyEo.getTaxonomyJson());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*//TODO Add logic to store below details
		collectionEio.setMetadata(metadata);
		collectionEio.setStatistics(statistics);
		collectionEio.setCollaboratorIds(collaboratorIds);
		collectionEio.setResourceIds(resourceIds);
		collectionEio.setResourceTitles(resourceTitles);
		collectionEio.setCollectionContents(collectionContents);
		*/
		return collectionEio.getCollectionJson();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String buildSource(JsonObject source) {
		return buildSource(source, (D) new CollectionEio());
	}

	@Override
	public String getName() {
		return IndexType.COLLECTION.getType();
	}
	
	private static final class Repository {
		private static final UserRepositoryImpl USER_REPO = new UserRepositoryImpl();
	}
	
	private UserRepositoryImpl getUserRepo() {
		return Repository.USER_REPO;
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

}
