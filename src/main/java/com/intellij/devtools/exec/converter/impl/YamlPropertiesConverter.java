package com.intellij.devtools.exec.converter.impl;

import static com.intellij.devtools.MessageKeys.CONVERTER_YAML_TO_PROPERTIES_NAME;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.exec.converter.Converter;
import com.intellij.devtools.locale.MessageBundle;
import com.intellij.devtools.utils.PropertiesUtils;
import com.intellij.devtools.utils.YamlUtils;
import com.intellij.lang.Language;
import com.intellij.lang.properties.PropertiesLanguage;
import javax.swing.Icon;
import org.jetbrains.yaml.YAMLLanguage;

public class YamlPropertiesConverter extends Converter {

  @Override
  public String getNodeName() {
    return MessageBundle.get(CONVERTER_YAML_TO_PROPERTIES_NAME);
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
    return YamlUtils.toProperties(data);
  }

  @Override
  protected String convertFrom(String data) {
    return PropertiesUtils.toYaml(data);
  }

  @Override
  protected String getFromLabel() {
    return "Yaml";
  }

  @Override
  protected String getToLabel() {
    return "Properties";
  }

  @Override
  protected Language getFromLanguage() {
    return YAMLLanguage.INSTANCE;
  }

  @Override
  protected Language getToLanguage() {
    return PropertiesLanguage.INSTANCE;
  }
}
