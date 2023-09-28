package com.intellij.devtools.exec.generator;

import static com.intellij.devtools.utils.GridConstraintUtils.CAN_SHRINK_AND_GROW;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridBagConstraint;
import static com.intellij.devtools.utils.GridConstraintUtils.buildGridConstraint;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_NONE;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED;

import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.Parameter;
import com.intellij.devtools.utils.ClipboardUtils;
import com.intellij.devtools.utils.ComponentUtils;
import com.intellij.icons.AllIcons.Actions;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public abstract class Generator extends Operation {

  private final JPanel parametersPanel = new JPanel();
  private final JPanel headerPanel = new JPanel();
  private final JPanel buttonsPanel = new JPanel();
  private final JPanel resultsPanel = new JPanel();

  private final JTextArea resultTextArea = new JTextArea();

  private final JButton generateButton = new JButton("Generate", Actions.Run_anything);
  private final JButton generateAndCopyButton = new JButton("Generate And Copy", Actions.RunAll);
  private final JButton copyButton = new JButton("Copy", Actions.Copy);
  private final JButton clearButton = new JButton("Reset", Actions.Refresh);

  private boolean isParametersAdded;

  private String resultText = null;

  private final transient Map<String, List<Parameter>> parameterMap;
  private transient Map<String, Object> parameterResult = Map.of();

  protected Generator() {
    configureComponents();
    configureLayout();
    configureListeners();
    parameterMap = Optional.ofNullable(getParameterGroups())
        .orElseGet(ArrayList::new)
        .stream()
        .flatMap(parameterGroup -> parameterGroup.getParameters().stream())
        .collect(Collectors.groupingBy(Parameter::getName));
  }

  protected Map<String, Object> getParameterResult() {
    return super.getParameterResult(parametersPanel);
  }

  protected abstract String generate();

  @Override
  public void restoreState() {
    resultTextArea.setText(resultText);
    for (Component panel : parametersPanel.getComponents()) {
      if(panel instanceof JPanel) {
        Component[] components = ((JPanel) panel).getComponents();
        Component component = components[1];
        if(component instanceof Spacer) {
          continue;
        }
        String name = component.getName();
        ComponentUtils.setValue(component, parameterResult.getOrDefault(name, parameterMap.get(name).get(0).getDefaultValue()));
      }
    }
  }

  @Override
  public void persistState() {
    resultText = resultTextArea.getText();
    parameterResult = getParameterResult(parametersPanel);
  }

  @Override
  public void reset() {
    resultTextArea.setText(null);
  }

  private void configureComponents() {
    resultTextArea.setEditable(false);
    isParametersAdded = configureParameters(parametersPanel);
    clearButton.setName("clear-button");
  }

  private void configureLayout() {

    buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
    buttonsPanel.add(generateAndCopyButton);
    buttonsPanel.add(generateButton);
    buttonsPanel.add(copyButton);
    buttonsPanel.add(clearButton);

    headerPanel.setLayout(new GridLayoutManager(1, 2));
    headerPanel.add(new JLabel("Generate"), buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_CAN_GROW));
    headerPanel.add(buttonsPanel, buildGridConstraint(0, 1, 1, 1, FILL_NONE, SIZEPOLICY_FIXED));

    resultsPanel.setLayout(new GridBagLayout());
    resultsPanel.add(resultTextArea, buildGridBagConstraint(0, 0, 1.0, 1.0, 1));

    setLayout(new GridLayoutManager(4, 1));
    if(isParametersAdded) {
      add(parametersPanel, buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED));
    }
    add(headerPanel,  buildGridConstraint(1, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW));
    add(resultsPanel, buildGridConstraint(2, 0, 1, 1, FILL_BOTH, CAN_SHRINK_AND_GROW));
    add(new Spacer(), buildGridConstraint(3, 0, 1, 1, FILL_BOTH, CAN_SHRINK_AND_GROW));
  }

  private void configureListeners() {
    generateButton.addActionListener(
        (evt) -> {
          reset();
          resultTextArea.setText(generate());
        });
    copyButton.addActionListener(
        (evt) -> {
          String copyText = Optional.ofNullable(resultTextArea.getSelectedText())
              .orElseGet(resultTextArea::getText);
          ClipboardUtils.copy(copyText);
        });
    generateAndCopyButton.addActionListener(
        (evt) -> {
          reset();
          resultTextArea.setText(generate());
          ClipboardUtils.copy(resultTextArea.getText());
        });
    clearButton.addActionListener(evt -> reset());
  }
}
