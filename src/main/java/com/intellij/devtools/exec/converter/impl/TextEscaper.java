package com.intellij.devtools.exec.converter.impl;

import static com.intellij.devtools.MessageKeys.CONVERTER_TEXT_ESCAPER_NAME;

import com.intellij.devtools.exec.converter.Converter;
import com.intellij.devtools.locale.MessageBundle;
import com.intellij.devtools.utils.TextUtils;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBInsets;
import java.awt.Insets;
import javax.swing.JPanel;

public class TextEscaper extends Converter {

  private ComboBox<String> languageComboBox;

  @Override
  public String getNodeName() {
    return MessageBundle.get(CONVERTER_TEXT_ESCAPER_NAME);
  }

  @Override
  protected String convertTo(String data) {
    String type = (String) languageComboBox.getSelectedItem();
    return TextUtils.unescapeText(data, type);
  }

  @Override
  protected String convertFrom(String data) {
    String type = (String) languageComboBox.getSelectedItem();
    return TextUtils.escapeText(data, type);
  }

  @Override
  protected String getFromLabel() {
    return "Escaped Text";
  }

  @Override
  protected String getToLabel() {
    return "Unescaped Text";
  }

  @Override
  protected void configureParameters(JPanel parametersPanel) {
    super.configureParameters(parametersPanel);
    this.languageComboBox =
        new ComboBox<>(new String[] {"Java", "HTML", "CSV", "XML", "Javascript"});
    languageComboBox.setSelectedItem("Java");

    GridBag gbc = new GridBag();
    Insets insets = JBInsets.create(3, 3);
    gbc.nextLine();
    parametersPanel.add(languageComboBox, gbc.next().insets(insets).fillCellNone());
    parametersPanel.add(
        new Spacer(), gbc.next().insets(insets).fillCellHorizontally().coverLine(2).weightx(1f));
  }

  @Override
  protected Language getFromLanguage() {
    return PlainTextLanguage.INSTANCE;
  }

  @Override
  protected Language getToLanguage() {
    return PlainTextLanguage.INSTANCE;
  }
}
