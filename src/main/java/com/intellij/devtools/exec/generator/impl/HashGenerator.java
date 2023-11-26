package com.intellij.devtools.exec.generator.impl;

import com.intellij.devtools.exec.generator.Generator;
import com.intellij.devtools.utils.ComponentUtils;
import com.intellij.devtools.utils.HashUtils;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBInsets;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class HashGenerator extends Generator {

  private JBLabel countLabel;
  private JBLabel inputLabel;
  private JComboBox<String> hashTypeComboBox;
  private EditorTextField inputTextField;

  private static final List<String> HASH_TYPES =
      Arrays.stream(Security.getProviders())
          .flatMap(provider -> provider.getServices().stream())
          .filter(s -> MessageDigest.class.getSimpleName().equals(s.getType()))
          .map(Provider.Service::getAlgorithm)
          .sorted()
          .toList();

  @Override
  public String getNodeName() {
    return "Hash";
  }

  @Override
  protected void configureComponents() {
    super.configureComponents();
    this.countLabel = new JBLabel("Algorithm");
    this.inputLabel = new JBLabel("Input");
    hashTypeComboBox = new ComboBox<>(HASH_TYPES.toArray(new String[0]));
    inputTextField = ComponentUtils.createEditorTextField(PlainTextLanguage.INSTANCE);
    inputTextField.setPreferredSize(new Dimension(-1, 100));
  }

  @Override
  protected void configureParameters(JPanel parametersPanel) {
    parametersPanel.setLayout(new GridBagLayout());

    GridBag gridBag = new GridBag();
    JBInsets insets = JBInsets.create(3, 3);
    parametersPanel.add(countLabel, gridBag.nextLine().next().insets(insets).fillCellNone());
    parametersPanel.add(hashTypeComboBox, gridBag.next().insets(insets).fillCellNone());
    parametersPanel.add(
        new Spacer(), gridBag.next().fillCellHorizontally().coverLine().weightx(1f));
    parametersPanel.add(
        inputLabel,
        gridBag.nextLine().next().fillCellHorizontally().coverLine().weightx(1f).insets(insets));
    parametersPanel.add(
        inputTextField,
        gridBag.nextLine().next().insets(insets).fillCellHorizontally().weightx(1f).coverLine());
  }

  @Override
  protected void configureListeners() {
    super.configureListeners();
    hashTypeComboBox.addActionListener(evt -> execute());
    inputTextField
        .getDocument()
        .addDocumentListener(
            ComponentUtils.getDocumentChangeListener(
                evt -> {
                  execute();
                  return null;
                }));
    if (HASH_TYPES.contains("MD5")) {
      hashTypeComboBox.setSelectedItem("MD5");
    }
  }

  @Override
  protected String generate() {
    if (Objects.nonNull(hashTypeComboBox.getSelectedItem())
        && Objects.nonNull(inputTextField.getText())) {
      return HashUtils.generateHash(
          (String) hashTypeComboBox.getSelectedItem(), inputTextField.getText());
    }
    return null;
  }

  @Override
  protected boolean showGenerateButtons() {
    return false;
  }

  @Override
  public void reset() {
    super.reset();
    inputTextField.setText(null);
  }
}
