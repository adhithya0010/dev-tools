package com.intellij.devtools.exec;

import com.intellij.ui.components.JBPanel;

public abstract class Operation extends JBPanel<Operation> implements BaseNode, Parameterized {

  public abstract OperationCategory getOperationCategory();

  public abstract OperationGroup getOperationGroup();

  public abstract void reset();

  public void persistState() {

  }

  public void restoreState() {

  }
}
