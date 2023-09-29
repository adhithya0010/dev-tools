package com.intellij.devtools.component.toolwindow;

import static com.intellij.devtools.utils.GridConstraintUtils.CAN_SHRINK_AND_GROW;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridConstraint;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED;

import com.intellij.devtools.component.combobox.TreeCellRenderer;
import com.intellij.devtools.component.combobox.TreeComboBox;
import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.OperationFactory;
import com.intellij.devtools.utils.ComponentUtils;
import com.intellij.devtools.utils.GridConstraintUtils;
import com.intellij.devtools.utils.TreeModelUtils;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

public class DevToolsPanel extends JPanel {

  // panels
  private final JPanel headerPanel = new JPanel();
  private final JPanel contentPanel = new JPanel();
  // components
  private final ComboBox<DefaultMutableTreeNode> operationGroupComboBox = new TreeComboBox();

  public DevToolsPanel() {
    configureComponents();
    configureLayouts();
    initializeListeners();
  }

  private void configureComponents() {
    operationGroupComboBox.setModel(
        TreeModelUtils.toComboBoxModel(OperationFactory.getInstance().getAllOperations()));
    operationGroupComboBox.setRenderer(new TreeCellRenderer<>());
    operationGroupComboBox.setSelectedItem(null);
    operationGroupComboBox.setMaximumRowCount(25);
  }

  private void configureLayouts() {
    headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
    headerPanel.add(operationGroupComboBox);

    contentPanel.setLayout(new GridLayoutManager(1, 1));

    this.setLayout(new GridLayoutManager(2, 1));
    this.add(
        headerPanel,
        GridConstraintUtils.buildGridConstraint(
            0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW));
    this.add(
        contentPanel,
        GridConstraintUtils.buildGridConstraint(1, 0, 1, 1, FILL_BOTH, CAN_SHRINK_AND_GROW));

    this.setPreferredSize(new Dimension(825, 1000));
  }

  private void initializeListeners() {
    AtomicReference<DefaultMutableTreeNode> previousSelection = new AtomicReference<>();
    operationGroupComboBox.addActionListener(
        (ActionEvent evt) -> {
          if (Objects.nonNull(previousSelection.get())) {
            DefaultMutableTreeNode previousNode = previousSelection.get();
            if (previousNode.isLeaf() && previousNode.getUserObject() instanceof Operation) {
              Operation operation = (Operation) previousNode.getUserObject();
              operation.persistState();
            }
          }
          DefaultMutableTreeNode treeNode =
              (DefaultMutableTreeNode) operationGroupComboBox.getSelectedItem();
          if (Objects.nonNull(treeNode)
              && treeNode.isLeaf()
              && treeNode.getUserObject() instanceof Operation) {
            Operation operation = (Operation) treeNode.getUserObject();
            operation.reset();
            operation.restoreState();

            ComponentUtils.removeAllChildren(contentPanel);
            contentPanel.add(operation, buildGridConstraint(0, 0, FILL_BOTH));
            ComponentUtils.refreshComponent(this);
            previousSelection.set(treeNode);
          }
        });
  }
}
