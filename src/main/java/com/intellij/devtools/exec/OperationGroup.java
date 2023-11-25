package com.intellij.devtools.exec;

import javax.swing.Icon;

public enum OperationGroup implements BaseNode {
  FORMATTER("Formatter"),
  CONVERTER("Converter"),
  ENCODER("Encoder"),
  GENERATOR("Generator"),
  MISC("Misc");

  private final String groupName;
  private final Icon icon;

  OperationGroup(String groupName) {
    this.groupName = groupName;
    this.icon = null;
  }

  @Override
  public String getNodeName() {
    return groupName;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }
}
