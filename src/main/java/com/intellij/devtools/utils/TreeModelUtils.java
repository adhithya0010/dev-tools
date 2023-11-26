package com.intellij.devtools.utils;

import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeModelUtils {

  private TreeModelUtils() {}

  public static DefaultMutableTreeNode toTreeNode(List<Operation> operations) {
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(null);
    Map<OperationGroup, Map<OperationCategory, List<Operation>>> operationsMap =
        new LinkedHashMap<>();

    operations.forEach(
        operation -> {
          OperationGroup operationGroup = operation.getOperationGroup();
          Map<OperationCategory, List<Operation>> categoryMap =
              operationsMap.getOrDefault(operationGroup, new LinkedHashMap<>());
          List<Operation> operationsByCategory =
              categoryMap.getOrDefault(operation.getOperationCategory(), new ArrayList<>());
          operationsByCategory.add(operation);
          categoryMap.put(operation.getOperationCategory(), operationsByCategory);
          operationsMap.put(operationGroup, categoryMap);
        });

    operationsMap.forEach(
        (groupType, categoryMap) -> {
          DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(groupType);
          categoryMap.forEach(
              (categoryType, operationsByCategory) -> {
                DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(categoryType);
                operationsByCategory.forEach(
                    operation -> {
                      categoryNode.add(new DefaultMutableTreeNode(operation));
                    });
                groupNode.add(categoryNode);
              });
          rootNode.add(groupNode);
        });
    return rootNode;
  }

  public static ComboBoxModel<DefaultMutableTreeNode> toComboBoxModel(List<Operation> operations) {
    DefaultMutableTreeNode rootNode = toTreeNode(operations);
    DefaultComboBoxModel<DefaultMutableTreeNode> comboBoxModel = new DefaultComboBoxModel<>();
    Collections.list(rootNode.preorderEnumeration())
        .forEach(
            node -> {
              if (((DefaultMutableTreeNode) node).getUserObject() == null
                  || ((DefaultMutableTreeNode) node).getUserObject() instanceof OperationGroup) {
                return;
              }
              comboBoxModel.addElement((DefaultMutableTreeNode) node);
            });
    return comboBoxModel;
  }
}
