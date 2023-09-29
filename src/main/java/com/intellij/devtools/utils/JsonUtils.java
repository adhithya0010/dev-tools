package com.intellij.devtools.utils;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import org.apache.commons.lang3.StringUtils;

public class JsonUtils {

  private JsonUtils() {}

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final YAMLMapper YAML_MAPPER = new YAMLMapper();
  private static final DefaultPrettyPrinter PRETTY_PRINTER = new DefaultPrettyPrinter();

  static {
    OBJECT_MAPPER.configure(ALLOW_COMMENTS, true);
    PRETTY_PRINTER.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
  }

  public static String minify(String data) {
    if (StringUtils.isEmpty(data)) {
      return null;
    }
    try {
      Object object = OBJECT_MAPPER.readValue(data, Object.class);
      return OBJECT_MAPPER.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "ERROR";
    }
  }

  public static String prettify(String data) {
    if (StringUtils.isEmpty(data)) {
      return null;
    }
    try {
      JsonNode object = OBJECT_MAPPER.readTree(data);
      return OBJECT_MAPPER.writer(PRETTY_PRINTER).writeValueAsString(object);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "ERROR";
    }
  }

  public static String toYaml(String data) {
    if (StringUtils.isEmpty(data)) {
      return "";
    }
    try {
      JsonNode jsonNodeTree = OBJECT_MAPPER.readTree(data);
      return YAML_MAPPER.writer(PRETTY_PRINTER).writeValueAsString(jsonNodeTree);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "ERROR";
    }
  }

  public static String toProperties(String data) {
    if (StringUtils.isEmpty(data)) {
      return "";
    }
    try {
      JsonNode jsonNode = OBJECT_MAPPER.readTree(data);
      Map<String, String> properties = new LinkedHashMap<>();
      toProperties("", jsonNode, properties);
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

  public static void toProperties(String path, JsonNode jsonNode, Map<String, String> properties) {
    if (jsonNode.isObject()) {
      ObjectNode objectNode = (ObjectNode) jsonNode;
      Iterator<Entry<String, JsonNode>> iterator = objectNode.fields();
      while (iterator.hasNext()) {
        Entry<String, JsonNode> next = iterator.next();
        String currentPath = StringUtils.isBlank(path) ? next.getKey() : path + "." + next.getKey();
        toProperties(currentPath, next.getValue(), properties);
      }
    } else if (jsonNode.isArray()) {
      ArrayNode arrayNode = (ArrayNode) jsonNode;
      for (int i = 0; i < arrayNode.size(); i++) {
        String currentPath = StringUtils.isBlank(path) ? ("[" + i + "]") : path + ("[" + i + "]");
        toProperties(currentPath, arrayNode.get(i), properties);
      }
    } else if (jsonNode.isValueNode()) {
      ValueNode valueNode = (ValueNode) jsonNode;
      properties.put(path, valueNode.asText());
    }
  }
}
