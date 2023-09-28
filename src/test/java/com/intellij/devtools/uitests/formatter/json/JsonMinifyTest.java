package com.intellij.devtools.uitests.formatter.json;

import com.intellij.devtools.uitests.lib.BaseUITest;
import com.intellij.devtools.uitests.lib.steps.FormatterSteps;
import com.intellij.devtools.uitests.lib.steps.ToolPanelSteps;
import com.intellij.devtools.uitests.lib.utils.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonMinifyTest extends BaseUITest {

  public static final String PRETTIFIED_SMALL_VALID = "formatter/json/prettified-small-valid";
  public static final String MINIFIED_SMALL_VALID = "formatter/json/minified-small-valid";
  public static final String INVALID = "formatter/json/invalid";

  @Test
  public void testJsonMinifySuccess() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectJsonMinify();
    ToolPanelSteps.getInstance(getRemoteRobot()).clickResetButtonIfPresent();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataText(FileUtils.getData(PRETTIFIED_SMALL_VALID));
    Assertions.assertEquals(FileUtils.getData(MINIFIED_SMALL_VALID), formatterSteps.getResultText());
  }

  @Test
  public void testJsonMinifyViaKeyboardShortcutsSuccess() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectJsonMinify();
    ToolPanelSteps.getInstance(getRemoteRobot()).clickResetButtonIfPresent();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataTextViaPasteKeyboardShortcut(FileUtils.getData(PRETTIFIED_SMALL_VALID));
    Assertions.assertEquals(FileUtils.getData(MINIFIED_SMALL_VALID), formatterSteps.getResultTextByCopyKeyboardShortcut());
  }

  @Test
  public void testJsonMinifyViaButtonsSuccess() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectJsonMinify();
    ToolPanelSteps.getInstance(getRemoteRobot()).clickResetButtonIfPresent();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataTextViaPasteButton(FileUtils.getData(PRETTIFIED_SMALL_VALID));
    Assertions.assertEquals(FileUtils.getData(MINIFIED_SMALL_VALID), formatterSteps.getResultTextByCopyButton());
  }

  @Test
  public void testJsonMinifyInvalidJson() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectJsonMinify();
    ToolPanelSteps.getInstance(getRemoteRobot()).clickResetButtonIfPresent();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataText(FileUtils.getData(INVALID));
    Assertions.assertEquals("ERROR", formatterSteps.getResultText());
  }
}