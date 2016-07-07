package org.gooru.nucleus.search.indexers.app.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import org.gooru.nucleus.search.indexers.app.constants.IndexerConstants;

public class CSVFileGenerator {

  private static final String DEFAULT_FILE_NAME = "indexer";
  private static final String FILE_REAL_PATH = System.getProperty(IndexerConstants.USER_DIR) + File.separator;

  public File generateCSVFile(boolean isNewFile, String fileName, List<Map<String, Object>> resultSet) throws IOException {

    boolean headerColumns = false;
    File csvfile = new File(setFilePath(fileName));
    PrintStream stream = generatePrintStream(isNewFile, csvfile);
    for (Map<String, Object> map : resultSet) {
      writeAsCsvToStream(map, stream, headerColumns);
      headerColumns = true;
    }
    writeToFile(stream);
    return csvfile;
  }

  public File generateCSVFile(boolean isNewFile, String fileName, Map<String, Object> resultSet) throws IOException {

    File csvfile = new File(setFilePath(fileName));
    PrintStream stream = generatePrintStream(isNewFile, csvfile);
    writeAsCsvToStream(resultSet, stream, isNewFile);
    writeToFile(stream);
    return csvfile;
  }

  public void includeEmptyLine(boolean isNewFile, String fileName, int lineCount) throws FileNotFoundException {

    File csvfile = new File(setFilePath(fileName));
    PrintStream stream = generatePrintStream(isNewFile, csvfile);
    for (int i = 0; i < lineCount; i++) {
      stream.println(IndexerConstants.STRING_EMPTY);
    }
    writeToFile(stream);
  }

  private Object appendDQ(Object key) {
    return IndexerConstants.DOUBLE_QUOTES + key + IndexerConstants.DOUBLE_QUOTES;
  }

  private void writeAsCsvToStream(Map<String, Object> map, PrintStream stream, boolean includeHeaderColumns) {
    if (includeHeaderColumns) {
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        stream.print(appendDQ(entry.getKey()) + IndexerConstants.COMMA);
      }
      stream.println(IndexerConstants.STRING_EMPTY);
    }
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      stream.print(appendDQ(entry.getValue()) + IndexerConstants.COMMA);
    }
    stream.println(IndexerConstants.STRING_EMPTY);
  }

  private PrintStream generatePrintStream(boolean isNewFile, File file) throws FileNotFoundException {
    PrintStream stream;
    if (isNewFile) {
      stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file, false)));
    } else {
      stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file, true)));
    }
    return stream;
  }

  private void writeToFile(PrintStream stream) {
    stream.flush();
    stream.close();
  }

  private String setFilePath(String file) {

    String fileName = FILE_REAL_PATH;
    if (file != null && (!file.isEmpty())) {
      fileName += file;
    } else {
      fileName += DEFAULT_FILE_NAME;
    }
    return fileName + IndexerConstants.CSV_EXT;
  }

}
