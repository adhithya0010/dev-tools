package com.intellij.devtools.component.combobox;

import com.intellij.devtools.exec.Operation;
import com.intellij.openapi.ui.ComboBox;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeComboBox extends ComboBox<DefaultMutableTreeNode> {

  private boolean showPopup = false;

  public TreeComboBox() {
    super();
  }

  @Override
  public void setSelectedIndex(int anIndex) {
    super.setSelectedIndex(anIndex);
  }

  @Override
  public void setSelectedItem(Object anObject) {
    if (anObject instanceof DefaultMutableTreeNode) {
      Object userObject = ((DefaultMutableTreeNode) anObject).getUserObject();
      if (!(userObject instanceof Operation)) {
        this.showPopup = true;
        return;
      }
    }

    super.setSelectedItem(anObject);
    this.showPopup = false;
  }

  public void setPopupVisible(boolean v) {
    super.setPopupVisible(this.showPopup);
    this.showPopup = false;
  }
}
