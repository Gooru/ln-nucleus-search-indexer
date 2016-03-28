package org.gooru.nucleus.search.indexers.app.processors.index.handlers;

import org.gooru.nucleus.search.indexers.app.constants.ErrorMsgConstants;
import org.gooru.nucleus.search.indexers.app.constants.EsIndex;
import org.gooru.nucleus.search.indexers.app.constants.ExecuteOperationConstants;
import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;
import org.gooru.nucleus.search.indexers.app.processors.ProcessorContext;
import org.gooru.nucleus.search.indexers.app.processors.repositories.RepoBuilder;
import org.gooru.nucleus.search.indexers.app.services.IndexService;
import org.gooru.nuclues.search.indexers.app.utils.IndexNameHolder;
import org.gooru.nuclues.search.indexers.app.utils.ValidationUtil;

import io.vertx.core.json.JsonObject;

public class ResourceIndexHandler implements IndexHandler {

	private final String indexName;

    public ResourceIndexHandler() {
    	this.indexName = getIndexName();
	}
    
	@Override
	public void indexDocument(String resourceId) throws Exception {
		try{
			ProcessorContext context = new ProcessorContext(resourceId, ExecuteOperationConstants.GET_RESOURCE);
			JsonObject result = RepoBuilder.buildIndexerRepo(context).getIndexDataContent();
			ValidationUtil.rejectIfNull(result, ErrorMsgConstants.RESOURCE_DATA_NULL);
			LOGGER.debug("RIH->indexDocument: getIndexDataContent() returned:" + result);
			IndexService.instance().indexDocuments(resourceId, indexName, getIndexType(), result);
		}  
		catch(Exception ex){
			LOGGER.error("RIH->indexDocument: Re-index failed for resource : " + resourceId +" Exception " +ex);
			throw new Exception(ex);
		}
	}

	@Override
	public void indexDocuments(JsonObject idsJson) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteIndexedDocument(String documentId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void indexDocmentPartial(JsonObject json) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	private String getIndexName(){
		return IndexNameHolder.getIndexName(EsIndex.RESOURCE);
	}
	
	private String getIndexType(){
		return IndexerConstants.TYPE_RESOURCE;
	}
	
}
