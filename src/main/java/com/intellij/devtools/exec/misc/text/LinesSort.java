package com.intellij.devtools.exec.misc.text;

import com.intellij.devtools.exec.formatter.Formatter;
import com.intellij.devtools.utils.TextUtils;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.PlainTextLanguage;

public class LinesSort extends Formatter {

  @Override
  public String getNodeName() {
    return "Sort Lines";
  }

  @Override
  protected String format(String rawData) {
    return TextUtils.sortLines(rawData);
  }

  @Override
  protected Language getLanguage() {
    return PlainTextLanguage.INSTANCE;
  }
}
