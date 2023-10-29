package com.intellij.devtools.exec;

import com.intellij.ui.IconManager;
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

  OperationCategory(String categoryName, String iconPath) {
    this.categoryName = categoryName;
    this.icon = IconManager.getInstance().getIcon(iconPath, getClass().getClassLoader());
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
