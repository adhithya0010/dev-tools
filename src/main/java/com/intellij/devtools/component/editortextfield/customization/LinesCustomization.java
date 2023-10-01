package com.intellij.devtools.component.editortextfield.customization;

import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.ui.SimpleEditorCustomization;
import org.jetbrains.annotations.NotNull;

public class LinesCustomization extends SimpleEditorCustomization {
  public static LinesCustomization ENABLED = new LinesCustomization(true);
  public static LinesCustomization DISABLED = new LinesCustomization(false);

  private LinesCustomization(boolean enabled) {
    super(enabled);
  }

  @Override
  public void customize(@NotNull EditorEx editor) {
    if (isEnabled()) {
      editor.getSettings().setAdditionalLinesCount(5);
    }
  }
}
