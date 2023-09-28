package com.intellij.devtools.component.listener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentChangeListener implements DocumentListener {

  @Override
  public void insertUpdate(DocumentEvent e) {
    handleChange(e);
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    handleChange(e);
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    handleChange(e);
  }

  public abstract void handleChange(DocumentEvent e);
}
