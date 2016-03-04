package org.gooru.nucleus.search.indexers.bootstrap;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;

public interface TransportClientFactory {

    TransportClient create(Settings settings);

}
