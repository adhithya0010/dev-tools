package com.intellij.devtools.exec.formatter;

import com.intellij.devtools.exec.PrettifyConfig;
import com.intellij.ui.JBIntSpinner;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBInsets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Insets;

public abstract class PrettyFormatter extends Formatter {

  private JBIntSpinner indentLengthSpinner;

  @Override
  protected void configureComponents() {
    super.configureComponents();

    indentLengthSpinner = new JBIntSpinner(2, 1, 16, 1);
    indentLengthSpinner.setPreferredSize(new Dimension(100, 30));
    indentLengthSpinner.setMinimumSize(new Dimension(100, 30));
  }

  @Override
  protected void configureParameters_(JPanel parametersPanel) {
    super.configureParameters_(parametersPanel);

    GridBag gbc = new GridBag();
    if (isIndentLengthEnabled()) {
      Insets insets = JBInsets.create(3, 3);
      gbc.nextLine();
      parametersPanel.add(new JLabel("Indent"), gbc.next().insets(insets).fillCellNone());
      parametersPanel.add(indentLengthSpinner, gbc.next().insets(insets).fillCellNone());
      parametersPanel.add(new Spacer(), gbc.next().insets(insets).fillCellHorizontally().coverLine(2).weightx(1f));
    }
  }

  @Override
  protected void configureListeners() {
    super.configureListeners();
    indentLengthSpinner.addChangeListener(
        evt -> {
          updateResult();
        });
  }

  @Override
  protected final String format(String rawData) {
    PrettifyConfig prettifyConfig =
        PrettifyConfig.builder().indentLength(indentLengthSpinner.getNumber()).build();
    return format(rawData, prettifyConfig);
  }

  protected boolean isIndentLengthEnabled() {
    return true;
  }

  protected abstract String format(String rawData, PrettifyConfig prettifyConfig);
}
