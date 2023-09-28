package com.intellij.devtools.component;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import org.apache.commons.lang3.StringUtils;

public class NumberFilter extends DocumentFilter {

  @Override
  public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
      throws BadLocationException {
    Document doc = fb.getDocument();
    StringBuilder sb = new StringBuilder();
    sb.append(doc.getText(0, doc.getLength()));
    sb.insert(offset, string);

    if (StringUtils.isNumeric(sb)) {
      super.insertString(fb, offset, string, attr);
    }
  }

  @Override
  public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
    Document doc = fb.getDocument();
    StringBuilder sb = new StringBuilder();
    sb.append(doc.getText(0, doc.getLength()));
    sb.delete(offset, offset + length);

    if (StringUtils.isNumeric(sb) || StringUtils.isEmpty(sb)) {
      super.remove(fb, offset, length);
    }
  }

  @Override
  public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
      throws BadLocationException {
    Document doc = fb.getDocument();
    StringBuilder sb = new StringBuilder();
    sb.append(doc.getText(0, doc.getLength()));
    sb.replace(offset, offset + length, text);

    if (StringUtils.isNumeric(sb)) {
      super.replace(fb, offset, length, text, attrs);
    }
  }
}
