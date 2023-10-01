package com.intellij.devtools.exec.formatter.impl.graphql;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.exec.formatter.Formatter;
import com.intellij.devtools.utils.GraphqlUtils;
import com.intellij.lang.Language;
import com.intellij.lang.jsgraphql.GraphQLLanguage;
import javax.swing.Icon;

public class GraphqlMinifier extends Formatter {

  @Override
  public String getNodeName() {
    return "Minify";
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public OperationCategory getOperationCategory() {
    return OperationCategory.GRAPHQL;
  }

  @Override
  public OperationGroup getOperationGroup() {
    return OperationGroup.FORMATTER;
  }

  @Override
  protected String format(String rawData) {
    return GraphqlUtils.minify(rawData);
  }

  @Override
  protected Language getLanguage() {
    return GraphQLLanguage.INSTANCE;
  }
}
