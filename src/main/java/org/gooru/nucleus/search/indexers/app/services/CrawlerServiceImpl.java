package org.gooru.nucleus.search.indexers.app.services;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;

public class CrawlerServiceImpl implements CrawlerService {

  protected static final Logger LOGGER = LoggerFactory.getLogger(CrawlerServiceImpl.class);

  public String extractUrl(String url) {
    String text = null;
    if (url.contains(".png") || url.contains(".jpg") || url.contains(".jpeg") || url.contains(".ppt")) {
      return text;
    }
    if (url.contains(".pdf")) {
      PdfReader reader;
      try {
        reader = new PdfReader(url);
        int numPages = reader.getNumberOfPages();
        int i = 0;
        for (i = 1; i <= numPages; i++) {
          String str = PdfTextExtractor.getTextFromPage(reader, i, new SimpleTextExtractionStrategy());
          text += str;
        }
        return text;
      } catch (Exception e) {
        LOGGER.error("Error while extracting Pdf url", e.getMessage());
      }
      return text;
    }
    Connection connection = null;
    Document doc = null;
    try {
      connection = Jsoup.connect(url);
      doc = connection.get();
    } catch (Exception e) {
      LOGGER.error("Error while extracting url", e.getMessage());
      return text;
    }
    text = doc.text();
    return text;
  }
}
