package com.intellij.devtools.component.editortextfield.customization;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.actions.IncrementalFindAction;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.psi.PsiFile;
import com.intellij.ui.SimpleEditorCustomization;
import com.intellij.util.ui.UIUtil;
import java.util.function.BiConsumer;
import javax.swing.BorderFactory;
import org.jetbrains.annotations.NotNull;

public class RegexEditorCustomization extends SimpleEditorCustomization {

  private final BiConsumer<Integer, PsiFile> task;
  private final PsiFile psiFile;
  private final Disposable disposable;

  public RegexEditorCustomization(
      BiConsumer<Integer, PsiFile> task, PsiFile psiFile, Disposable disposable) {
    super(true);
    this.task = task;
    this.psiFile = psiFile;
    this.disposable = disposable;
  }

  @Override
  public void customize(@NotNull EditorEx editor) {
    editor.putUserData(
        org.intellij.lang.regexp.intention.CheckRegExpForm.Keys.CHECK_REG_EXP_EDITOR, Boolean.TRUE);
    editor.putUserData(IncrementalFindAction.SEARCH_DISABLED, Boolean.TRUE);
    editor
        .getCaretModel()
        .addCaretListener(
            new CaretListener() {
              @Override
              public void caretPositionChanged(CaretEvent event) {
                final int offset = editor.logicalPositionToOffset(event.getNewPosition());
                task.accept(offset, psiFile);
              }
            },
            disposable);
    setupBorder(editor);
  }

  protected void setupBorder(@NotNull EditorEx editor) {
    editor.setBorder(
        BorderFactory.createCompoundBorder(
            UIUtil.getTextFieldBorder(), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
  }
}
