package org.gooru.nucleus.search.indexers.app.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Renuka
 */
public final class EsMappingUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(EsMappingUtil.class);

  private EsMappingUtil() {
    throw new AssertionError();
  }

  public static String getMappingConfig(String indexType) {
    return getConfig(indexType, "mappings");
  }

  public static String getSettingConfig(String indexType) {
    return getConfig(indexType, "settings");
  }

  public static String getConfig(String indexType, String configFile) {
    String content = null;
    String settingsPath = "config/index-v2/" + indexType + "/_" + configFile + ".json";
    try {
      InputStream resourceStream = EsMappingUtil.class.getClassLoader().getResourceAsStream(settingsPath);
      content = readFileAsString(resourceStream);
    } catch (Exception exception) {
      LOGGER.debug("Exception while reading mapping files from config");
      exception.printStackTrace();
    }
    return content;
  }

  private static String readFileAsString(InputStream is) {

    BufferedReader br = null;
    StringBuilder sb = new StringBuilder();
    String line;
    try {
      br = new BufferedReader(new InputStreamReader(is));
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      LOGGER.debug("IOException while reading mapping files from config");
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          LOGGER.debug("IOException while reading mapping files from config");
          e.printStackTrace();
        }
      }
    }
    return sb.toString();
  }

}
