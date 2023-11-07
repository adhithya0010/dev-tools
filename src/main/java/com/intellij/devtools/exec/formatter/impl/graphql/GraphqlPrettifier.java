package com.intellij.devtools.exec.formatter.impl.graphql;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.PrettifyConfig;
import com.intellij.devtools.exec.formatter.PrettyFormatter;
import com.intellij.devtools.utils.GraphqlUtils;
import com.intellij.lang.Language;
import com.intellij.lang.jsgraphql.GraphQLLanguage;

public class GraphqlPrettifier extends PrettyFormatter {

  public GraphqlPrettifier() {
    super();
  }

  @Override
  protected boolean isIndentLengthEnabled() {
    return false;
  }

  @Override
  public String getNodeName() {
    return "Prettify";
  }

  @Override
  public OperationCategory getOperationCategory() {
    return OperationCategory.GRAPHQL;
  }

  @Override
  protected String format(String rawData, PrettifyConfig prettifyConfig) {
    return GraphqlUtils.prettify(rawData, prettifyConfig);
  }

  @Override
  protected Language getLanguage() {
    return GraphQLLanguage.INSTANCE;
  }
}
