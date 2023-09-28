package com.intellij.devtools.uitests.formatter;

import com.intellij.devtools.uitests.lib.BaseUITest;
import com.intellij.devtools.uitests.lib.steps.FormatterSteps;
import com.intellij.devtools.uitests.lib.steps.ToolPanelSteps;
import com.intellij.devtools.uitests.lib.utils.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class FormatterBaseTest extends BaseUITest {

  protected abstract void selectOperation();

  protected abstract String getPrettifiedSmallValidPath();
  protected abstract String getMinifiedSmallValidPath();
  protected abstract String getPrettifiedLargeValidPath();
  protected abstract String getMinifiedLargeValidPath();
  protected abstract String getInvalidPath();

  @Test
  public void testMinifySuccess() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectJsonMinify();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataText(FileUtils.getData(getPrettifiedSmallValidPath()));
    Assertions.assertEquals(FileUtils.getData(getMinifiedSmallValidPath()), formatterSteps.getResultText());
  }

  @Test
  public void testMinifyViaKeyboardShortcutsSuccess() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectJsonMinify();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataTextViaPasteKeyboardShortcut(FileUtils.getData(getPrettifiedSmallValidPath()));
    Assertions.assertEquals(FileUtils.getData(getMinifiedSmallValidPath()), formatterSteps.getResultTextByCopyKeyboardShortcut());
  }

  @Test
  public void testMinifyViaButtonsSuccess() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectJsonMinify();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataTextViaPasteButton(FileUtils.getData(getPrettifiedLargeValidPath()));
    Assertions.assertEquals(FileUtils.getData(getMinifiedLargeValidPath()), formatterSteps.getResultTextByCopyButton());
  }

  @Test
  public void testMinifyInvalidJson() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectJsonMinify();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataText(FileUtils.getData(getInvalidPath()));
    Assertions.assertEquals("ERROR", formatterSteps.getResultText());
  }

}
