package com.intellij.devtools.uitests.lib;

import com.intellij.devtools.uitests.lib.steps.ProjectSteps;
import com.intellij.remoterobot.RemoteRobot;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RemoteRobotExtension.class)
public class BaseUITest {

  private RemoteRobot remoteRobot;

  @BeforeAll
  public static void initializeAll(RemoteRobot remoteRobot) {
    ProjectSteps projectSteps = ProjectSteps.getInstance(remoteRobot);
//    projectSteps.createNewJavaProject();
//    ToolPanelSteps.getInstance(remoteRobot).openDevToolsPanel();
  }

  @AfterAll
  public static void cleanUpTest(RemoteRobot remoteRobot) {
    ProjectSteps projectSteps = ProjectSteps.getInstance(remoteRobot);
//    projectSteps.closeProject();
  }

  @BeforeEach
  public void setupTest(RemoteRobot remoteRobot) {
    this.remoteRobot = remoteRobot;
  }

  public RemoteRobot getRemoteRobot() {
    return remoteRobot;
  }
}
