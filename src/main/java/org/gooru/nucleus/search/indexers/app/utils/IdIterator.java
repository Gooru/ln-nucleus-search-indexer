package org.gooru.nucleus.search.indexers.app.utils;

/**
 * @author Renuka
 * 
 */
public abstract class IdIterator {

	private static final String SEPARATOR = ",";

	public IdIterator(String id) throws Exception {
		if (id != null) {
			String[] ids = id.split(SEPARATOR);
			for (String indexableId : ids) {
				execute(indexableId);
			}
		}
	}

	public abstract void execute(String indexableId) throws Exception;

}
