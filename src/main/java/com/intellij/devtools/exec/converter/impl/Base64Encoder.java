package com.intellij.devtools.exec.converter.impl;

import static com.intellij.devtools.MessageKeys.ENCODER_BASE_64_NAME;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.exec.converter.Converter;
import com.intellij.devtools.locale.MessageBundle;
import com.intellij.devtools.utils.Base64Utils;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import javax.swing.Icon;

public class Base64Encoder extends Converter {

  public Base64Encoder() {
    super();
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public String getNodeName() {
    return MessageBundle.get(ENCODER_BASE_64_NAME);
  }

  @Override
  public OperationCategory getOperationCategory() {
    return OperationCategory.BASE_64;
  }

  @Override
  public OperationGroup getOperationGroup() {
    return OperationGroup.ENCODER;
  }

  @Override
  protected String convertFrom(String data) {
    return Base64Utils.decode(data);
  }

  @Override
  protected String convertTo(String data) {
    return Base64Utils.encode(data);
  }

  @Override
  protected String getFromLabel() {
    return "Plain";
  }

  @Override
  protected Language getFromLanguage() {
    return PlainTextLanguage.INSTANCE;
  }

  @Override
  protected Language getToLanguage() {
    return PlainTextLanguage.INSTANCE;
  }

  @Override
  protected String getToLabel() {
    return "Encoded";
  }
}
