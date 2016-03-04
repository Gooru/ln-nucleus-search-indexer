/**
 * 
 */
package org.gooru.nucleus.search.indexers.app.constants;

/**
 * @author SearchTeam
 *
 */
public enum EsIndex {
	
	RESOURCE("resource"),
	SCOLLECTION("scollection"),
	TAXONOMY("taxonomy"),
	LIBRARY("library"),
	SEARCH_QUERY("searchquery"),
	DICTIONARY("dictionary"),
	USER("user"),
    CONTENT_PROVIDER("content_provider", new String[] {"publisher","aggregator"});

	private String name;
	
	private String[] types;
	
	/**
	 * 
	 */
	private EsIndex(String name, String[] types) {
		this.name = name;
		this.types = types;
	}
	
	private EsIndex(String name) {
		this.name = name;
		this.types = new String[]{name};
	}

	public String getName() {
		return name;
	}

	public String[] getTypes() {
		return types;
	}
}
