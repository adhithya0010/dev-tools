package com.intellij.devtools.component.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI.Borders;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public class DevToolsWindowFactory implements ToolWindowFactory {

  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    ContentManager contentManager = toolWindow.getContentManager();
    contentManager.addContent(createContent(contentManager));
  }

  private Content createContent(ContentManager contentManager) {
    JPanel rootPanel = new JPanel(new GridLayoutManager(1, 1));

    GridConstraints gc = new GridConstraints();
    gc.setFill(GridConstraints.FILL_BOTH);
    gc.setRow(0);
    gc.setColumn(0);
    rootPanel.add(new DevToolsPanel(), gc);
    rootPanel.setBorder(Borders.empty(10));

    return contentManager.getFactory().createContent(rootPanel, null, false);
  }
}
