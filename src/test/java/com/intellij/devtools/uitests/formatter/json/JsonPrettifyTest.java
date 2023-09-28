package com.intellij.devtools.uitests.formatter.json;

import com.intellij.devtools.uitests.lib.BaseUITest;
import com.intellij.devtools.uitests.lib.steps.FormatterSteps;
import com.intellij.devtools.uitests.lib.steps.ToolPanelSteps;
import com.intellij.devtools.uitests.lib.utils.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonPrettifyTest extends BaseUITest {

  public static final String MINIFIED_SMALL_VALID = "formatter/json/minified-small-valid";
  public static final String PRETTIFIED_SMALL_VALID = "formatter/json/prettified-small-valid";
  public static final String INVALID = "formatter/json/invalid";

  @Test
  public void testJsonPrettifySuccess() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectJsonPrettify();
    ToolPanelSteps.getInstance(getRemoteRobot()).clickResetButtonIfPresent();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataText(FileUtils.getData(MINIFIED_SMALL_VALID));
    Assertions.assertEquals(FileUtils.getData(PRETTIFIED_SMALL_VALID), formatterSteps.getResultText());
  }

  @Test
  public void testJsonPrettifyViaButtonsSuccess() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectJsonPrettify();
    ToolPanelSteps.getInstance(getRemoteRobot()).clickResetButtonIfPresent();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataTextViaPasteButton(FileUtils.getData(MINIFIED_SMALL_VALID));
    Assertions.assertEquals(FileUtils.getData(PRETTIFIED_SMALL_VALID), formatterSteps.getResultTextByCopyButton());
  }

  @Test
  public void testJsonPrettifyViaShortcutsSuccess() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectJsonPrettify();
    ToolPanelSteps.getInstance(getRemoteRobot()).clickResetButtonIfPresent();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataTextViaPasteKeyboardShortcut(FileUtils.getData(MINIFIED_SMALL_VALID));
    Assertions.assertEquals(FileUtils.getData(PRETTIFIED_SMALL_VALID), formatterSteps.getResultTextByCopyKeyboardShortcut());
  }

  @Test
  public void testJsonPrettifyInvalidJson() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectJsonPrettify();
    ToolPanelSteps.getInstance(getRemoteRobot()).clickResetButtonIfPresent();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataText(FileUtils.getData(INVALID));
    Assertions.assertEquals("ERROR", formatterSteps.getResultText());
  }
}