package com.intellij.devtools.exec.generator;

import static com.intellij.devtools.utils.GridConstraintUtils.CAN_SHRINK_AND_GROW;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridBagConstraint;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridConstraint;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_NONE;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED;

import com.intellij.devtools.component.editortextfield.customization.ReadOnlyCustomization;
import com.intellij.devtools.component.editortextfield.customization.WrapTextCustomization;
import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.utils.ClipboardUtils;
import com.intellij.devtools.utils.ComponentUtils;
import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBPanel;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.GridBagLayout;
import java.util.Optional;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public abstract class Generator extends Operation {

  private final JPanel headerPanel = new JBPanel<>();
  private final JPanel buttonsPanel = new JBPanel<>();
  private final JPanel resultsPanel = new JBPanel<>();

  private EditorTextField resultTextField;

  private final JButton generateButton = new JButton("Generate", Actions.Run_anything);
  private final JButton generateAndCopyButton = new JButton("Generate And Copy", Actions.RunAll);
  private final JButton copyButton = new JButton("Copy", Actions.Copy);
  private final JButton clearButton = new JButton("Reset", Actions.Refresh);

  private String resultText = null;

  protected Generator() {
    configureComponents();
    configureParameters(parametersPanel);
    configureLayouts();
    configureListeners();
  }

  protected abstract String generate();

  @Override
  public OperationGroup getOperationGroup() {
    return null;
  }

  @Override
  public OperationCategory getOperationCategory() {
    return null;
  }

  @Override
  public void restoreState() {
    resultTextField.setText(resultText);
  }

  @Override
  public void persistState() {
    resultText = resultTextField.getText();
  }

  @Override
  public void reset() {
    resultTextField.setText(null);
  }

  @Override
  protected void configureComponents() {
    resultTextField =
        ComponentUtils.createEditorTextField(
            PlainTextLanguage.INSTANCE,
            ReadOnlyCustomization.ENABLED,
            WrapTextCustomization.ENABLED);
    clearButton.setName("clear-button");
  }

  @Override
  protected void configureLayouts() {

    buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
    if (showGenerateButtons()) {
      buttonsPanel.add(generateAndCopyButton);
      buttonsPanel.add(generateButton);
    }
    buttonsPanel.add(copyButton);
    buttonsPanel.add(clearButton);

    headerPanel.setLayout(new GridLayoutManager(1, 2));
    headerPanel.add(
        new JLabel("Result"),
        buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_CAN_GROW));
    headerPanel.add(buttonsPanel, buildGridConstraint(0, 1, 1, 1, FILL_NONE, SIZEPOLICY_FIXED));

    resultsPanel.setLayout(new GridBagLayout());
    resultsPanel.add(resultTextField, buildGridBagConstraint(0, 0, 1.0, 1.0, 1));

    setLayout(new GridLayoutManager(4, 1));
    add(parametersPanel, buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED));
    add(
        headerPanel,
        buildGridConstraint(1, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW));
    add(resultsPanel, buildGridConstraint(2, 0, 1, 1, FILL_BOTH, CAN_SHRINK_AND_GROW));
    add(new Spacer(), buildGridConstraint(3, 0, 1, 1, FILL_BOTH, CAN_SHRINK_AND_GROW));
  }

  @Override
  protected void configureListeners() {
    generateButton.addActionListener((evt) -> execute());
    copyButton.addActionListener(
        (evt) -> {
          String copyText =
              Optional.ofNullable(resultTextField.getEditor())
                  .map(Editor::getSelectionModel)
                  .map(SelectionModel::getSelectedText)
                  .orElseGet(resultTextField::getText);
          ClipboardUtils.copy(copyText);
        });
    generateAndCopyButton.addActionListener(
        (evt) -> {
          execute();
          ClipboardUtils.copy(resultTextField.getText());
        });
    clearButton.addActionListener(evt -> reset());
  }

  protected void execute() {
    SwingUtilities.invokeLater(() -> resultTextField.setText(generate()));
  }

  protected boolean showGenerateButtons() {
    return true;
  }
}
