package org.gooru.nuclues.search.indexers.app.repositories.activejdbc;

import java.util.List;
import java.util.Map;

public interface IndexRepository {

	List<Map> getMetadata(String referenceIds);

}
