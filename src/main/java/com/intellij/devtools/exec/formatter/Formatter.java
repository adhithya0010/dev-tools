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
import com.intellij.devtools.exec.Orientation;
import com.intellij.devtools.utils.ClipboardUtils;
import com.intellij.devtools.utils.ProjectUtils;
import com.intellij.icons.AllIcons.Actions;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.EditorTextFieldProvider;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBPanel;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public abstract class Formatter extends Operation {

  private final JBSplitter splitter = new JBSplitter(true);
  private final JPanel dataPanel = new JBPanel<>();
  private final JPanel resultsPanel = new JBPanel<>();

  private final JPanel dataHeaderPanel = new JBPanel<>();
  private final JPanel resultHeaderPanel = new JBPanel<>();

  private final JPanel dataHeaderButtonPanel = new JBPanel<>();

  private EditorTextField dataTextField;
  private EditorTextField resultTextField;

  private final JButton pasteButton = new JButton("Paste", Actions.MenuPaste);
  private final JButton copyButton = new JButton("Copy", Actions.Copy);
  private final JButton clearButton = new JButton("Reset", Actions.Refresh);

  private String dataText = null;
  private String resultText = null;

  protected Formatter() {
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

    setLayout(new GridLayoutManager(2, 1));

    splitter.setFirstComponent(dataPanel);
    splitter.setSecondComponent(resultsPanel);

    this.add(parametersPanel, buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED));
    this.add(splitter, buildGridConstraint(1, 0, FILL_BOTH));

    dataHeaderButtonPanel.setLayout(new BoxLayout(dataHeaderButtonPanel, BoxLayout.X_AXIS));
    dataHeaderButtonPanel.add(clearButton);
    dataHeaderButtonPanel.add(pasteButton);

    dataHeaderPanel.setLayout(new BorderLayout());
    dataHeaderPanel.add(new JLabel("Data"), BorderLayout.WEST);
    dataHeaderPanel.add(dataHeaderButtonPanel, BorderLayout.EAST);

    JPanel dataContentPanel = new JBPanel<>();
    dataContentPanel.setLayout(new GridLayoutManager(2, 1));
    dataContentPanel.add(
        dataHeaderPanel,
        buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW));
    dataContentPanel.add(
        dataTextField, buildGridConstraint(1, 0, 1, 1, FILL_BOTH, CAN_SHRINK_AND_GROW));

    resultHeaderPanel.setLayout(new BorderLayout());
    resultHeaderPanel.add(new JLabel("Result"), BorderLayout.WEST);
    resultHeaderPanel.add(copyButton, BorderLayout.EAST);

    JPanel resultContentPanel = new JBPanel<>();
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

  @Override
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

  @Override
  public boolean isOrientationSupported() {
    return true;
  }

  @Override
  public void setOrientation(Orientation orientation) {
    super.setOrientation(orientation);
    splitter.setOrientation(Orientation.HORIZONTAL.equals(orientation));
  }

  protected void updateResult() {
    resultTextField.setText(null);
    String formattedData = format(getData());
    resultTextField.setText(formattedData);
  }

  protected String getData() {
    return dataTextField.getText();
  }

  protected abstract String format(String rawData);
}
