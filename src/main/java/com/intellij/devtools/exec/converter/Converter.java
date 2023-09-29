package com.intellij.devtools.exec.converter;

import static com.intellij.devtools.utils.GridConstraintUtils.CAN_SHRINK_AND_GROW;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridBagConstraint;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridConstraint;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED;

import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.Parameter;
import com.intellij.devtools.utils.ClipboardUtils;
import com.intellij.devtools.utils.ComponentUtils;
import com.intellij.icons.AllIcons.Actions;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.PlatformIcons;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;

public abstract class Converter extends Operation {

  private final JPanel parametersPanel = new JPanel();
  private final JPanel fromPanel = new JPanel();
  private final JPanel toPanel = new JPanel();

  private final JPanel fromHeaderPanel = new JPanel();
  private final JPanel fromButtonPanel = new JPanel();

  private final JPanel toHeaderPanel = new JPanel();
  private final JPanel toButtonPanel = new JPanel();

  private final JTextArea fromTextArea = new JTextArea();
  private final JTextArea toTextArea = new JTextArea();

  private final JButton clearButton = new JButton();

  private final JButton fromCopyButton = new JButton();
  private final JButton fromPasteButton = new JButton();

  private final JButton toCopyButton = new JButton();
  private final JButton toPasteButton = new JButton();

  private final AtomicBoolean isConversionInProgress = new AtomicBoolean(false);
  private boolean isParametersAdded;

  private String fromText = null;
  private String toText = null;

  private final transient Map<String, List<Parameter>> parameterMap;
  private transient Map<String, Object> parameterResult = Map.of();

  protected Converter() {
    configureComponents();
    configureLayouts();
    configureListeners();
    parameterMap =
        Optional.ofNullable(getParameterGroups()).orElseGet(ArrayList::new).stream()
            .flatMap(parameterGroup -> parameterGroup.getParameters().stream())
            .collect(Collectors.groupingBy(Parameter::getName));
  }

  protected abstract String convertTo(String data);

  protected abstract String convertFrom(String data);

  protected abstract String getFromLabel();

  protected abstract String getToLabel();

  public Map<String, Object> getParameterResult() {
    return super.getParameterResult(parametersPanel);
  }

  @Override
  public void restoreState() {
    fromTextArea.setText(fromText);
    toTextArea.setText(toText);
    for (Component panel : parametersPanel.getComponents()) {
      if (panel instanceof JPanel) {
        Component[] components = ((JPanel) panel).getComponents();
        Component component = components[1];
        String name = component.getName();
        ComponentUtils.setValue(
            component,
            parameterResult.getOrDefault(name, parameterMap.get(name).get(0).getDefaultValue()));
      }
    }
  }

  @Override
  public void persistState() {
    fromText = fromTextArea.getText();
    toText = toTextArea.getText();
    parameterResult = getParameterResult(parametersPanel);
  }

  @Override
  public void reset() {
    ComponentUtils.resetTextField(fromTextArea);
  }

  private void configureComponents() {
    fromTextArea.setLineWrap(true);
    fromTextArea.setName("from-text-area");

    toTextArea.setLineWrap(true);
    toTextArea.setName("to-text-area");

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

    isParametersAdded = configureParameters(parametersPanel);
  }

  private void configureLayouts() {
    ComponentUtils.removeAllChildren(this);

    setLayout(new GridLayoutManager(3, 1));
    if (isParametersAdded) {
      add(parametersPanel, buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED));
    }
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
        ComponentUtils.attachScroll(fromTextArea),
        buildGridConstraint(1, 0, 1, 1, FILL_BOTH, CAN_SHRINK_AND_GROW));

    toContentPanel.setLayout(new GridLayoutManager(2, 1));
    toContentPanel.add(
        toHeaderPanel,
        buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW));
    toContentPanel.add(
        ComponentUtils.attachScroll(toTextArea),
        buildGridConstraint(1, 0, 1, 1, FILL_BOTH, CAN_SHRINK_AND_GROW));

    fromPanel.setLayout(new GridBagLayout());
    toPanel.setLayout(new GridBagLayout());

    fromPanel.add(fromContentPanel, buildGridBagConstraint(0, 0, 1.0, 1.0, 1));
    toPanel.add(toContentPanel, buildGridBagConstraint(1, 0, 1.0, 1.0, 1));

    ComponentUtils.refreshComponent(this);
  }

  private void configureListeners() {
    Document fromDocument = fromTextArea.getDocument();
    Document toDocument = toTextArea.getDocument();
    fromDocument.addDocumentListener(ComponentUtils.getDocumentChangeListener(this::convertTo));
    toDocument.addDocumentListener(ComponentUtils.getDocumentChangeListener(this::convertFrom));

    clearButton.addActionListener(evt -> reset());

    fromCopyButton.addActionListener(evt -> ClipboardUtils.copy(fromTextArea.getText()));
    fromPasteButton.addActionListener(
        evt -> {
          ClipboardUtils.paste().ifPresent(fromTextArea::setText);
        });

    toCopyButton.addActionListener(evt -> ClipboardUtils.copy(toTextArea.getText()));
    toPasteButton.addActionListener(
        evt -> {
          ClipboardUtils.paste().ifPresent(toTextArea::setText);
        });
  }

  private Void convertFrom(DocumentEvent e) {
    if (!isConversionInProgress.get()) {
      isConversionInProgress.set(true);
      try {
        fromTextArea.setText(convertFrom(toTextArea.getText()));
      } finally {
        isConversionInProgress.set(false);
      }
    }
    return null;
  }

  private Void convertTo(DocumentEvent e) {
    if (!isConversionInProgress.get()) {
      isConversionInProgress.set(true);
      try {
        toTextArea.setText(convertTo(fromTextArea.getText()));
      } finally {
        isConversionInProgress.set(false);
      }
    }
    return null;
  }
}
