package com.intellij.devtools.exec;

import javax.swing.JPanel;

public abstract class Operation extends JPanel implements BaseNode, Parameterized {

  public abstract OperationCategory getOperationCategory();

  public abstract OperationGroup getOperationGroup();

  public abstract void reset();

  public void persistState() {

  }

  public void restoreState() {

  }
}
