package org.gooru.nucleus.search.indexers.bootstrap.shutdown;

import org.gooru.nucleus.search.indexers.app.components.DataSourceRegistry;
import org.gooru.nucleus.search.indexers.app.components.ElasticSearchRegistry;
import org.gooru.nucleus.search.indexers.app.components.KafkaRegistry;
import org.gooru.nucleus.search.indexers.app.utils.UtilityManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Finalizers implements Iterable<Finalizer> {

  private final Iterator<Finalizer> internalIterator;

  public Finalizers() {
    List<Finalizer> finalizers = new ArrayList<>();
    finalizers.add(DataSourceRegistry.getInstance());
    finalizers.add(ElasticSearchRegistry.getInstance());
    finalizers.add(KafkaRegistry.getInstance());
    finalizers.add(UtilityManager.getInstance());
    internalIterator = finalizers.iterator();
  }

  @Override
  public Iterator<Finalizer> iterator() {
    return new Iterator<Finalizer>() {

      @Override
      public boolean hasNext() {
        return internalIterator.hasNext();
      }

      @Override
      public Finalizer next() {
        return internalIterator.next();
      }

    };
  }

}
