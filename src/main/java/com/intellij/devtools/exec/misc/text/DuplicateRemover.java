package com.intellij.devtools.exec.misc.text;

import static com.intellij.devtools.utils.GridConstraintUtils.CAN_SHRINK_AND_GROW;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridBagConstraint;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridConstraint;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED;

import com.intellij.devtools.component.editortextfield.customization.ReadOnlyCustomization;
import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.Orientation;
import com.intellij.devtools.utils.ClipboardUtils;
import com.intellij.devtools.utils.ComponentUtils;
import com.intellij.devtools.utils.ProjectUtils;
import com.intellij.devtools.utils.TextUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.EditorTextFieldProvider;
import com.intellij.ui.JBSplitter;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Optional;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class DuplicateRemover extends Operation {

  private final JBSplitter splitter = new JBSplitter(true);
  private final JPanel dataPanel = new JPanel();
  private final JPanel resultsPanel = new JPanel();

  private final JPanel dataHeaderPanel = new JPanel();
  private final JPanel resultHeaderPanel = new JPanel();

  private final JPanel dataHeaderButtonPanel = new JPanel();

  private final JLabel dataLabel = new JLabel("Input");
  private final JLabel resultsLabel = new JLabel("Results");

  private EditorTextField dataTextField;
  private EditorTextField resultTextField;

  private final JButton pasteButton = new JButton("Paste", AllIcons.Actions.MenuPaste);
  private final JButton copyButton = new JButton("Copy", AllIcons.Actions.Copy);
  private final JButton clearButton = new JButton();

  private String dataText = null;
  private String resultText = null;

  public DuplicateRemover() {
    this.configureComponents();
    this.configureLayouts();
    this.configureListeners();
  }

  @Override
  public void configureComponents() {
    dataTextField =
        EditorTextFieldProvider.getInstance()
            .getEditorField(PlainTextLanguage.INSTANCE, ProjectUtils.getProject(), List.of());
    resultTextField =
        EditorTextFieldProvider.getInstance()
            .getEditorField(
                PlainTextLanguage.INSTANCE,
                ProjectUtils.getProject(),
                List.of(ReadOnlyCustomization.ENABLED));

    dataTextField.setName("data-text-area");
    resultTextField.setName("result-text-area");

    clearButton.setText("Reset");
    clearButton.setIcon(AllIcons.Actions.Refresh);
    clearButton.setName("clear-button");
  }

  @Override
  protected void configureLayouts() {
    setLayout(new GridLayoutManager(2, 1));

    this.add(splitter, buildGridConstraint(0, 0, FILL_BOTH));

    splitter.setFirstComponent(dataPanel);
    splitter.setSecondComponent(resultsPanel);

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

  @Override
  protected void configureListeners() {
    dataTextField.addDocumentListener(
        ComponentUtils.getDocumentChangeListener(
            (DocumentEvent e) -> {
              runInEDThread(
                  () -> {
                    ComponentUtils.resetTextField(resultTextField);
                    resultTextField.setText(TextUtils.removeDuplicates(dataTextField.getText()));
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
    return "Remove Duplicate Lines";
  }

  @Override
  public void reset() {
    ComponentUtils.resetTextField(dataTextField);
  }

  @Override
  public void persistState() {
    dataText = Optional.ofNullable(dataTextField).map(EditorTextField::getText).orElse(null);
    resultText = Optional.ofNullable(resultTextField).map(EditorTextField::getText).orElse(null);
  }

  @Override
  public void restoreState() {
    Optional.ofNullable(dataTextField).ifPresent(component -> component.setText(dataText));
    Optional.ofNullable(resultTextField).ifPresent(component -> component.setText(resultText));
  }

  @Override
  public void setOrientation(Orientation orientation) {
    super.setOrientation(orientation);
    splitter.setOrientation(Orientation.HORIZONTAL.equals(orientation));
  }

  @Override
  public boolean isOrientationSupported() {
    return true;
  }
}
