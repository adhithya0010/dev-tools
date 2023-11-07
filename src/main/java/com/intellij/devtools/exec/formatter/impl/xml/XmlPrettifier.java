package com.intellij.devtools.exec.formatter.impl.xml;

import static com.intellij.devtools.MessageKeys.FORMATTER_XML_PRETTIFIER_NAME;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.PrettifyConfig;
import com.intellij.devtools.exec.formatter.PrettyFormatter;
import com.intellij.devtools.locale.MessageBundle;
import com.intellij.devtools.utils.XmlUtils;
import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;

public class XmlPrettifier extends PrettyFormatter {

  public XmlPrettifier() {
    super();
  }

  @Override
  public String getNodeName() {
    return MessageBundle.get(FORMATTER_XML_PRETTIFIER_NAME);
  }

  @Override
  public OperationCategory getOperationCategory() {
    return OperationCategory.XML;
  }

  @Override
  protected String format(String rawData, PrettifyConfig prettifyConfig) {
    return XmlUtils.prettify(rawData, prettifyConfig);
  }

  @Override
  protected Language getLanguage() {
    return XMLLanguage.INSTANCE;
  }
}
