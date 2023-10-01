package com.intellij.devtools.component.editortextfield.customization;

import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.ui.SimpleEditorCustomization;
import org.jetbrains.annotations.NotNull;

public class ReadOnlyCustomization extends SimpleEditorCustomization {

  public static ReadOnlyCustomization ENABLED = new ReadOnlyCustomization(true);
  public static ReadOnlyCustomization DISABLED = new ReadOnlyCustomization(false);

  private ReadOnlyCustomization(boolean enabled) {
    super(enabled);
  }

  @Override
  public void customize(@NotNull EditorEx editor) {
    editor.setViewer(isEnabled());
  }
}
