package com.intellij.devtools.uitests.converter;

import com.intellij.devtools.uitests.lib.RemoteRobotExtension;
import com.intellij.devtools.uitests.lib.steps.ConverterSteps;
import com.intellij.devtools.uitests.lib.steps.ToolPanelSteps;
import com.intellij.devtools.uitests.lib.utils.FileUtils;
import com.intellij.remoterobot.RemoteRobot;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RemoteRobotExtension.class)
public abstract class ConverterBaseTest {

  private RemoteRobot remoteRobot;
  private ConverterSteps converterSteps;

  protected abstract void selectOperation();

  protected abstract String getFromDataSmall();

  protected abstract String getFromDataLarge();

  protected abstract String getToDataSmall();

  protected abstract String getToDataLarge();

  @BeforeAll
  public static void initialize(RemoteRobot remoteRobot) {
    //    ProjectSteps.getInstance(remoteRobot).createNewJavaProject();
    //    ToolPanelSteps.getInstance(remoteRobot).openDevToolsPanel();
  }

  @BeforeEach
  public void setupCase(RemoteRobot remoteRobot) {
    this.remoteRobot = remoteRobot;
    this.converterSteps = ConverterSteps.getInstance(remoteRobot);
    selectOperation();
    ToolPanelSteps.getInstance(getRemoteRobot()).clickResetButtonIfPresent();
  }

  @AfterAll
  public static void cleanUp(RemoteRobot remoteRobot) {
    //    ProjectSteps.getInstance(remoteRobot).closeProject();
  }

  public RemoteRobot getRemoteRobot() {
    return remoteRobot;
  }

  @Test
  public void testConvertFromToToSuccessByTyping() {
    converterSteps.inputFromTextByKeyboard(FileUtils.getData(getFromDataSmall()));
    Assertions.assertEquals(
        FileUtils.getData(getToDataSmall()), converterSteps.getToTextUsingObject());
  }

  @Test
  public void testConvertFromToToSuccessByShortcuts() {
    converterSteps.inputFromTextByShortcut(FileUtils.getData(getFromDataLarge()));
    Assertions.assertEquals(
        FileUtils.getData(getToDataLarge()), converterSteps.getToTextByShortcut());
  }

  @Test
  public void testConvertFromToToSuccessByButtons() {
    converterSteps.inputFromTextByButton(FileUtils.getData(getFromDataSmall()));
    Assertions.assertEquals(
        FileUtils.getData(getToDataSmall()), converterSteps.getToTextByButton());
  }

  @Test
  public void testConvertToToFromSuccessByTyping() {
    converterSteps.inputToTextByKeyboard(FileUtils.getData(getToDataSmall()));
    Assertions.assertEquals(
        FileUtils.getData(getFromDataSmall()), converterSteps.getFromTextUsingObject());
  }

  @Test
  public void testConvertToToFromSuccessByShortcuts() {
    converterSteps.inputToTextByShortcut(FileUtils.getData(getToDataLarge()));
    Assertions.assertEquals(
        FileUtils.getData(getFromDataLarge()), converterSteps.getFromTextByShortcut());
  }

  @Test
  public void testConvertToToFromSuccessByButtons() {
    converterSteps.inputToTextByButton(FileUtils.getData(getToDataSmall()));
    Assertions.assertEquals(
        FileUtils.getData(getFromDataSmall()), converterSteps.getFromTextByButton());
  }
}
