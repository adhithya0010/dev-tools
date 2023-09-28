package com.intellij.devtools.exec.formatter.impl.xml;

import static com.intellij.devtools.MessageKeys.FORMATTER_XML_PRETTIFIER_NAME;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.exec.formatter.Formatter;
import com.intellij.devtools.locale.MessageBundle;
import com.intellij.devtools.utils.XmlUtils;
import javax.swing.Icon;

public class XmlPrettifier extends Formatter {

  public XmlPrettifier() {
    super();
  }

  @Override
  public String getNodeName() {
    return MessageBundle.get(FORMATTER_XML_PRETTIFIER_NAME);
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public OperationCategory getOperationCategory() {
    return OperationCategory.XML;
  }

  @Override
  public OperationGroup getOperationGroup() {
    return OperationGroup.FORMATTER;
  }

  @Override
  protected String format(String rawData) {
    return XmlUtils.prettify(rawData);
  }
}
