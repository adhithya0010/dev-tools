package com.intellij.devtools.utils;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import pl.jalokim.propertiestojson.util.PropertiesToJsonConverter;

public class PropertiesUtils {

  private PropertiesUtils() {}

  public static String toJson(String propertiesText) {
    if (StringUtils.isEmpty(propertiesText)) {
      return "";
    }
    try {
      Map<String, String> properties = new HashMap<>();
      String[] propertyLines = propertiesText.split("\n");
      for (String propertyLine : propertyLines) {
        String[] propertyParts = propertyLine.split("=");
        String key = propertyParts[0];
        String value = propertyParts[1];
        properties.put(key, value);
      }
      return new PropertiesToJsonConverter().convertToJson(properties);
    } catch (Exception e) {
      e.printStackTrace();
      return "ERROR";
    }
  }

  public static String toYaml(String propertiesText) {
    if (StringUtils.isEmpty(propertiesText)) {
      return "";
    }
    try {
      Map<String, String> properties = new HashMap<>();
      String[] propertyLines = propertiesText.split("\n");
      for (String propertyLine : propertyLines) {
        String[] propertyParts = propertyLine.split("=");
        String key = propertyParts[0];
        String value = propertyParts[1];
        properties.put(key, value);
      }
      return JsonUtils.toYaml(new PropertiesToJsonConverter().convertToJson(properties));
    } catch (Exception e) {
      e.printStackTrace();
      return "ERROR";
    }
  }
}
