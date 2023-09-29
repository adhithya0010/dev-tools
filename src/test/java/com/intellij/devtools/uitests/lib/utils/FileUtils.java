package com.intellij.devtools.uitests.lib.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtils {

  private FileUtils() {}

  public static String getData(String filePath) {
    URL fileUrl = FileUtils.class.getClassLoader().getResource(filePath);
    try {
      if (Objects.nonNull(fileUrl)) {
        File file = new File(fileUrl.toURI());
        if (file.exists()) {
          byte[] bytes = Files.readAllBytes(Path.of(file.toURI()));
          return new String(bytes);
        }
      }
    } catch (URISyntaxException | IOException e) {
      throw new RuntimeException("File Not found");
    }
    throw new RuntimeException("File Not found");
  }
}
