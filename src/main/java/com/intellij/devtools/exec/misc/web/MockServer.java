//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.intellij.devtools.exec.misc.web;

import com.intellij.devtools.component.editortextfield.customization.LinesCustomization;
import com.intellij.devtools.component.editortextfield.customization.WrapTextCustomization;
import com.intellij.devtools.component.table.InvocationsModel;
import com.intellij.devtools.component.table.MockMetadata;
import com.intellij.devtools.component.table.RunningServersModel;
import com.intellij.devtools.exec.HttpMethod;
import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.utils.GridConstraintUtils;
import com.intellij.devtools.utils.ProjectUtils;
import com.intellij.devtools.utils.ServerUtils;
import com.intellij.icons.AllIcons.Actions;
import com.intellij.icons.AllIcons.Diff;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.EditorTextFieldProvider;
import com.intellij.ui.table.JBTable;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Component;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.jdesktop.swingx.prompt.PromptSupport;

public class MockServer extends Operation {
  private JPanel parameterPanel;
  private JPanel runningServersPanel;
  private JPanel runningServersHeaderPanel;
  private JPanel runningServersContentPanel;
  private JPanel historyPanel;
  private JPanel historyHeadersPanel;
  private JPanel historyContentPanel;
  private JLabel parametersLabel;
  private JLabel pathLabel;
  private JLabel portLabel;
  private JLabel responseCodeLabel;
  private JLabel methodLabel;
  private JLabel responseHeadersLabel;
  private JLabel responseBodyLabel;
  private JLabel runningServersLabel;
  private JLabel invocationsLabel;
  private JTextField pathTextField;
  private JTextField portTextField;
  private JTextField responseCodeTextField;
  private JComboBox<HttpMethod> methodComboBox;
  private EditorTextField responseHeadersTextField;
  private EditorTextField responseBodyTextField;
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

  private void configureComponents() {
    this.parameterPanel = new JPanel();
    this.runningServersPanel = new JPanel();
    this.runningServersHeaderPanel = new JPanel();
    this.runningServersContentPanel = new JPanel();
    this.historyPanel = new JPanel();
    this.historyHeadersPanel = new JPanel();
    this.historyContentPanel = new JPanel();
    this.parametersLabel = new JLabel("Parameters");
    this.pathLabel = new JLabel("Path");
    this.portLabel = new JLabel("Port");
    this.responseCodeLabel = new JLabel("Response code");
    this.methodLabel = new JLabel("Method");
    this.responseHeadersLabel = new JLabel("Response Headers");
    this.responseBodyLabel = new JLabel("Response Body");
    this.runningServersLabel = new JLabel("Live Mocks");
    this.invocationsLabel = new JLabel("Invocations");
    this.pathTextField = new JTextField();
    this.portTextField = new JTextField();
    this.responseCodeTextField = new JTextField();
    this.methodComboBox = new ComboBox(HttpMethod.values());
    this.methodComboBox.setSelectedItem(HttpMethod.GET);
    this.methodComboBox.setRenderer(
        new DefaultListCellRenderer() {
          public Component getListCellRendererComponent(
              JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            this.setText(((HttpMethod) value).getValue());
            return this;
          }
        });
    this.responseHeadersTextField =
        EditorTextFieldProvider.getInstance()
            .getEditorField(
                PlainTextLanguage.INSTANCE,
                ProjectUtils.getProject(),
                List.of(WrapTextCustomization.ENABLED, LinesCustomization.ENABLED));
    this.responseBodyTextField =
        EditorTextFieldProvider.getInstance()
            .getEditorField(
                PlainTextLanguage.INSTANCE,
                ProjectUtils.getProject(),
                List.of(WrapTextCustomization.ENABLED, LinesCustomization.ENABLED));
    this.startServerButton = new JButton("Mock", Actions.Execute);
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
    PromptSupport.setPrompt("Path to mock /test/abc", this.pathTextField);
    PromptSupport.setPrompt("Port for the mock server", this.portTextField);
    PromptSupport.setPrompt("Response status code value", this.responseCodeTextField);
    responseHeadersTextField.setPlaceholder(
        "Rows are separated by lines\nKeys and values are separated by :\neg:\nContent-Type:application/json");
    responseBodyTextField.setPlaceholder("Response body data");
  }

  private JPanel encloseForRight(JComponent component) {
    JPanel jPanel = new JPanel(new GridLayoutManager(1, 2));
    jPanel.add(new Spacer(), GridConstraintUtils.buildGridConstraint(0, 0, 1, 1, 1, 3, 3));
    jPanel.add(component, GridConstraintUtils.buildGridConstraint(0, 1, 1, 1, 0, 0, 0));
    return jPanel;
  }

  private JPanel encloseForLeft(JComponent component) {
    JPanel jPanel = new JPanel(new GridLayoutManager(1, 2));
    jPanel.add(component, GridConstraintUtils.buildGridConstraint(0, 0, 1, 1, 0, 0, 0));
    jPanel.add(new Spacer(), GridConstraintUtils.buildGridConstraint(0, 1, 1, 1, 1, 3, 3));
    return jPanel;
  }

  private JPanel encloseForVertical(JComponent component) {
    JPanel jPanel = new JPanel(new GridLayoutManager(2, 1));
    jPanel.add(
        this.encloseForLeft(component),
        GridConstraintUtils.buildGridConstraint(0, 0, 1, 1, 1, 0, 3));
    jPanel.add(new Spacer(), GridConstraintUtils.buildGridConstraint(1, 0, 1, 1, 3, 3, 3));
    return jPanel;
  }

  private void configureLayout() {
    this.parameterPanel.setLayout(new GridLayoutManager(7, 2));
    this.parameterPanel.add(
        this.encloseForVertical(this.parametersLabel),
        GridConstraintUtils.buildGridConstraint(0, 0, 1, 1, 1, 3, 3));
    this.parameterPanel.add(
        this.encloseForRight(this.startServerButton),
        GridConstraintUtils.buildGridConstraint(0, 1, 1, 1, 1, 3, 3));
    this.parameterPanel.add(
        this.encloseForVertical(this.pathLabel),
        GridConstraintUtils.buildGridConstraint(1, 0, 1, 1, 1, 0));
    this.parameterPanel.add(
        this.pathTextField, GridConstraintUtils.buildGridConstraint(1, 1, 1, 1, 1, 3));
    this.parameterPanel.add(
        this.encloseForVertical(this.portLabel),
        GridConstraintUtils.buildGridConstraint(2, 0, 1, 1, 1, 0));
    this.parameterPanel.add(
        this.portTextField, GridConstraintUtils.buildGridConstraint(2, 1, 1, 1, 1, 3));
    this.parameterPanel.add(
        this.encloseForVertical(this.responseCodeLabel),
        GridConstraintUtils.buildGridConstraint(3, 0, 1, 1, 1, 0));
    this.parameterPanel.add(
        this.responseCodeTextField, GridConstraintUtils.buildGridConstraint(3, 1, 1, 1, 1, 3));
    this.parameterPanel.add(
        this.encloseForVertical(this.methodLabel),
        GridConstraintUtils.buildGridConstraint(4, 0, 1, 1, 1, 3, 3));
    this.parameterPanel.add(
        this.methodComboBox, GridConstraintUtils.buildGridConstraint(4, 1, 1, 1, 1, 3, 3));
    this.parameterPanel.add(
        this.encloseForVertical(this.responseHeadersLabel),
        GridConstraintUtils.buildGridConstraint(5, 0, 1, 1, 3, 3, 3));
    this.parameterPanel.add(
        this.responseHeadersTextField,
        GridConstraintUtils.buildGridConstraint(5, 1, 1, 1, 1, 3, 3));
    this.parameterPanel.add(
        this.encloseForVertical(this.responseBodyLabel),
        GridConstraintUtils.buildGridConstraint(6, 0, 1, 1, 3, 3, 3));
    this.parameterPanel.add(
        this.responseBodyTextField, GridConstraintUtils.buildGridConstraint(6, 1, 1, 1, 1, 3, 3));
    this.runningServersHeaderPanel.setLayout(new GridLayoutManager(1, 2));
    this.runningServersHeaderPanel.add(
        this.runningServersLabel, GridConstraintUtils.buildGridConstraint(0, 0, 1, 1, 1, 3, 3));
    this.runningServersHeaderPanel.add(
        this.stopServerButton, GridConstraintUtils.buildGridConstraint(0, 1, 1, 1, 0, 0, 0));
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
    this.setLayout(new GridLayoutManager(3, 1));
    this.add(this.parameterPanel, GridConstraintUtils.buildGridConstraint(0, 0, 1, 1, 1, 0));
    this.add(
        this.runningServersPanel, GridConstraintUtils.buildGridConstraint(1, 0, 1, 1, 3, 3, 3));
    this.add(this.historyPanel, GridConstraintUtils.buildGridConstraint(2, 0, 1, 1, 3, 3, 3));
  }

  private void configureListeners() {
    this.startServerButton.addActionListener(
        (evt) -> {
          String path = this.pathTextField.getText();
          String port = this.portTextField.getText();
          String responseCode = this.responseCodeTextField.getText();
          HttpMethod httpMethod = (HttpMethod) this.methodComboBox.getSelectedItem();
          String responseHeaders = this.responseHeadersTextField.getText();
          String responseBody = this.responseBodyTextField.getText();
          MockMetadata mockMetadata =
              ServerUtils.startServer(
                  this.invocationsTableModel,
                  path,
                  port,
                  httpMethod,
                  responseCode,
                  responseHeaders,
                  responseBody);
          this.runningServersTableModel.addRow(mockMetadata);
        });
    this.stopServerButton.addActionListener(
        (evt) -> {
          List<MockMetadata> selectedMockMetadata =
              this.runningServersTableModel.getSelectedServerMetas();
          selectedMockMetadata.forEach(
              (mockMetadata) -> {
                ServerUtils.stopServer(mockMetadata.id);
                this.runningServersTableModel.removeRow(mockMetadata);
              });
          runningServersTableModel.removeSelectedRows();
        });

    clearHistoryButton.addActionListener(
        (evt) -> {
          invocationsTableModel.clear();
        });
  }

  @Override
  public String getNodeName() {
    return "Mock Server";
  }

  @Override
  public Icon getIcon() {
    return null;
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
