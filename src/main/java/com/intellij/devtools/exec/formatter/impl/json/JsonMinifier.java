package com.intellij.devtools.exec.formatter.impl.json;

import static com.intellij.devtools.MessageKeys.FORMATTER_JSON_MINIFY_NAME;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.exec.formatter.Formatter;
import com.intellij.devtools.locale.MessageBundle;
import com.intellij.devtools.utils.JsonUtils;
import javax.swing.Icon;

public class JsonMinifier extends Formatter {

  public JsonMinifier() {
    super();
  }

  @Override
  public String getNodeName() {
    return MessageBundle.get(FORMATTER_JSON_MINIFY_NAME);
  }

  @Override
  public OperationCategory getOperationCategory() {
    return OperationCategory.JSON;
  }

  @Override
  public OperationGroup getOperationGroup() {
    return OperationGroup.FORMATTER;
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  protected String format(String rawData) {
    return JsonUtils.minify(rawData);
  }
}
