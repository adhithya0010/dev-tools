package com.intellij.devtools.exec.converter.impl;

import static com.intellij.devtools.MessageKeys.ENCODER_URL_NAME;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.converter.Converter;
import com.intellij.devtools.locale.MessageBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlEncoder extends Converter {

  public UrlEncoder() {
    super();
  }

  @Override
  public String getNodeName() {
    return MessageBundle.get(ENCODER_URL_NAME);
  }

  @Override
  public OperationCategory getOperationCategory() {
    return OperationCategory.URL;
  }

  @Override
  protected String convertFrom(String data) {
    return URLDecoder.decode(data, StandardCharsets.UTF_8);
  }

  @Override
  protected String convertTo(String data) {
    return URLEncoder.encode(data, StandardCharsets.UTF_8);
  }

  @Override
  protected String getFromLabel() {
    return "Plain";
  }

  @Override
  protected String getToLabel() {
    return "Encoded";
  }

  @Override
  protected Language getFromLanguage() {
    return PlainTextLanguage.INSTANCE;
  }

  @Override
  protected Language getToLanguage() {
    return PlainTextLanguage.INSTANCE;
  }
}
