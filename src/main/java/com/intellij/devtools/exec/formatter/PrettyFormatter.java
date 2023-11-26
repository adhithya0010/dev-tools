package com.intellij.devtools.exec.formatter;

import com.intellij.devtools.exec.PrettifyConfig;
import com.intellij.ui.JBIntSpinner;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBInsets;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class PrettyFormatter extends Formatter {

  private static final int DEFAULT_INDENT_LENGTH = 2;

  private JBIntSpinner indentLengthSpinner;
  private int indentLength = DEFAULT_INDENT_LENGTH;

  @Override
  protected void configureComponents() {
    super.configureComponents();

    indentLengthSpinner = new JBIntSpinner(DEFAULT_INDENT_LENGTH, 1, 16, 1);
    indentLengthSpinner.setPreferredSize(new Dimension(100, 30));
    indentLengthSpinner.setMinimumSize(new Dimension(100, 30));
  }

  @Override
  protected void configureParameters(JPanel parametersPanel) {
    super.configureParameters(parametersPanel);

    GridBag gbc = new GridBag();
    if (isIndentLengthEnabled()) {
      Insets insets = JBInsets.create(3, 3);
      gbc.nextLine();
      parametersPanel.add(new JLabel("Indent"), gbc.next().insets(insets).fillCellNone());
      parametersPanel.add(indentLengthSpinner, gbc.next().insets(insets).fillCellNone());
      parametersPanel.add(
          new Spacer(), gbc.next().insets(insets).fillCellHorizontally().coverLine(2).weightx(1f));
    }
  }

  @Override
  protected void configureListeners() {
    super.configureListeners();
    indentLengthSpinner.addChangeListener(evt -> updateResult());
  }

  @Override
  protected final String format(String rawData) {
    PrettifyConfig prettifyConfig =
        PrettifyConfig.builder().indentLength(indentLengthSpinner.getNumber()).build();
    return format(rawData, prettifyConfig);
  }

  @Override
  public void reset() {
    super.reset();
    indentLengthSpinner.setNumber(DEFAULT_INDENT_LENGTH);
  }

  @Override
  public void persistState() {
    super.persistState();
    indentLength = indentLengthSpinner.getNumber();
  }

  @Override
  public void restoreState() {
    super.restoreState();
    indentLengthSpinner.setNumber(indentLength);
  }

  protected boolean isIndentLengthEnabled() {
    return true;
  }

  protected abstract String format(String rawData, PrettifyConfig prettifyConfig);
}
