package com.intellij.devtools.exec.converter.impl;

import static com.intellij.devtools.MessageKeys.CONVERTER_JSON_TO_PROPERTIES_NAME;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.exec.converter.Converter;
import com.intellij.devtools.locale.MessageBundle;
import com.intellij.devtools.utils.JsonUtils;
import com.intellij.devtools.utils.PropertiesUtils;
import com.intellij.json.JsonLanguage;
import com.intellij.lang.Language;
import com.intellij.lang.properties.PropertiesLanguage;
import javax.swing.Icon;

public class JsonPropertiesConverter extends Converter {

  @Override
  public String getNodeName() {
    return MessageBundle.get(CONVERTER_JSON_TO_PROPERTIES_NAME);
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
    return JsonUtils.toProperties(data);
  }

  @Override
  protected String convertFrom(String data) {
    return PropertiesUtils.toJson(data);
  }

  @Override
  protected String getFromLabel() {
    return "Json";
  }

  @Override
  protected Language getFromLanguage() {
    return JsonLanguage.INSTANCE;
  }

  @Override
  protected Language getToLanguage() {
    return PropertiesLanguage.INSTANCE;
  }

  @Override
  protected String getToLabel() {
    return "Properties";
  }
}
