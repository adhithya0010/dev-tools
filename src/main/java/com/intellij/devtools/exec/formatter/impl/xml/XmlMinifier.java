package com.intellij.devtools.exec.formatter.impl.xml;

import static com.intellij.devtools.MessageKeys.FORMATTER_XML_MINIFY_NAME;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.formatter.Formatter;
import com.intellij.devtools.locale.MessageBundle;
import com.intellij.devtools.utils.XmlUtils;
import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;

public class XmlMinifier extends Formatter {

  public XmlMinifier() {
    super();
  }

  @Override
  public String getNodeName() {
    return MessageBundle.get(FORMATTER_XML_MINIFY_NAME);
  }

  @Override
  public OperationCategory getOperationCategory() {
    return OperationCategory.XML;
  }

  @Override
  protected String format(String rawData) {
    return XmlUtils.minify(rawData);
  }

  @Override
  protected Language getLanguage() {
    return XMLLanguage.INSTANCE;
  }
}
