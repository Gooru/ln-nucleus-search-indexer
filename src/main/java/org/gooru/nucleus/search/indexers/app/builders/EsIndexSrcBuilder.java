package org.gooru.nucleus.search.indexers.app.builders;

import java.util.HashMap;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.IndexType;
import org.gooru.nucleus.search.indexers.app.index.model.UserEo;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.CollectionRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.ContentRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.IndexRepositoryImpl;
import org.gooru.nucleus.search.indexers.app.repositories.activejdbc.UserRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
}
