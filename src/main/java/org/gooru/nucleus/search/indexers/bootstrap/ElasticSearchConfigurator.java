package org.gooru.nucleus.search.indexers.bootstrap;

import java.util.List;

import org.elasticsearch.common.transport.TransportAddress;

public interface ElasticSearchConfigurator {

	String getClusterName();

    boolean getClientTransportSniff();

    List<TransportAddress> getTransportAddresses();
}
