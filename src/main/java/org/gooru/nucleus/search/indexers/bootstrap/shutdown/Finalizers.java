package org.gooru.nucleus.search.indexers.bootstrap.shutdown;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gooru.nucleus.search.indexers.app.components.KafkaRegistry;

public class Finalizers implements Iterable<Finalizer> {


  private final Iterator<Finalizer> internalIterator;

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

  public Finalizers() {
    List<Finalizer> finalizers = new ArrayList<>();
    finalizers.add(KafkaRegistry.getInstance());
    internalIterator = finalizers.iterator();
  }


}
