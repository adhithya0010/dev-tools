package com.intellij.devtools.exec.generator.impl;

import static com.intellij.devtools.MessageKeys.GENERATOR_UUID_NAME;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.exec.Parameter;
import com.intellij.devtools.exec.Parameter.Type;
import com.intellij.devtools.exec.ParameterGroup;
import com.intellij.devtools.exec.generator.Generator;
import com.intellij.devtools.locale.MessageBundle;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import javax.swing.Icon;

public class UUIDGenerator extends Generator {

  public static final String COUNT_PARAMETER_NAME = "count";

  @Override
  public String getNodeName() {
    return MessageBundle.get(GENERATOR_UUID_NAME);
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public OperationCategory getOperationCategory() {
    return null;
  }

  @Override
  public OperationGroup getOperationGroup() {
    return OperationGroup.GENERATOR;
  }

  @Override
  protected String generate() {
    Map<String, Object> parameterResult = getParameterResult();
    int count = (int) parameterResult.get(COUNT_PARAMETER_NAME);
    StringJoiner sj = new StringJoiner("\n");
    while (count-- > 0) {
      sj.add(UUID.randomUUID().toString());
    }
    return sj.toString();
  }

  @Override
  public List<ParameterGroup> getParameterGroups() {
    Parameter countParameter =
        Parameter.builder()
            .name(COUNT_PARAMETER_NAME)
            .label("Count")
            .type(Type.NUMBER)
            .defaultValue("1")
            .minValue(1)
            .maxValue(Long.MAX_VALUE)
            .build();
    return List.of(ParameterGroup.builder().parameters(List.of(countParameter)).build());
  }
}
