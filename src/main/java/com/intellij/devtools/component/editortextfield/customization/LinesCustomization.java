package com.intellij.devtools.component.editortextfield.customization;

import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.ui.SimpleEditorCustomization;
import org.jetbrains.annotations.NotNull;

public class LinesCustomization extends SimpleEditorCustomization {
  public static LinesCustomization ENABLED = new LinesCustomization(true);
  private int lines = 5;

  private LinesCustomization(boolean enabled) {
    super(enabled);
  }

  public LinesCustomization(int lines) {
    super(true);
    this.lines = lines;
  }

  @Override
  public void customize(@NotNull EditorEx editor) {
    if (isEnabled()) {
      editor.getSettings().setAdditionalLinesCount(lines);
    }
  }
}
