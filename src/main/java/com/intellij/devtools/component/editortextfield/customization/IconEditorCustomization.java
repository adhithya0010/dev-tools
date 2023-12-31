package com.intellij.devtools.component.editortextfield.customization;

import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.ui.SimpleEditorCustomization;
import com.intellij.ui.components.JBScrollBar;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.jetbrains.annotations.NotNull;

public class IconEditorCustomization extends SimpleEditorCustomization {

  private final JLabel icon;

  public IconEditorCustomization(JLabel icon) {
    super(true);
    this.icon = icon;
  }

  @Override
  public void customize(@NotNull EditorEx editor) {
    final JScrollPane scrollPane = editor.getScrollPane();
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    final JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
    verticalScrollBar.setBackground(editor.getBackgroundColor());
    verticalScrollBar.add(JBScrollBar.LEADING, icon);
    verticalScrollBar.setOpaque(true);
  }
}
