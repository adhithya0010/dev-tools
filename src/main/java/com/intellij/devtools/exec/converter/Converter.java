package com.intellij.devtools.exec.converter;

import static com.intellij.devtools.utils.GridConstraintUtils.CAN_SHRINK_AND_GROW;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridBagConstraint;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridConstraint;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED;

import com.intellij.devtools.component.editortextfield.customization.WrapTextCustomization;
import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.OperationCategory;
import com.intellij.devtools.exec.OperationGroup;
import com.intellij.devtools.utils.ClipboardUtils;
import com.intellij.devtools.utils.ComponentUtils;
import com.intellij.devtools.utils.ProjectUtils;
import com.intellij.icons.AllIcons.Actions;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.EditorTextFieldProvider;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.PlatformIcons;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class Converter extends Operation {

  private final JPanel fromPanel = new JPanel();
  private final JPanel toPanel = new JPanel();

  private final JPanel fromHeaderPanel = new JPanel();
  private final JPanel fromButtonPanel = new JPanel();

  private final JPanel toHeaderPanel = new JPanel();
  private final JPanel toButtonPanel = new JPanel();

  private EditorTextField fromTextField;
  private EditorTextField toTextField;

  private final JButton clearButton = new JButton();

  private final JButton fromCopyButton = new JButton();
  private final JButton fromPasteButton = new JButton();

  private final JButton toCopyButton = new JButton();
  private final JButton toPasteButton = new JButton();

  private final AtomicBoolean isConversionInProgress = new AtomicBoolean(false);

  private String fromText = null;
  private String toText = null;

  protected abstract String convertTo(String data);

  protected abstract String convertFrom(String data);

  protected abstract String getFromLabel();

  protected abstract String getToLabel();

  protected abstract Language getFromLanguage();

  protected abstract Language getToLanguage();

  protected Converter() {
    configureComponents();
    configureParameters(parametersPanel);
    configureLayouts();
    configureListeners();
  }

  @Override
  public OperationCategory getOperationCategory() {
    return null;
  }

  @Override
  public OperationGroup getOperationGroup() {
    return null;
  }

  @Override
  public void restoreState() {
    fromTextField.setText(fromText);
    toTextField.setText(toText);
  }

  @Override
  public void persistState() {
    fromText = fromTextField.getText();
    toText = toTextField.getText();
  }

  @Override
  public void reset() {
    fromTextField.setText(null);
  }

  @Override
  protected void configureComponents() {
    fromTextField =
        EditorTextFieldProvider.getInstance()
            .getEditorField(
                getFromLanguage(),
                ProjectUtils.getProject(),
                List.of(WrapTextCustomization.ENABLED));
    toTextField =
        EditorTextFieldProvider.getInstance()
            .getEditorField(
                getToLanguage(), ProjectUtils.getProject(), List.of(WrapTextCustomization.ENABLED));

    fromTextField.setName("from-text-area");
    toTextField.setName("to-text-area");

    fromCopyButton.setIcon(PlatformIcons.COPY_ICON);
    fromCopyButton.setText("Copy");
    fromCopyButton.setName("from-copy-button");
    fromPasteButton.setIcon(Actions.MenuPaste);
    fromPasteButton.setText("Paste");
    fromPasteButton.setName("from-paste-button");
    clearButton.setText("Reset");
    clearButton.setIcon(Actions.Refresh);
    clearButton.setName("clear-button");

    toCopyButton.setIcon(PlatformIcons.COPY_ICON);
    toCopyButton.setText("Copy");
    toCopyButton.setName("to-copy-button");
    toPasteButton.setIcon(Actions.MenuPaste);
    toPasteButton.setText("Paste");
    toPasteButton.setName("to-paste-button");
  }

  @Override
  protected void configureLayouts() {
    ComponentUtils.removeAllChildren(this);

    setLayout(new GridLayoutManager(3, 1));
    add(parametersPanel, buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED));
    add(fromPanel, buildGridConstraint(1, 0, FILL_BOTH));
    add(toPanel, buildGridConstraint(2, 0, FILL_BOTH));

    fromHeaderPanel.setLayout(new BorderLayout());
    fromHeaderPanel.add(new JLabel(getFromLabel()), BorderLayout.WEST);
    fromButtonPanel.setLayout(new BoxLayout(fromButtonPanel, BoxLayout.X_AXIS));
    fromButtonPanel.add(clearButton);
    fromButtonPanel.add(fromCopyButton);
    fromButtonPanel.add(fromPasteButton);

    fromHeaderPanel.add(fromButtonPanel, BorderLayout.EAST);

    toHeaderPanel.setLayout(new BorderLayout());
    toButtonPanel.setLayout(new BoxLayout(toButtonPanel, BoxLayout.X_AXIS));
    toButtonPanel.add(toCopyButton);
    toButtonPanel.add(toPasteButton);

    toHeaderPanel.add(new JLabel(getToLabel()), BorderLayout.WEST);
    toHeaderPanel.add(toButtonPanel, BorderLayout.EAST);

    JPanel fromContentPanel = new JPanel();
    JPanel toContentPanel = new JPanel();

    fromContentPanel.setLayout(new GridLayoutManager(2, 1));
    fromContentPanel.add(
        fromHeaderPanel,
        buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW));
    fromContentPanel.add(
        fromTextField, buildGridConstraint(1, 0, 1, 1, FILL_BOTH, CAN_SHRINK_AND_GROW));

    toContentPanel.setLayout(new GridLayoutManager(2, 1));
    toContentPanel.add(
        toHeaderPanel,
        buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW));
    toContentPanel.add(
        toTextField, buildGridConstraint(1, 0, 1, 1, FILL_BOTH, CAN_SHRINK_AND_GROW));

    fromPanel.setLayout(new GridBagLayout());
    toPanel.setLayout(new GridBagLayout());

    fromPanel.add(fromContentPanel, buildGridBagConstraint(0, 0, 1.0, 1.0, 1));
    toPanel.add(toContentPanel, buildGridBagConstraint(1, 0, 1.0, 1.0, 1));

    ComponentUtils.refreshComponent(this);
  }

  @Override
  protected void configureParameters(JPanel parametersPanel) {
    super.configureParameters(parametersPanel);
    parametersPanel.setLayout(new GridBagLayout());
  }

  @Override
  protected void configureListeners() {
    fromTextField.addDocumentListener(
        new DocumentListener() {
          @Override
          public void documentChanged(com.intellij.openapi.editor.event.DocumentEvent event) {
            convertTo(event);
          }
        });
    toTextField.addDocumentListener(
        new DocumentListener() {
          @Override
          public void documentChanged(com.intellij.openapi.editor.event.DocumentEvent event) {
            convertFrom(event);
          }
        });

    clearButton.addActionListener(evt -> reset());

    fromCopyButton.addActionListener(evt -> ClipboardUtils.copy(fromTextField.getText()));
    fromPasteButton.addActionListener(
        evt -> {
          ClipboardUtils.paste().ifPresent(fromTextField::setText);
        });

    toCopyButton.addActionListener(evt -> ClipboardUtils.copy(toTextField.getText()));
    toPasteButton.addActionListener(
        evt -> {
          ClipboardUtils.paste().ifPresent(toTextField::setText);
        });
  }

  private Void convertFrom(com.intellij.openapi.editor.event.DocumentEvent e) {
    if (!isConversionInProgress.get()) {
      isConversionInProgress.set(true);
      try {
        fromTextField.setText(convertFrom(toTextField.getText()));
      } finally {
        isConversionInProgress.set(false);
      }
    }
    return null;
  }

  private Void convertTo(com.intellij.openapi.editor.event.DocumentEvent e) {
    if (!isConversionInProgress.get()) {
      isConversionInProgress.set(true);
      try {
        toTextField.setText(convertTo(fromTextField.getText()));
      } finally {
        isConversionInProgress.set(false);
      }
    }
    return null;
  }
}
