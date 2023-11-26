package com.intellij.devtools.exec;

import com.intellij.devtools.exec.converter.impl.Base64Encoder;
import com.intellij.devtools.exec.converter.impl.JsonPropertiesConverter;
import com.intellij.devtools.exec.converter.impl.JsonYamlConverter;
import com.intellij.devtools.exec.converter.impl.TextEscaper;
import com.intellij.devtools.exec.converter.impl.UrlEncoder;
import com.intellij.devtools.exec.converter.impl.YamlPropertiesConverter;
import com.intellij.devtools.exec.formatter.impl.graphql.GraphqlMinifier;
import com.intellij.devtools.exec.formatter.impl.graphql.GraphqlPrettifier;
import com.intellij.devtools.exec.formatter.impl.json.JsonMinifier;
import com.intellij.devtools.exec.formatter.impl.json.JsonPrettifier;
import com.intellij.devtools.exec.formatter.impl.xml.XmlMinifier;
import com.intellij.devtools.exec.formatter.impl.xml.XmlPrettifier;
import com.intellij.devtools.exec.generator.impl.HashGenerator;
import com.intellij.devtools.exec.generator.impl.UUIDGenerator;
import com.intellij.devtools.exec.misc.text.DuplicateRemover;
import com.intellij.devtools.exec.misc.text.LinesSort;
import com.intellij.devtools.exec.misc.text.Regex;
import com.intellij.devtools.exec.misc.text.TextDiff;
import com.intellij.devtools.exec.misc.time.TimeFormatter;
import com.intellij.devtools.exec.misc.web.MockServer;
import java.util.ArrayList;
import java.util.List;

public class OperationFactory {

  private static OperationFactory instance = null;

  private OperationFactory() {}

  public static OperationFactory getInstance() {
    if (instance == null) {
      instance = new OperationFactory();
    }
    return instance;
  }

  public List<Operation> getAllOperations() {
    List<Operation> operations = new ArrayList<>();
    operations.addAll(getAllFormatters());
    operations.addAll(getAllConverters());
    operations.addAll(getAllEncoders());
    operations.addAll(getAllGenerators());
    operations.addAll(getAllMiscOperations());
    return operations;
  }

  public List<Operation> getAllConverters() {
    return List.of(
        new JsonPropertiesConverter(),
        new JsonYamlConverter(),
        new YamlPropertiesConverter(),
        new TextEscaper());
  }

  public List<Operation> getAllEncoders() {
    return List.of(new Base64Encoder(), new UrlEncoder());
  }

  public List<Operation> getAllFormatters() {
    return List.of(
        new JsonPrettifier(),
        new JsonMinifier(),
        new XmlPrettifier(),
        new XmlMinifier(),
        new GraphqlPrettifier(),
        new GraphqlMinifier());
  }

  public List<Operation> getAllGenerators() {
    return List.of(new UUIDGenerator(), new HashGenerator());
  }

  public List<Operation> getAllTextOperations() {
    return List.of(new TextDiff(), new DuplicateRemover(), new Regex(), new LinesSort());
  }

  public List<Operation> getAllTimeOperations() {
    return List.of(new TimeFormatter());
  }

  public List<Operation> getAllMiscOperations() {
    return List.of(
        new MockServer(), new TextDiff(), new DuplicateRemover(), new Regex(), new LinesSort());
  }
}
