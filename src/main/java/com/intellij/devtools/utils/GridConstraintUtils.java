package com.intellij.devtools.utils;

import com.intellij.uiDesigner.core.GridConstraints;
import java.awt.GridBagConstraints;

public class GridConstraintUtils {

  private GridConstraintUtils() {}

  public static final int CAN_SHRINK_AND_GROW =
      GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK;

  public static GridConstraints buildGridConstraint(int row, int column, int fill) {
    GridConstraints gc = new GridConstraints();
    gc.setFill(fill);
    gc.setRow(row);
    gc.setColumn(column);
    return gc;
  }

  public static GridConstraints buildGridConstraint(
      int row, int column, int rowSpan, int colSpan, int fill, int sizePolicy) {
    return buildGridConstraint(row, column, rowSpan, colSpan, fill, sizePolicy, sizePolicy);
  }

  public static GridConstraints buildGridConstraint(
      int row, int column, int rowSpan, int colSpan, int fill, int vSizePolicy, int hSizePolicy) {
    GridConstraints gc = new GridConstraints();
    gc.setRow(row);
    gc.setColumn(column);
    gc.setRowSpan(rowSpan);
    gc.setColSpan(colSpan);
    gc.setFill(fill);
    gc.setVSizePolicy(vSizePolicy);
    gc.setHSizePolicy(hSizePolicy);
    return gc;
  }

  public static GridBagConstraints buildGridBagConstraint(int gridx, int gridy, int fill) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = gridx;
    gbc.gridy = gridy;
    gbc.fill = fill;
    return gbc;
  }

  public static GridBagConstraints buildGridBagConstraint(
      int gridx, int gridy, double weightx, double weighty, int fill) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = gridx;
    gbc.gridy = gridy;
    gbc.weightx = weightx;
    gbc.weighty = weighty;
    gbc.fill = fill;
    return gbc;
  }
}
