package org.gooru.nucleus.search.indexers.app.constants;

public enum ContentFormat {
	
	RESOURCE("resource"),
	COLLECTION("collection"),
	ASSESSMENT("assessment"),
	QUESTION("question");
	
	String contentFormat;
	
	private ContentFormat(String contentFormat){
		this.contentFormat = contentFormat;
	}
	
	public String getContentFormat(){
		return contentFormat;
	}
}
