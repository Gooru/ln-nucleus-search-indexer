package org.gooru.nucleus.search.indexers.app.constants;

public enum IndexType {
	
	SCOLLECTION("scollection"),
	RESOURCE("resource"),
	USER("user"),
	SEARCH_QUERY("searchquery"),
	TAXONOMY("taxonomy"),
	LIBRARY("library"),
	DICTIONARY("dictionary"),
	PUBLISHER("publisher"),
	AGGREGATOR("aggregator");
	
	String type;
	
	IndexType(String type){
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}

}
