package org.gooru.nucleus.search.indexers.app.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
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
        String contentType = getUrlContentType(url, 3000);

        if (contentType != null) {
            if (contentType.contains("image") || contentType.contains("powerpoint")) {
                return text;
            }
            if (contentType.contains("pdf")) {
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
        }

        Connection connection = null;
        Document doc = null;
        try {
            connection = Jsoup.connect(url);
            doc = connection.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36").get();
            text = doc.text();
        } catch (UnsupportedMimeTypeException u) {
            LOGGER.error("UnsupportedMimeTypeException for url : {} Mimetype : {}", url, u.getMimeType());
        } catch (Exception e) {
            LOGGER.error("Error while extracting url : {} Exception :: {}", url, e.getMessage());
        }
        return text;
    }

    public static String getUrlContentType(String url, int timeout) {
        url = url.replaceFirst("^https", "http"); // Otherwise an exception may
                                                  // be thrown on invalid SSL certificates.
        String contentType = null;                                   
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            if (200 <= responseCode && responseCode <= 399) contentType = connection.getContentType();
        } catch (IOException exception) {
            LOGGER.error("Exception while checking url : {} IOException : {}", url, exception.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error while checking url : {} Exception :: {}", url, e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return contentType;
    }
}
