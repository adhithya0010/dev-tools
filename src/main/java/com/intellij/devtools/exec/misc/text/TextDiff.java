package com.intellij.devtools.exec.misc.text;

import static com.intellij.devtools.utils.GridConstraintUtils.buildGridConstraint;
import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;

import com.intellij.devtools.exec.Operation;
import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManagerEx;
import com.intellij.diff.DiffRequestPanel;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.uiDesigner.core.GridLayoutManager;

public class TextDiff extends Operation {

  private DiffRequestPanel diffRequestPanel = null;

  @Override
  protected void configureComponents() {
    Document document1 = EditorFactory.getInstance().createDocument("");
    Document document2 = EditorFactory.getInstance().createDocument("");

    DiffContent diffContent1 = DiffContentFactory.getInstance().create(null, document1);
    DiffContent diffContent2 = DiffContentFactory.getInstance().create(null, document2);

    diffRequestPanel = DiffManagerEx.getInstance().createRequestPanel(null, () -> {}, null);
    diffRequestPanel.setRequest(
        new SimpleDiffRequest(null, diffContent1, diffContent2, "Original", "Changed"));
  }

  public TextDiff() {
    configureComponents();
    configureParameters(parametersPanel);
    configureLayouts();
    configureListeners();
  }

  @Override
  protected void configureLayouts() {
    setLayout(new GridLayoutManager(1, 1));
    this.add(diffRequestPanel.getComponent(), buildGridConstraint(0, 0, FILL_BOTH));
  }

  @Override
  public String getNodeName() {
    return "Diff";
  }
}
