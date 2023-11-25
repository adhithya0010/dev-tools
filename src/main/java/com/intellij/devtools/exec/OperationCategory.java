package com.intellij.devtools.exec;

import javax.swing.Icon;

public enum OperationCategory implements BaseNode {
  JSON("Json"),
  XML("Xml"),
  GRAPHQL("Graphql"),
  BASE_64("Base64"),
  URL("Url"),
  WEB("Web"),
  TIME("Time"),
  TEXT("Text");

  private final String categoryName;
  private final Icon icon;

  OperationCategory(String categoryName) {
    this.categoryName = categoryName;
    this.icon = null;
  }

  @Override
  public String getNodeName() {
    return categoryName;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }
}
