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
import com.intellij.devtools.exec.Parameter;
import com.intellij.devtools.utils.ClipboardUtils;
import com.intellij.devtools.utils.ComponentUtils;
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
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public abstract class Formatter extends Operation {

  private final JPanel parametersPanel = new JPanel();
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
  private final JButton clearButton = new JButton();

  private String dataText = null;
  private String resultText = null;

  private boolean isParametersAdded;
  private final transient Map<String, List<Parameter>> parameterMap;
  private transient Map<String, Object> parameterResult = Map.of();

  protected Formatter() {
    configureComponents();
    configureLayout();
    configureListeners();
    parameterMap =
        Optional.ofNullable(getParameterGroups()).orElseGet(ArrayList::new).stream()
            .flatMap(parameterGroup -> parameterGroup.getParameters().stream())
            .collect(Collectors.groupingBy(Parameter::getName));
  }

  @Override
  public void restoreState() {
    dataTextField.setText(dataText);
    resultTextField.setText(resultText);
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
    dataText = dataTextField.getText();
    resultText = resultTextField.getText();
    parameterResult = getParameterResult(parametersPanel);
  }

  @Override
  public void reset() {
    dataTextField.setText(null);
    //    ComponentUtils.resetTextField(dataTextField);
  }

  protected abstract Language getLanguage();

  private void configureComponents() {

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

    //    dataTextField.setLineWrap(true);
    dataTextField.setName("data-text-area");

    //    resultTextField.setLineWrap(true);
    resultTextField.setName("result-text-area");

    //    dataScrollPane = ComponentUtils.attachScroll(dataTextField);
    //    resultScrollPane = ComponentUtils.attachScroll(resultTextField);

    clearButton.setText("Reset");
    clearButton.setIcon(Actions.Refresh);
    clearButton.setName("clear-button");

    configureParameters();
  }

  private void configureParameters() {
    //    List<Parameter> parameters = getParameterGroups();
    //    if(CollectionUtils.isEmpty(parameters)) {
    //      parametersPanel.setVisible(false);
    //      return;
    //    }
    //    parametersPanel.setLayout(new GridLayoutManager(parameters.size(), 1));
    //    AtomicInteger i = new AtomicInteger(0);
    //    parameters.forEach(parameter -> {
    //      JPanel row = new JPanel(new GridBagLayout());
    //      Optional<JComponent> component = ComponentUtils.toComponent(parameter);
    //      if(component.isPresent()) {
    //        JLabel label = new JLabel(parameter.getLabel());
    //        JComponent value = component.get();
    //        value.setName(parameter.getName());
    //        row.add(label, buildGridBagConstraint(0, 0, FILL_NONE));
    //        row.add(value, buildGridBagConstraint(1, 0, 1.0, 1.0, FILL_HORIZONTAL));
    //        parametersPanel.add(row, buildGridConstraint(i.getAndIncrement(), 0,
    // FILL_HORIZONTAL));
    //        this.isParametersAdded = true;
    //      }
    //    });
    //    parametersPanel.setVisible(true);
  }

  private void configureLayout() {

    setLayout(new GridLayoutManager(3, 1));
    if (isParametersAdded) {
      add(parametersPanel, buildGridConstraint(0, 0, 1, 1, FILL_HORIZONTAL, SIZEPOLICY_FIXED));
    }
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

  private void configureListeners() {
    dataTextField.addDocumentListener(
        new DocumentListener() {
          @Override
          public void documentChanged(
              com.intellij.openapi.editor.event.@NotNull DocumentEvent event) {
            resultTextField.setText(null);
            String formattedData = format(dataTextField.getText());
            resultTextField.setText(formattedData);
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

  private void runInEDThread(Runnable task, EditorTextField resultTextField) {
    ApplicationManager.getApplication()
        .invokeLater(task, ModalityState.stateForComponent(resultTextField));
  }

  protected abstract String format(String rawData);
}
