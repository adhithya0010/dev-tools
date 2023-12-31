package com.intellij.devtools.exec.formatter.impl.json;

import static com.intellij.devtools.MessageKeys.FORMATTER_JSON_PRETTIFIER_NAME;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.PrettifyConfig;
import com.intellij.devtools.exec.formatter.PrettyFormatter;
import com.intellij.devtools.locale.MessageBundle;
import com.intellij.devtools.utils.JsonUtils;
import com.intellij.json.json5.Json5Language;
import com.intellij.lang.Language;

public class JsonPrettifier extends PrettyFormatter {

  @Override
  public String getNodeName() {
    return MessageBundle.get(FORMATTER_JSON_PRETTIFIER_NAME);
  }

  @Override
  public OperationCategory getOperationCategory() {
    return OperationCategory.JSON;
  }

  @Override
  protected String format(String rawData, PrettifyConfig prettifyConfig) {
    return JsonUtils.prettify(rawData, prettifyConfig);
  }

  @Override
  protected Language getLanguage() {
    return Json5Language.INSTANCE;
  }
}
