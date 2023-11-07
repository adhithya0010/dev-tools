package com.intellij.devtools.component.toolwindow;

import com.intellij.devtools.exec.OperationFactory;
import com.intellij.devtools.exec.misc.web.MockServer;
import com.intellij.devtools.utils.GridConstraintUtils;
import com.intellij.devtools.utils.ProjectUtils;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.uiDesigner.core.GridConstraints;
import java.awt.GridLayout;

public class DevToolsPanel extends JBPanel<DevToolsPanel> {

  public DevToolsPanel(Project project) {
    ProjectUtils.setProject(project);
    createComponents();
  }

  private void createComponents() {
    JBTabbedPane tabbedPane = new JBTabbedPane();

    tabbedPane.addTab(
        "Formatter", new DropdownWrappedPanel(OperationFactory.getInstance().getAllFormatters()));
    tabbedPane.addTab(
        "Converter", new DropdownWrappedPanel(OperationFactory.getInstance().getAllConverters()));
    tabbedPane.addTab(
        "Generator", new DropdownWrappedPanel(OperationFactory.getInstance().getAllGenerators()));
    tabbedPane.addTab(
        "Text", new DropdownWrappedPanel(OperationFactory.getInstance().getAllTextOperations()));
    tabbedPane.addTab(
        "Time", new DropdownWrappedPanel(OperationFactory.getInstance().getAllTimeOperations()));
    tabbedPane.addTab("Mock Server", new MockServer());

    setLayout(new GridLayout(1, 1));
    add(
        tabbedPane,
        GridConstraintUtils.buildGridConstraint(
            0,
            0,
            1,
            1,
            GridConstraints.FILL_BOTH,
            GridConstraintUtils.CAN_SHRINK_AND_GROW,
            GridConstraintUtils.CAN_SHRINK_AND_GROW));
  }
}
