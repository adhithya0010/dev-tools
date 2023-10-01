package com.intellij.devtools.exec.misc.text;

import static com.intellij.devtools.utils.GridConstraintUtils.CAN_SHRINK_AND_GROW;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridBagConstraint;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridConstraint;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED;

import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.utils.ClipboardUtils;
import com.intellij.devtools.utils.ComponentUtils;
import com.intellij.devtools.utils.ProjectUtils;
import com.intellij.devtools.utils.TextUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.EditorTextFieldProvider;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class LinesSort extends Operation {

  private final JPanel dataPanel = new JPanel();
  private final JPanel resultsPanel = new JPanel();

  private final JPanel dataHeaderPanel = new JPanel();
  private final JPanel resultHeaderPanel = new JPanel();

  private final JPanel dataHeaderButtonPanel = new JPanel();
  private final JPanel resultsHeaderButtonPanel = new JPanel();

  private final JLabel dataLabel = new JLabel("Input");
  private final JLabel resultsLabel = new JLabel("Results");

  private EditorTextField dataTextField;
  private EditorTextField resultTextField;

  private final JButton pasteButton = new JButton("Paste", AllIcons.Actions.MenuPaste);
  private final JButton copyButton = new JButton("Copy", AllIcons.Actions.Copy);
  private final JButton clearButton = new JButton();

  private String dataText = null;
  private String resultText = null;

  public LinesSort() {
    this.configureComponents();
    this.configureLayout();
    this.configureListeners();
  }

  private void configureComponents() {
    dataTextField =
        EditorTextFieldProvider.getInstance()
            .getEditorField(PlainTextLanguage.INSTANCE, ProjectUtils.getProject(), List.of());
    resultTextField =
        EditorTextFieldProvider.getInstance()
            .getEditorField(PlainTextLanguage.INSTANCE, ProjectUtils.getProject(), List.of());

    dataTextField.setName("data-text-area");
    resultTextField.setName("result-text-area");

    clearButton.setText("Reset");
    clearButton.setIcon(AllIcons.Actions.Refresh);
    clearButton.setName("clear-button");
  }

  private void configureLayout() {
    setLayout(new GridLayoutManager(2, 1));

    this.add(dataPanel, buildGridConstraint(0, 0, FILL_BOTH));
    this.add(resultsPanel, buildGridConstraint(1, 0, FILL_BOTH));

    dataHeaderButtonPanel.setLayout(new BoxLayout(dataHeaderButtonPanel, BoxLayout.X_AXIS));
    dataHeaderButtonPanel.add(clearButton);
    dataHeaderButtonPanel.add(pasteButton);

    dataHeaderPanel.setLayout(new BorderLayout());
    dataHeaderPanel.add(dataLabel, BorderLayout.WEST);
    dataHeaderPanel.add(dataHeaderButtonPanel, BorderLayout.EAST);

    JPanel dataContentPanel = new JPanel();
    dataContentPanel.setLayout(new GridLayoutManager(2, 1));
    dataContentPanel.add(
        dataHeaderPanel,
        buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW));
    dataContentPanel.add(
        dataTextField, buildGridConstraint(1, 0, 1, 1, FILL_BOTH, CAN_SHRINK_AND_GROW));

    resultHeaderPanel.setLayout(new BorderLayout());
    resultHeaderPanel.add(resultsLabel, BorderLayout.WEST);
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

  private void configureListeners() {
    dataTextField.addDocumentListener(
        ComponentUtils.getDocumentChangeListener(
            (DocumentEvent e) -> {
              runInEDThread(
                  () -> {
                    ComponentUtils.resetTextField(resultTextField);
                    resultTextField.setText(TextUtils.sortLines(dataTextField.getText()));
                  });
              return null;
            }));

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

  private void runInEDThread(Runnable task) {
    SwingUtilities.invokeLater(task);
  }

  @Override
  public String getNodeName() {
    return "Sort Lines";
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public OperationCategory getOperationCategory() {
    return OperationCategory.TEXT;
  }

  @Override
  public OperationGroup getOperationGroup() {
    return OperationGroup.MISC;
  }

  @Override
  public void reset() {
    ComponentUtils.resetTextField(dataTextField);
  }

  @Override
  public void persistState() {
    dataText = dataTextField.getText();
    resultText = resultTextField.getText();
  }

  @Override
  public void restoreState() {
    dataTextField.setText(dataText);
    resultTextField.setText(resultText);
  }
}
