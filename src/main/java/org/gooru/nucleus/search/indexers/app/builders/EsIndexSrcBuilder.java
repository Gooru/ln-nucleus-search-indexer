package org.gooru.nucleus.search.indexers.app.builders;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CollectionRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.ContentRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.IndexRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.UserRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.index.model.CodeEo;
import org.gooru.nucleus.search.indexers.app.index.model.TaxonomyEo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author Renuka
 * 
 */
public abstract class EsIndexSrcBuilder<S, D> implements IsEsIndexSrcBuilder<S, D> {

	protected static final Logger LOGGER = LoggerFactory.getLogger(EsIndexSrcBuilder.class);
	protected static final String dateInputPatterns[] = {"yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss.SSS", "yyyy/MM/dd", "yyyy-MM" };
	protected static final String dateOutputPattern = "yyyy/MM/dd HH:mm:ss";
	
	private static final Map<String, IsEsIndexSrcBuilder<?, ?>> esIndexSrcBuilders = new HashMap<>();
	
	protected static final String IS_BUILD_INDEX = "isBuildIndex";

	static {
		registerESIndexSrcBuilders();
	}

	private static void registerESIndexSrcBuilders() {
		esIndexSrcBuilders.put(IndexType.RESOURCE.getType(), new ContentEsIndexSrcBuilder<>());
		esIndexSrcBuilders.put(IndexType.COLLECTION.getType(), new CollectionEsIndexSrcBuilder<>());		
	}
	
	public static IsEsIndexSrcBuilder<?, ?> get(String requestBuilderName) {
		if (esIndexSrcBuilders.containsKey(requestBuilderName)) {
			return esIndexSrcBuilders.get(requestBuilderName);
		} else {
			throw new RuntimeException("Oops! Invalid type : " + requestBuilderName);
		}
	}
	
	@Override
	public String buildSource(JsonObject source, D destination) throws Exception {
		return build(source, destination).toString();
	}

	protected abstract JsonObject build(JsonObject source, D destination) throws Exception;

	private static final class Repository {
		private static final ContentRepositoryImpl CONTENT_REPO = new ContentRepositoryImpl();
		private static final CollectionRepositoryImpl COLLECTION_REPO = new CollectionRepositoryImpl();
		private static final UserRepositoryImpl USER_REPO = new UserRepositoryImpl();
		private static final IndexRepositoryImpl INDEX_REPO = new IndexRepositoryImpl();
	}
	
	protected CollectionRepositoryImpl getCollectionRepo() {
		return Repository.COLLECTION_REPO;
	}
	
	protected UserRepositoryImpl getUserRepo() {
		return Repository.USER_REPO;
	}
	
	protected ContentRepositoryImpl getContentRepo() {
		return Repository.CONTENT_REPO;
	}
	
	protected IndexRepositoryImpl getIndexRepo() {
		return Repository.INDEX_REPO;
	}
	
	protected void setUser(JsonObject user, UserEo userEo) {
		userEo.setUsernameDisplay(user.getString("username", null));
		userEo.setUserId(user.getString("id"));
		userEo.setLastName(user.getString("lastname", null));
		userEo.setFirstName(user.getString("firstname", null));
		userEo.setFullName(user.getString("firstname") + " " + user.getString("lastname"));
		userEo.setEmailId(user.getString("lastname", null));
		userEo.setProfileVisibility(user.getBoolean("profileVisibility", false));
	}
	
	protected void addTaxnomy(JsonArray taxonomyArray, TaxonomyEo taxonomyEo) {
		JsonArray subjectArray = new JsonArray();
		JsonArray courseArray = new JsonArray();
		JsonArray domainArray = new JsonArray();
		JsonArray standardArray = new JsonArray();
		JsonArray learningTargetArray = new JsonArray();
		JsonObject taxonomyDataSet = new JsonObject();

		for (int index = 0; index < taxonomyArray.size(); index++) {
			CodeEo subject = new CodeEo();
			CodeEo course = new CodeEo();
			CodeEo domain = new CodeEo();
			
			String code = taxonomyArray.getString(index);
			String[] codes = code.split(IndexerConstants.HYPHEN_SEPARATOR);
			// TODO fetch label from DB
			subject.setCodeId(code.substring(0, StringUtils.ordinalIndexOf(code, "-", 1)));
			subject.setLabel("");
			course.setCodeId(code.substring(0, StringUtils.ordinalIndexOf(code, "-", 2)));
			course.setLabel("");
			domain.setCodeId(code.substring(0, StringUtils.ordinalIndexOf(code, "-", 3)));
			domain.setLabel("");

			subjectArray.add(subject.getCode());
			courseArray.add(course.getCode());
			domainArray.add(domain.getCode());

			if (codes.length >= 4) {
				if (codes.length == 4) {
					standardArray.add(code);
				} else if (codes.length == 5) {
					standardArray.add(code.substring(0, StringUtils.ordinalIndexOf(code, "-", 4)));
					learningTargetArray.add(code);
				}
			}
		}
		if (subjectArray.size() > 0) {
			taxonomyEo.setSubject(new JsonArray(subjectArray.stream().distinct().collect(Collectors.toList())));
			taxonomyEo.setCourse(new JsonArray(courseArray.stream().distinct().collect(Collectors.toList())));
			taxonomyEo.setDomain(new JsonArray(domainArray.stream().distinct().collect(Collectors.toList())));
		}
		JsonArray standards = null;
		if (standardArray.size() > 0) {
			taxonomyEo.setHasStandard(1);
			standards = new JsonArray(standardArray.stream().distinct().collect(Collectors.toList()));
			taxonomyEo.setStandards(standards);
			taxonomyEo.setLearningTargets(learningTargetArray);
		}
		// TODO fetch label from DB
		taxonomyDataSet.put(IndexerConstants.SUBJECT, new JsonArray());
		taxonomyDataSet.put(IndexerConstants.COURSE, new JsonArray());
		taxonomyDataSet.put(IndexerConstants.DOMAIN, new JsonArray());
		JsonObject curriculumTaxonomy = new JsonObject();
		curriculumTaxonomy.put(IndexerConstants.CURRICULUM_CODE, standards != null ? standards : new JsonArray()).put(IndexerConstants.CURRICULUM_DESC, new JsonArray()).put(IndexerConstants.CURRICULUM_NAME, new JsonArray());
		taxonomyDataSet.put(IndexerConstants.CURRICULUM, curriculumTaxonomy);
		taxonomyEo.setTaxonomyDataSet(taxonomyDataSet.toString());
	}
	
}
