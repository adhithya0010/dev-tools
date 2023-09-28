package com.intellij.devtools.utils;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import org.apache.commons.lang3.StringUtils;

public class YamlUtils {

  private YamlUtils() {
  }

  public static final YAMLMapper YAML_MAPPER = new YAMLMapper();
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final DefaultPrettyPrinter PRETTY_PRINTER = new DefaultPrettyPrinter();

  static {
    OBJECT_MAPPER.configure(ALLOW_COMMENTS, true);
    PRETTY_PRINTER.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
  }

  public static String toJson(String data) {
    if(StringUtils.isEmpty(data)) {
      return "";
    }
    try {
      JsonNode jsonNode = YAML_MAPPER.readTree(data);
      return OBJECT_MAPPER.writer(PRETTY_PRINTER).writeValueAsString(jsonNode);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "ERROR";
    }
  }

  public static String toProperties(String data) {
    if(StringUtils.isEmpty(data)) {
      return "";
    }
    try {
      JsonNode jsonNode = YAML_MAPPER.readTree(data);
      Map<String, String> properties = new LinkedHashMap<>();
      JsonUtils.toProperties("", jsonNode, properties);
      List<String> propertyLines = new ArrayList<>();
      properties.forEach((k, v) -> propertyLines.add(String.format("%s=%s", k, v)));
      StringJoiner lineJoiner = new StringJoiner("\n");
      propertyLines.forEach(lineJoiner::add);
      return lineJoiner.toString();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "ERROR";
    }
  }
}
