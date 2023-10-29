package com.intellij.devtools.exec.misc.time;

import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import javax.swing.Icon;

public class TimeParser extends Operation {
  @Override
  public String getNodeName() {
    return "Time Parse";
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public OperationCategory getOperationCategory() {
    return OperationCategory.TIME;
  }

  @Override
  public OperationGroup getOperationGroup() {
    return OperationGroup.MISC;
  }
}
