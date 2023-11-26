package com.intellij.devtools.exec.generator.impl;

import static com.intellij.devtools.MessageKeys.GENERATOR_UUID_NAME;

import com.intellij.devtools.exec.generator.Generator;
import com.intellij.devtools.locale.MessageBundle;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBLabel;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBInsets;
import java.awt.GridBagLayout;
import java.util.StringJoiner;
import java.util.UUID;
import javax.swing.JPanel;

public class UUIDGenerator extends Generator {

  private JBIntSpinner countSpinner;

  @Override
  public String getNodeName() {
    return MessageBundle.get(GENERATOR_UUID_NAME);
  }

  @Override
  protected void configureParameters(JPanel parametersPanel) {
    parametersPanel.setLayout(new GridBagLayout());
    JBLabel countLabel = new JBLabel("Count");
    this.countSpinner = new JBIntSpinner(1, 1, 100);
    GridBag gridBag = new GridBag();
    JBInsets insets = JBInsets.create(3, 3);
    gridBag.nextLine();
    parametersPanel.add(countLabel, gridBag.next().insets(insets).fillCellNone());
    parametersPanel.add(countSpinner, gridBag.next().insets(insets).fillCellNone());
    parametersPanel.add(
        new Spacer(), gridBag.next().fillCellHorizontally().coverLine(2).weightx(1f));
  }

  @Override
  protected String generate() {
    int count = countSpinner.getNumber();
    StringJoiner sj = new StringJoiner("\n");
    while (count-- > 0) {
      sj.add(UUID.randomUUID().toString());
    }
    return sj.toString();
  }
}
