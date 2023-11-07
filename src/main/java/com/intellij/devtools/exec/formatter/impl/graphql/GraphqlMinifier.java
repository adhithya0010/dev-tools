package com.intellij.devtools.exec.formatter.impl.graphql;

import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.formatter.Formatter;
import com.intellij.devtools.utils.GraphqlUtils;
import com.intellij.lang.Language;
import com.intellij.lang.jsgraphql.GraphQLLanguage;

public class GraphqlMinifier extends Formatter {

  @Override
  public String getNodeName() {
    return "Minify";
  }

  @Override
  public OperationCategory getOperationCategory() {
    return OperationCategory.GRAPHQL;
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
