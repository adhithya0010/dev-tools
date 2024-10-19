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
import com.intellij.devtools.exec.PrettifyConfig;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class JsonUtils {

  public static final Scope ROOT_SCOPE = Scope.newEmptyScope();

  private JsonUtils() {}

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final YAMLMapper YAML_MAPPER = new YAMLMapper();
  private static final DefaultPrettyPrinter PRETTY_PRINTER = new DefaultPrettyPrinter();

  static {
    OBJECT_MAPPER.configure(ALLOW_COMMENTS, true);
    PRETTY_PRINTER.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

    ROOT_SCOPE.loadFunctions(Scope.class.getClassLoader());
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

  public static String prettify(String data, PrettifyConfig prettifyConfig) {
    if (StringUtils.isEmpty(data)) {
      return null;
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      DefaultPrettyPrinter.Indenter indenter =
          new DefaultIndenter(
              StringUtils.repeat(' ', prettifyConfig.getIndentLength()), DefaultIndenter.SYS_LF);
      DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
      prettyPrinter.indentObjectsWith(indenter);

      JsonNode object = objectMapper.readTree(data);
      return objectMapper.writer(prettyPrinter).writeValueAsString(object);
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

  public static String executeJQ(String query, String data) {
    try {
      // JsonQuery#compile(...) parses and compiles a given expression. The resulting JsonQuery
      // instance
      // is immutable and thread-safe. It should be reused as possible if you repeatedly use the
      // same expression.
      JsonQuery q = JsonQuery.compile(query);

      // You need a JsonNode to use as an input to the JsonQuery. There are many ways you can grab a
      // JsonNode.
      // In this example, we just parse a JSON text into a JsonNode.
      JsonNode in = OBJECT_MAPPER.readTree(data);

      // Finally, JsonQuery#apply(...) executes the query with given input and returns a list of
      // JsonNode.
      // The childScope will not be modified by this call because it internally creates a child
      // scope as necessary.
      final List<JsonNode> out = q.apply(ROOT_SCOPE, in);
      if (CollectionUtils.isEmpty(out)) {
        return "";
      }
      JsonNode result = out.get(0);
      return result.toPrettyString();
    } catch (JsonProcessingException e) {
      return "ERROR";
    }
  }
}
