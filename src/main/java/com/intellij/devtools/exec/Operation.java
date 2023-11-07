package com.intellij.devtools.exec;

import com.intellij.ui.components.JBPanel;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

public abstract class Operation extends JBPanel<Operation> implements BaseNode {

  protected final JPanel parametersPanel = new JPanel(new GridBagLayout());

  protected void configureComponents() {}

  protected void configureLayouts() {}

  protected void configureListeners() {}

  protected void configureParameters(JPanel parametersPanel) {}

  public abstract OperationCategory getOperationCategory();

  public abstract OperationGroup getOperationGroup();

  public void reset() {}

  public void persistState() {}

  public void restoreState() {}
}
