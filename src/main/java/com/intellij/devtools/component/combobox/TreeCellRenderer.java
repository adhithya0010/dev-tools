package com.intellij.devtools.component.combobox;

import com.intellij.devtools.exec.BaseNode;
import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.utils.ComponentUtils;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.render.RenderingUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import java.awt.Color;
import java.awt.Component;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jetbrains.annotations.NotNull;

public class TreeCellRenderer<T> extends JPanel implements ListCellRenderer<T> {

  protected final JBLabel label = new JBLabel("Select");

  public TreeCellRenderer() {
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    label.setBorder(JBUI.Borders.empty(5));
  }

  @Override
  public Component getListCellRendererComponent(
      JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
    ComponentUtils.removeAllChildren(this);
    setComponentOrientation(list.getComponentOrientation());
    setBorder(JBUI.Borders.empty(UIUtil.getListCellVPadding(), UIUtil.getListCellHPadding()));

    Color bg, fg;
    JList.DropLocation dropLocation = list.getDropLocation();

    if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == index) {
      bg = UIManager.getColor("List.dropCellBackground");
      fg = UIManager.getColor("List.dropCellForeground");
    } else {
      bg = RenderingUtil.getBackground(list, isSelected);
      fg = RenderingUtil.getForeground(list, isSelected);
    }

    if (Objects.isNull(value)
        || !(value instanceof DefaultMutableTreeNode)
        || !(((DefaultMutableTreeNode) value).getUserObject() instanceof Operation)) {
      bg = UIManager.getColor("ComboBox.background");
      setOpaque(true);
    } else {
      setOpaque(isSelected);
    }

    setBackground(bg);
    setForeground(fg);
    customize(list, value, index, isSelected, cellHasFocus);

    return this;
  }

  public void customize(
      @NotNull JList<? extends T> list, T value, int index, boolean selected, boolean hasFocus) {
    if (Objects.isNull(value)) {
      setLabelText("Select");
      setLabelIcon(null);
      add(label);
      return;
    }

    if (value instanceof DefaultMutableTreeNode node) {
      if (index < 0) {
        if (node.isLeaf() && node.getUserObject() instanceof Operation operation) {
          setLabelText(buildPathText(operation));
        } else {
          Object userObject = node.getUserObject();
          addTreeSpacer(node);
          configureLabel(userObject);
        }
      } else {
        Object userObject = node.getUserObject();
        addTreeSpacer(node);
        configureLabel(userObject);
      }
      if (node.getLevel() == 1 || node.getLevel() == 2) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(label);
        add(container);
      } else {
        add(label);
      }
    }
  }

  private void configureLabel(Object userObject) {
    setLabelText(getName(userObject));
    setLabelIcon(getIcon(userObject));
  }

  private void addTreeSpacer(DefaultMutableTreeNode node) {
    if (node.isLeaf() && ((Operation) node.getUserObject()).getOperationCategory() != null) {
      add(Box.createHorizontalStrut(20 * (node.getLevel() - 1)));
    }
  }

  private String buildPathText(Operation operation) {
    StringJoiner joiner = new StringJoiner(" / ");
    Optional.ofNullable(operation.getOperationCategory())
        .map(OperationCategory::getNodeName)
        .ifPresent(joiner::add);
    Optional.ofNullable(operation.getNodeName()).ifPresent(joiner::add);
    return joiner.toString();
  }

  public void setLabelText(String text) {
    label.setText(text);
  }

  public void setLabelIcon(Icon icon) {
    label.setIcon(icon);
  }

  private Icon getIcon(Object userObject) {
    if (userObject instanceof BaseNode) {
      return ((BaseNode) userObject).getIcon();
    }
    return null;
  }

  private String getName(Object userObject) {
    if (userObject instanceof BaseNode) {
      return ((BaseNode) userObject).getNodeName();
    }
    return null;
  }
}
