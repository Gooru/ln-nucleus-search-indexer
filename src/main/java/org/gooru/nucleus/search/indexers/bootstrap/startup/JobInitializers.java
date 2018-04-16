package org.gooru.nucleus.search.indexers.bootstrap.startup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gooru.nucleus.search.indexers.app.jobs.PopulateGutBasedAssessmentSuggestJob;
import org.gooru.nucleus.search.indexers.app.jobs.PopulateGutBasedCollectionSuggestJob;
import org.gooru.nucleus.search.indexers.app.jobs.PopulateGutBasedResourceSuggestJob;
import org.gooru.nucleus.search.indexers.app.jobs.PopulateLearningMapsTable;

public class JobInitializers implements Iterable<JobInitializer> {

  private final Iterator<JobInitializer> internalIterator;

  public JobInitializers() {
    List<JobInitializer> initializers = new ArrayList<>();
    initializers.add(PopulateGutBasedResourceSuggestJob.instance());
    initializers.add(PopulateGutBasedCollectionSuggestJob.instance());
    initializers.add(PopulateGutBasedAssessmentSuggestJob.instance());
    initializers.add(PopulateLearningMapsTable.instance());
    internalIterator = initializers.iterator();
  }

  @Override
  public Iterator<JobInitializer> iterator() {
    return new Iterator<JobInitializer>() {

      @Override
      public boolean hasNext() {
        return internalIterator.hasNext();
      }

      @Override
      public JobInitializer next() {
        return internalIterator.next();
      }

    };
  }
}
