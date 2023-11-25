package com.intellij.devtools.exec.converter.impl;

import com.intellij.devtools.exec.converter.Converter;
import com.intellij.devtools.utils.Base64Utils;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.PlainTextLanguage;

public class Base64Encoder extends Converter {

  public Base64Encoder() {
    super();
  }

  @Override
  public String getNodeName() {
    return "Base64";
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
