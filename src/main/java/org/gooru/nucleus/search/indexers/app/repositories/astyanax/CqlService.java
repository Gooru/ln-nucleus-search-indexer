package org.gooru.nucleus.search.indexers.app.repositories.astyanax;

import java.util.Map;

import com.netflix.astyanax.model.Rows;

public interface CqlService {
	
    Rows<String, String> read(String cfName, String key);
    
    boolean saveContentStatistics(Map<String, Object> insertData);
    
}
