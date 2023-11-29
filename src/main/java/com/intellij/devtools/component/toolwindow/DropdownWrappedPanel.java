package com.intellij.devtools.component.toolwindow;

import static com.intellij.devtools.utils.GridConstraintUtils.CAN_SHRINK_AND_GROW;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridConstraint;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED;

import com.intellij.devtools.component.button.IconButton;
import com.intellij.devtools.component.combobox.TreeCellRenderer;
import com.intellij.devtools.component.combobox.TreeComboBox;
import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.Orientation;
import com.intellij.devtools.utils.ComponentUtils;
import com.intellij.devtools.utils.GridConstraintUtils;
import com.intellij.devtools.utils.TreeModelUtils;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.IconLoader;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

public class DropdownWrappedPanel extends JPanel {

  public static final Icon SPLIT_RIGHT =
      IconLoader.findIcon(
          "devtools/splitBottom/splitBottom.svg", DropdownWrappedPanel.class.getClassLoader());
  public static final Icon SPLIT_BOTTOM =
      IconLoader.findIcon(
          "devtools/splitRight/splitRight.svg", DropdownWrappedPanel.class.getClassLoader());

  private final List<Operation> operations;
  private final JPanel headerPanel = new JPanel();
  private final JPanel contentPanel = new JPanel();
  private final ComboBox<DefaultMutableTreeNode> operationGroupComboBox = new TreeComboBox();
  private final IconButton orientationButton = new IconButton(SPLIT_RIGHT);
  private Orientation currentOrientation = Orientation.HORIZONTAL;

  public DropdownWrappedPanel(List<Operation> operations) {
    this.operations = operations;
    configureComponents();
    configureLayouts();
    initializeListeners();
  }

  private void configureComponents() {
    setBorder(JBUI.Borders.emptyTop(10));
    operationGroupComboBox.setModel(TreeModelUtils.toComboBoxModel(operations));
    operationGroupComboBox.setRenderer(new TreeCellRenderer<>());
    operationGroupComboBox.setSelectedItem(null);
    operationGroupComboBox.setMaximumRowCount(25);
    orientationButton.setToolTipText("Click to split right");
    orientationButton.setEnabled(false);
  }

  private void configureLayouts() {
    headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
    headerPanel.add(operationGroupComboBox);
    headerPanel.add(orientationButton);

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
            if (previousNode.isLeaf()
                && previousNode.getUserObject() instanceof Operation operation) {
              operation.persistState();
            }
          }
          DefaultMutableTreeNode treeNode =
              (DefaultMutableTreeNode) operationGroupComboBox.getSelectedItem();
          if (Objects.nonNull(treeNode)
              && treeNode.isLeaf()
              && treeNode.getUserObject() instanceof Operation operation) {
            operation.reset();
            operation.restoreState();

            ComponentUtils.removeAllChildren(contentPanel);
            contentPanel.add(operation, buildGridConstraint(0, 0, FILL_BOTH));
            ComponentUtils.refreshComponent(this);
            previousSelection.set(treeNode);

            if (operation.isOrientationSupported()) {
              orientationButton.setEnabled(true);
              currentOrientation = operation.getOrientation();
              if (Orientation.HORIZONTAL.equals(operation.getOrientation())) {
                orientationButton.setIcon(SPLIT_RIGHT);
                orientationButton.setToolTipText("Click to split right");
              } else {
                orientationButton.setIcon(SPLIT_BOTTOM);
                orientationButton.setToolTipText("Click to split bottom");
              }
            }
          } else {
            orientationButton.setEnabled(false);
            orientationButton.setToolTipText(null);
            orientationButton.setIcon(SPLIT_RIGHT);
          }
        });

    orientationButton.addActionListener(
        evt -> {
          DefaultMutableTreeNode selectedItem =
              (DefaultMutableTreeNode) operationGroupComboBox.getSelectedItem();
          if (Objects.nonNull(selectedItem)
              && selectedItem.isLeaf()
              && selectedItem.getUserObject() instanceof Operation operation) {

            System.out.printf("Current orientation: %s", currentOrientation);

            if (Orientation.HORIZONTAL.equals(currentOrientation)) {
              operation.setOrientation(currentOrientation = Orientation.VERTICAL);
              orientationButton.setIcon(SPLIT_BOTTOM);
              orientationButton.setToolTipText("Click to split bottom");
            } else {
              operation.setOrientation(currentOrientation = Orientation.HORIZONTAL);
              orientationButton.setIcon(SPLIT_RIGHT);
              orientationButton.setToolTipText("Click to split right");
            }
          }
        });
  }
}
