package org.gooru.nucleus.search.indexers.bootstrap.startup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gooru.nucleus.search.indexers.app.components.CassandraClient;
import org.gooru.nucleus.search.indexers.app.components.ElasticSearchClient;
import org.gooru.nucleus.search.indexers.app.components.KafkaRegistry;

public class Initializers implements Iterable<Initializer> {

	private final Iterator<Initializer> internalIterator;

	@Override
	public Iterator<Initializer> iterator() {
		return new Iterator<Initializer>() {

			@Override
			public boolean hasNext() {
				return internalIterator.hasNext();
			}

			@Override
			public Initializer next() {
				return internalIterator.next();
			}

		};
	}

	public Initializers() {
		List<Initializer> initializers = new ArrayList<>();
		initializers.add(KafkaRegistry.getInstance());
		initializers.add(ElasticSearchClient.getInstance());
		initializers.add(CassandraClient.getInstance());
		internalIterator = initializers.iterator();
	}

}
