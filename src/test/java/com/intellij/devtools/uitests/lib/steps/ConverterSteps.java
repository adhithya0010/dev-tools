package com.intellij.devtools.uitests.lib.steps;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static com.intellij.remoterobot.stepsProcessing.StepWorkerKt.step;

import com.intellij.devtools.uitests.lib.component.TextAreaFixture;
import com.intellij.devtools.utils.ClipboardUtils;
import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.JButtonFixture;

public class ConverterSteps {

  private static ConverterSteps INSTANCE = null;
  private final RemoteRobot remoteRobot;

  private ConverterSteps(RemoteRobot remoteRobot) {
    this.remoteRobot = remoteRobot;
  }

  public static ConverterSteps getInstance(RemoteRobot remoteRobot) {
    if (INSTANCE == null) {
      INSTANCE = new ConverterSteps(remoteRobot);
    }
    return INSTANCE;
  }

  public void inputFromTextByKeyboard(String text) {
    step("input from text via keyboard", () -> {
      TextAreaFixture textArea = getTextArea("from-text-area");
      textArea.inputTextViaKeyboard(text);
    });
  }

  public void inputFromTextByShortcut(String text) {
    step("input from text via shortcut", () -> {
      TextAreaFixture textArea = getTextArea("from-text-area");
      textArea.inputTextViaPasteShortcut(text);
    });
  }

  public void inputFromTextByButton(String text) {
    step("input from text via button", () -> {
      ClipboardUtils.copy(text);
      clickFromPasteButton();
    });
  }

  public String getFromTextUsingObject() {
    return getTextArea("from-text-area").getText();
  }

  public String getFromTextByShortcut() {
    TextAreaFixture textArea = getTextArea("from-text-area");
    return textArea.getTextViaCopyShortcut();
  }

  public String getFromTextByButton() {
    clickFromCopyButton();
    return ClipboardUtils.paste().orElse(null);
  }

  public void inputToTextByKeyboard(String text) {
    step("input from text via keyboard", () -> {
      TextAreaFixture textArea = getTextArea("to-text-area");
      textArea.inputTextViaKeyboard(text);
    });
  }

  public void inputToTextByShortcut(String text) {
    step("input from text via shortcut", () -> {
      TextAreaFixture textArea = getTextArea("to-text-area");
      textArea.inputTextViaPasteShortcut(text);
    });
  }

  public void inputToTextByButton(String text) {
    step("input from text via button", () -> {
      ClipboardUtils.copy(text);
      clickToPasteButton();
    });
  }

  public String getToTextUsingObject() {
    return getTextArea("to-text-area").getText();
  }

  public String getToTextByShortcut() {
    TextAreaFixture textArea = getTextArea("to-text-area");
    return textArea.getTextViaCopyShortcut();
  }

  public String getToTextByButton() {
    clickToCopyButton();
    return ClipboardUtils.paste().orElse(null);
  }

  public TextAreaFixture getTextArea(String name) {
    return TextAreaFixture.byName(remoteRobot, name);
  }

  public void clickFromCopyButton() {
    JButtonFixture button = getButtonByName("from-copy-button");
    button.click();
  }

  public void clickFromPasteButton() {
    JButtonFixture button = getButtonByName("from-paste-button");
    button.click();
  }

  public void clickToCopyButton() {
    JButtonFixture button = getButtonByName("to-copy-button");
    button.click();
  }

  public void clickToPasteButton() {
    JButtonFixture button = getButtonByName("to-paste-button");
    button.click();
  }

  private JButtonFixture getButtonByName(String name) {
    return remoteRobot.find(JButtonFixture.class, byXpath(String.format("//div[@name='%s']", name)));
  }
}
