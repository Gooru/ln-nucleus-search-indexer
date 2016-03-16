package org.gooru.nucleus.search.indexers.app.repositories.astyanax;

import java.util.Map;

import org.gooru.nucleus.search.indexers.app.components.CassandraRegistry;
import org.gooru.nucleus.search.indexers.app.constants.ColumnFamilyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ConsistencyLevel;
import com.netflix.astyanax.model.CqlResult;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.serializers.StringSerializer;

public class CqlServiceImpl implements CqlService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CqlServiceImpl.class);

	private static final ConsistencyLevel DEFAULT_CONSISTENCY_LEVEL = ConsistencyLevel.CL_QUORUM;

	public ColumnFamily<String, String> accessColumnFamily(String columnFamilyName) {
		return new ColumnFamily<String, String>(columnFamilyName, StringSerializer.get(), StringSerializer.get());
	}

	@Override
	public Rows<String, String> read(String cfName, String key) {
		OperationResult<CqlResult<String, String>> result = null;
		try {
			result = CassandraRegistry.getInstance().getKeyspace().prepareQuery(accessColumnFamily(cfName)).setConsistencyLevel(DEFAULT_CONSISTENCY_LEVEL)
					.withCql("SELECT * FROM " + cfName + " WHERE row_key = ?;")
					.asPreparedStatement().withStringValue(key).execute();
		} catch (ConnectionException e) {
			LOGGER.error("CQL Exception:", e);
		}
		return result.getResult().getRows();
	}
	
	@Override
	public boolean saveContentStatistics(Map<String, Object> insertData) {
		try {
			CassandraRegistry.getInstance().getKeyspace().prepareQuery(accessColumnFamily(ColumnFamilyConstants.CONTENT_STATISTICS.getColumnFamily()))
			.setConsistencyLevel(DEFAULT_CONSISTENCY_LEVEL)
			.withCql(CqlQuery.INSERT_STAT_DATA)
			.asPreparedStatement().withStringValue(insertData.get("row_key").toString())
			.withLongValue(Long.valueOf(insertData.get("views").toString()))
			.execute();
		} catch (ConnectionException e) {
			LOGGER.error("Error while storing statistics data", e);
			return false;
		}
		return true;
	}

}
