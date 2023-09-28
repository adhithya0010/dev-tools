package com.intellij.devtools.exec.converter.impl;

import static com.intellij.devtools.MessageKeys.CONVERTER_TEXT_ESCAPER_NAME;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.exec.Parameter;
import com.intellij.devtools.exec.Parameter.Type;
import com.intellij.devtools.exec.ParameterGroup;
import com.intellij.devtools.exec.converter.Converter;
import com.intellij.devtools.locale.MessageBundle;
import com.intellij.devtools.utils.TextUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;

public class TextEscaper extends Converter {

  @Override
  public String getNodeName() {
    return MessageBundle.get(CONVERTER_TEXT_ESCAPER_NAME);
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
    return OperationGroup.CONVERTER;
  }

  @Override
  protected String convertTo(String data) {
    Map<String, Object> parameterResult = getParameterResult();
    return TextUtils.unescapeText(data, (String) parameterResult.getOrDefault("type", "java"));
  }

  @Override
  protected String convertFrom(String data) {
    Map<String, Object> parameterResult = getParameterResult();
    return TextUtils.escapeText(data, (String) parameterResult.getOrDefault("type", "java"));
  }

  @Override
  protected String getFromLabel() {
    return "Escaped Text";
  }

  @Override
  protected String getToLabel() {
    return "Unescaped Text";
  }

  @Override
  public List<ParameterGroup> getParameterGroups() {
    Parameter typeParameter = Parameter.builder()
        .name("type")
        .label("Type")
        .type(Type.SELECT)
        .values(Arrays.asList("Java", "HTML", "CSV", "XML", "Javascript"))
        .defaultValue("Java")
        .build();
    return List.of(ParameterGroup.builder()
            .parameters(List.of(typeParameter))
        .build());
  }
}
