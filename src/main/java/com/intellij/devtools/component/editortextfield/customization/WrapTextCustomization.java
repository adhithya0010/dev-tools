package com.intellij.devtools.component.editortextfield.customization;

import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.ui.SimpleEditorCustomization;
import org.jetbrains.annotations.NotNull;

public class WrapTextCustomization extends SimpleEditorCustomization {
  public static final WrapTextCustomization ENABLED = new WrapTextCustomization(true);
  public static final WrapTextCustomization DISABLED = new WrapTextCustomization(false);

  private WrapTextCustomization(boolean enabled) {
    super(enabled);
  }

  @Override
  public void customize(@NotNull EditorEx editor) {
    editor.getSettings().setUseSoftWraps(isEnabled());
  }
}
