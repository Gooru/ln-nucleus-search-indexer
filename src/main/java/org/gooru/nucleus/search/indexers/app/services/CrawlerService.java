package org.gooru.nucleus.search.indexers.app.services;

public interface CrawlerService {
  
  static CrawlerService instance() {
    return new CrawlerServiceImpl();
  }

  String extractUrl(String url);
}
