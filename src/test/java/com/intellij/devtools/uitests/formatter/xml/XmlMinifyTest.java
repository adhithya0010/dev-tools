package com.intellij.devtools.uitests.formatter.xml;

import com.intellij.devtools.uitests.lib.BaseUITest;
import com.intellij.devtools.uitests.lib.steps.FormatterSteps;
import com.intellij.devtools.uitests.lib.steps.ToolPanelSteps;
import com.intellij.devtools.uitests.lib.utils.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XmlMinifyTest extends BaseUITest {

  public static final String PRETTIFIED_SMALL_VALID = "formatter/xml/prettified-small-valid";
  public static final String PRETTIFIED_LARGE_VALID = "formatter/xml/prettified-valid";
  public static final String MINIFIED_SMALL_VALID = "formatter/xml/minified-small-valid";
  public static final String MINIFIED_LARGE_VALID = "formatter/xml/minified-valid";
  public static final String INVALID = "formatter/xml/invalid";

  @Test
  public void testXmlMinifySuccess() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectXmlMinify();
    ToolPanelSteps.getInstance(getRemoteRobot()).clickResetButtonIfPresent();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataText(FileUtils.getData(PRETTIFIED_SMALL_VALID));
    Assertions.assertEquals(
        FileUtils.getData(MINIFIED_SMALL_VALID), formatterSteps.getResultText());
  }

  @Test
  public void testXmlMinifyViaKeyboardShortcutsSuccess() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectXmlMinify();
    ToolPanelSteps.getInstance(getRemoteRobot()).clickResetButtonIfPresent();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataTextViaPasteKeyboardShortcut(FileUtils.getData(PRETTIFIED_SMALL_VALID));
    Assertions.assertEquals(
        FileUtils.getData(MINIFIED_SMALL_VALID),
        formatterSteps.getResultTextByCopyKeyboardShortcut());
  }

  @Test
  public void testXmlMinifyViaButtonsSuccess() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectXmlMinify();
    ToolPanelSteps.getInstance(getRemoteRobot()).clickResetButtonIfPresent();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataTextViaPasteButton(FileUtils.getData(PRETTIFIED_LARGE_VALID));
    Assertions.assertEquals(
        FileUtils.getData(MINIFIED_LARGE_VALID), formatterSteps.getResultTextByCopyButton());
  }

  @Test
  public void testXmlMinifyInvalidJson() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectXmlMinify();
    ToolPanelSteps.getInstance(getRemoteRobot()).clickResetButtonIfPresent();
    FormatterSteps formatterSteps = FormatterSteps.getInstance(getRemoteRobot());
    formatterSteps.inputDataText(FileUtils.getData(INVALID));
    Assertions.assertEquals("ERROR", formatterSteps.getResultText());
  }
}
