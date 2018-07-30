package org.gooru.nucleus.search.indexers.bootstrap.startup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gooru.nucleus.search.indexers.app.jobs.PopulateCompetencyContentMap;
import org.gooru.nucleus.search.indexers.app.jobs.PopulateLearningMaps;
import org.gooru.nucleus.search.indexers.app.jobs.PopulateSignatureAssessmentJob;
import org.gooru.nucleus.search.indexers.app.jobs.PopulateSignatureCollectionJob;
import org.gooru.nucleus.search.indexers.app.jobs.PopulateSignatureResourceJob;

public class JobInitializers implements Iterable<JobInitializer> {

  private final Iterator<JobInitializer> internalIterator;

  public JobInitializers() {
    List<JobInitializer> initializers = new ArrayList<>();
    initializers.add(PopulateSignatureResourceJob.instance());
    initializers.add(PopulateSignatureCollectionJob.instance());
    initializers.add(PopulateSignatureAssessmentJob.instance());
    initializers.add(PopulateLearningMaps.instance());
    initializers.add(PopulateCompetencyContentMap.instance());
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
