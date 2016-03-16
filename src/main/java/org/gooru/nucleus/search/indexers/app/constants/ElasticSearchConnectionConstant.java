package org.gooru.nucleus.search.indexers.app.constants;


/**
 * @author Search Team
 * 
 */
public enum ElasticSearchConnectionConstant {

	INDEX_PREFIX_PART("elasticsearch.indexPrefix", "gooru_"),
	
	INDEX_MIDDLE_PART("elasticsearch.indexMiddle", "local_"),

	INDEX_SUFFIX_PART("elasticsearch.indexSuffix", "_v2"),

	CLUSTER_NAME("elasticsearch.clustername", "elasticsearch"),

	HOST("elasticsearch.host", "127.0.0.1:9200"),

	CLIENT_TRANSPORT_SNIFF("elasticsearch.client.transport.sniff", "true");
		
	String defaultValue;

	String key;

	private ElasticSearchConnectionConstant(String key,
			String defaultValue) {
		this.defaultValue = defaultValue;
		this.key = key;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

}
