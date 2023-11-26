//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.intellij.devtools.exec.misc.web;

import com.intellij.devtools.component.dialog.CreateMockDialog;
import com.intellij.devtools.component.table.InvocationsModel;
import com.intellij.devtools.component.table.RunningServersModel;
import com.intellij.devtools.exec.HttpRequestConfig;
import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.utils.GridConstraintUtils;
import com.intellij.devtools.utils.ServerUtils;
import com.intellij.icons.AllIcons.Actions;
import com.intellij.icons.AllIcons.Diff;
import com.intellij.ui.table.JBTable;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import java.util.List;
import java.util.Optional;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MockServer extends Operation {
  private JPanel runningServersPanel;
  private JPanel runningServersHeaderPanel;
  private JPanel runningServersContentPanel;
  private JPanel historyPanel;
  private JPanel historyHeadersPanel;
  private JPanel historyContentPanel;
  private JLabel runningServersLabel;
  private JLabel invocationsLabel;
  private JButton startServerButton;
  private JButton stopServerButton;
  private JButton clearHistoryButton;
  private JTable runningServersTable;
  private JTable historyTable;
  private RunningServersModel runningServersTableModel;
  private InvocationsModel invocationsTableModel;

  public MockServer() {
    this.configureComponents();
    this.configureLayout();
    this.configureListeners();
  }

  @Override
  protected void configureComponents() {
    setBorder(JBUI.Borders.emptyTop(10));
    this.runningServersPanel = new JPanel();
    this.runningServersHeaderPanel = new JPanel();
    this.runningServersContentPanel = new JPanel();
    this.historyPanel = new JPanel();
    this.historyHeadersPanel = new JPanel();
    this.historyContentPanel = new JPanel();
    this.runningServersLabel = new JLabel("Live Mocks");
    this.invocationsLabel = new JLabel("Invocations");
    this.startServerButton = new JButton("Create", Actions.Execute);
    this.stopServerButton = new JButton("Destroy", Actions.Suspend);
    this.clearHistoryButton = new JButton("Clear", Diff.Remove);
    this.runningServersTableModel = new RunningServersModel();
    this.runningServersTable = new JBTable(this.runningServersTableModel);
    this.runningServersTable.setRowSelectionAllowed(false);
    this.runningServersTable.setCellSelectionEnabled(false);
    this.invocationsTableModel = new InvocationsModel();
    this.historyTable = new JBTable(this.invocationsTableModel);
    this.historyTable.setRowSelectionAllowed(false);
    this.historyTable.setCellSelectionEnabled(false);
  }

  private void configureLayout() {
    this.runningServersHeaderPanel.setLayout(new GridLayoutManager(1, 3));
    this.runningServersHeaderPanel.add(
        this.runningServersLabel, GridConstraintUtils.buildGridConstraint(0, 0, 1, 1, 1, 3, 3));
    this.runningServersHeaderPanel.add(
        this.startServerButton, GridConstraintUtils.buildGridConstraint(0, 1, 1, 1, 0, 0, 0));
    this.runningServersHeaderPanel.add(
        this.stopServerButton, GridConstraintUtils.buildGridConstraint(0, 2, 1, 1, 0, 0, 0));
    this.runningServersContentPanel.setLayout(new GridLayoutManager(1, 1));
    this.runningServersContentPanel.add(
        new JScrollPane(this.runningServersTable),
        GridConstraintUtils.buildGridConstraint(0, 0, 1, 1, 3, 3));
    this.runningServersPanel.setLayout(new GridLayoutManager(2, 1));
    this.runningServersPanel.add(
        this.runningServersHeaderPanel,
        GridConstraintUtils.buildGridConstraint(0, 0, 1, 1, 1, 0, 0));
    this.runningServersPanel.add(
        this.runningServersContentPanel,
        GridConstraintUtils.buildGridConstraint(1, 0, 1, 1, 3, 3, 3));
    this.historyHeadersPanel.setLayout(new GridLayoutManager(1, 2));
    this.historyHeadersPanel.add(
        this.invocationsLabel, GridConstraintUtils.buildGridConstraint(0, 0, 1, 1, 1, 3, 3));
    this.historyHeadersPanel.add(
        this.clearHistoryButton, GridConstraintUtils.buildGridConstraint(0, 1, 1, 1, 0, 0, 0));
    this.historyContentPanel.setLayout(new GridLayoutManager(1, 1));
    this.historyContentPanel.add(
        new JScrollPane(this.historyTable),
        GridConstraintUtils.buildGridConstraint(0, 0, 1, 1, 3, 3));
    this.historyPanel.setLayout(new GridLayoutManager(2, 1));
    this.historyPanel.add(
        this.historyHeadersPanel, GridConstraintUtils.buildGridConstraint(0, 0, 1, 1, 1, 0, 0));
    this.historyPanel.add(
        this.historyContentPanel, GridConstraintUtils.buildGridConstraint(1, 0, 1, 1, 3, 3, 3));
    this.setLayout(new GridLayoutManager(2, 1));
    this.add(
        this.runningServersPanel, GridConstraintUtils.buildGridConstraint(0, 0, 1, 1, 3, 3, 3));
    this.add(this.historyPanel, GridConstraintUtils.buildGridConstraint(1, 0, 1, 1, 3, 3, 3));
  }

  @Override
  protected void configureListeners() {
    var that = this;
    this.startServerButton.addActionListener(
        (evt) -> {
          CreateMockDialog createMockDialog = new CreateMockDialog(that);
          Optional<HttpRequestConfig> httpRequestConfig = createMockDialog.showAndGetData();
          if (httpRequestConfig.isPresent()) {
            ServerUtils.startServer(this.invocationsTableModel, httpRequestConfig.get());
            this.runningServersTableModel.addRow(httpRequestConfig.get());
          }
        });
    this.stopServerButton.addActionListener(
        (evt) -> {
          List<HttpRequestConfig> selectedRequestConfigs =
              this.runningServersTableModel.getSelectedRequestConfigs();
          selectedRequestConfigs.forEach(
              (httpRequestConfig) -> {
                ServerUtils.stopServer(httpRequestConfig.getId());
                this.runningServersTableModel.removeRow(httpRequestConfig);
              });
          runningServersTableModel.removeSelectedRows();
        });

    clearHistoryButton.addActionListener((evt) -> invocationsTableModel.clear());
  }

  @Override
  public String getNodeName() {
    return "Mock Server";
  }

  @Override
  public OperationCategory getOperationCategory() {
    return OperationCategory.WEB;
  }

  @Override
  public OperationGroup getOperationGroup() {
    return OperationGroup.MISC;
  }

  @Override
  public void reset() {
    // TODO
  }
}
