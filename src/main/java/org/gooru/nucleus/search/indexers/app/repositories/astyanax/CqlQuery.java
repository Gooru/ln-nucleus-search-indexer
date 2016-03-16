package org.gooru.nucleus.search.indexers.app.repositories.astyanax;

public class CqlQuery {

	public static final String INSERT_STAT_DATA = "INSERT INTO content_statistics (row_key, views) VALUES (?, ?);";
}
