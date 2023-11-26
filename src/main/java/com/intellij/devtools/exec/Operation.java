package com.intellij.devtools.exec;

import com.intellij.ui.components.JBPanel;
import java.awt.GridBagLayout;
import javax.swing.Icon;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.Setter;

public abstract class Operation extends JBPanel<Operation> implements BaseNode {

  protected final JPanel parametersPanel = new JPanel(new GridBagLayout());

  @Getter @Setter private Orientation orientation = Orientation.HORIZONTAL;

  protected void configureComponents() {}

  protected void configureLayouts() {}

  protected void configureListeners() {}

  protected void configureParameters(JPanel parametersPanel) {}

  public OperationCategory getOperationCategory() {
    return null;
  }

  public OperationGroup getOperationGroup() {
    return null;
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  public void reset() {}

  public void persistState() {}

  public void restoreState() {}

  public boolean isOrientationSupported() {
    return false;
  }
}
