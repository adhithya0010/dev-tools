package com.intellij.devtools.exec.misc.time;

import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;

public class TimezoneConverter extends Operation {
  @Override
  public String getNodeName() {
    return "Time Parse";
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
