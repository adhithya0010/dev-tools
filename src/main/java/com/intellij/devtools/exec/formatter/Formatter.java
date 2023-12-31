package com.intellij.devtools.exec.formatter;

import static com.intellij.devtools.utils.GridConstraintUtils.CAN_SHRINK_AND_GROW;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridBagConstraint;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridConstraint;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED;

import com.intellij.devtools.component.editortextfield.customization.ReadOnlyCustomization;
import com.intellij.devtools.component.editortextfield.customization.WrapTextCustomization;
import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.utils.ClipboardUtils;
import com.intellij.devtools.utils.ProjectUtils;
import com.intellij.icons.AllIcons.Actions;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.EditorTextFieldProvider;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public abstract class Formatter extends Operation {

  private final JPanel dataPanel = new JPanel();
  private final JPanel resultsPanel = new JPanel();

  private final JPanel dataHeaderPanel = new JPanel();
  private final JPanel resultHeaderPanel = new JPanel();

  private final JPanel dataHeaderButtonPanel = new JPanel();

  private JComponent dataScrollPane;
  private JComponent resultScrollPane;

  private EditorTextField dataTextField;
  private EditorTextField resultTextField;

  private final JButton pasteButton = new JButton("Paste", Actions.MenuPaste);
  private final JButton copyButton = new JButton("Copy", Actions.Copy);
  private final JButton clearButton = new JButton("Reset", Actions.Refresh);

  private String dataText = null;
  private String resultText = null;

  private boolean isParametersAdded;

  public Formatter() {
    configureComponents();
    configureParameters(parametersPanel);
    configureLayouts();
    configureListeners();
  }

  @Override
  public OperationGroup getOperationGroup() {
    return null;
  }

  @Override
  public void restoreState() {
    dataTextField.setText(dataText);
    resultTextField.setText(resultText);
  }

  @Override
  public void persistState() {
    dataText = dataTextField.getText();
    resultText = resultTextField.getText();
  }

  @Override
  public void reset() {
    dataTextField.setText(null);
  }

  protected abstract Language getLanguage();

  @Override
  protected void configureComponents() {
    dataTextField =
        EditorTextFieldProvider.getInstance()
            .getEditorField(
                getLanguage(), ProjectUtils.getProject(), List.of(WrapTextCustomization.ENABLED));
    resultTextField =
        EditorTextFieldProvider.getInstance()
            .getEditorField(
                getLanguage(),
                ProjectUtils.getProject(),
                List.of(WrapTextCustomization.ENABLED, ReadOnlyCustomization.ENABLED));

    dataTextField.setName("data-text-area");
    resultTextField.setName("result-text-area");

    clearButton.setText("Reset");
    clearButton.setIcon(Actions.Refresh);
    clearButton.setName("clear-button");
  }

  @Override
  protected void configureParameters(JPanel parametersPanel) {
    parametersPanel.setLayout(new GridBagLayout());
  }

  @Override
  protected void configureLayouts() {

    setLayout(new GridLayoutManager(3, 1));

    this.add(parametersPanel, buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED));
    this.add(dataPanel, buildGridConstraint(1, 0, FILL_BOTH));
    this.add(resultsPanel, buildGridConstraint(2, 0, FILL_BOTH));

    dataHeaderButtonPanel.setLayout(new BoxLayout(dataHeaderButtonPanel, BoxLayout.X_AXIS));
    dataHeaderButtonPanel.add(clearButton);
    dataHeaderButtonPanel.add(pasteButton);

    dataHeaderPanel.setLayout(new BorderLayout());
    dataHeaderPanel.add(new JLabel("Data"), BorderLayout.WEST);
    dataHeaderPanel.add(dataHeaderButtonPanel, BorderLayout.EAST);

    JPanel dataContentPanel = new JPanel();
    dataContentPanel.setLayout(new GridLayoutManager(2, 1));
    dataContentPanel.add(
        dataHeaderPanel,
        buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW));
    dataContentPanel.add(
        dataTextField, buildGridConstraint(1, 0, 1, 1, FILL_BOTH, CAN_SHRINK_AND_GROW));

    resultHeaderPanel.setLayout(new BorderLayout());
    resultHeaderPanel.add(new JLabel("Result"), BorderLayout.WEST);
    resultHeaderPanel.add(copyButton, BorderLayout.EAST);

    JPanel resultContentPanel = new JPanel();
    resultContentPanel.setLayout(new GridLayoutManager(2, 1));
    resultContentPanel.add(
        resultHeaderPanel,
        buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW));
    resultContentPanel.add(
        resultTextField, buildGridConstraint(1, 0, 1, 1, FILL_BOTH, CAN_SHRINK_AND_GROW));

    dataPanel.setLayout(new GridBagLayout());
    resultsPanel.setLayout(new GridBagLayout());
    dataPanel.add(dataContentPanel, buildGridBagConstraint(0, 0, 1.0, 1.0, 1));
    resultsPanel.add(resultContentPanel, buildGridBagConstraint(1, 0, 1.0, 1.0, 1));
  }

  protected void configureListeners() {
    dataTextField.addDocumentListener(
        new DocumentListener() {
          @Override
          public void documentChanged(
              com.intellij.openapi.editor.event.@NotNull DocumentEvent event) {
            updateResult();
          }
        });

    copyButton.addActionListener(
        (ActionEvent evt) -> {
          ClipboardUtils.copy(resultTextField.getText());
        });

    pasteButton.addActionListener(
        (ActionEvent evt) -> {
          ClipboardUtils.paste().ifPresent(dataTextField::setText);
        });
    clearButton.addActionListener(evt -> reset());
  }

  protected void updateResult() {
    resultTextField.setText(null);
    String formattedData = format(getData());
    resultTextField.setText(formattedData);
  }

  protected String getData() {
    return dataTextField.getText();
  }

  private void runInEDThread(Runnable task, EditorTextField resultTextField) {
    ApplicationManager.getApplication()
        .invokeLater(task, ModalityState.stateForComponent(resultTextField));
  }

  protected abstract String format(String rawData);
}
