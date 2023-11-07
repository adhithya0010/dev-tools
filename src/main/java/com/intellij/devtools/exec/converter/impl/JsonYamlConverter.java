package com.intellij.devtools.exec.converter.impl;

import static com.intellij.devtools.MessageKeys.CONVERTER_JSON_TO_YAML_NAME;

import com.intellij.devtools.exec.converter.Converter;
import com.intellij.devtools.locale.MessageBundle;
import com.intellij.devtools.utils.JsonUtils;
import com.intellij.devtools.utils.YamlUtils;
import com.intellij.json.JsonLanguage;
import com.intellij.lang.Language;
import org.jetbrains.yaml.YAMLLanguage;

public class JsonYamlConverter extends Converter {

  @Override
  public String getNodeName() {
    return MessageBundle.get(CONVERTER_JSON_TO_YAML_NAME);
  }

  @Override
  protected String convertTo(String data) {
    return JsonUtils.toYaml(data);
  }

  @Override
  protected String convertFrom(String data) {
    return YamlUtils.toJson(data);
  }

  @Override
  protected String getFromLabel() {
    return "Json";
  }

  @Override
  protected String getToLabel() {
    return "Yaml";
  }

  @Override
  protected Language getFromLanguage() {
    return JsonLanguage.INSTANCE;
  }

  @Override
  protected Language getToLanguage() {
    return YAMLLanguage.INSTANCE;
  }
}
