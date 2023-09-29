package com.intellij.devtools.uitests.lib.steps;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static com.intellij.remoterobot.stepsProcessing.StepWorkerKt.step;

import com.intellij.devtools.uitests.lib.component.TextAreaFixture;
import com.intellij.devtools.utils.ClipboardUtils;
import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.JButtonFixture;

public class FormatterSteps {

  private final RemoteRobot remoteRobot;

  private FormatterSteps(RemoteRobot remoteRobot) {
    this.remoteRobot = remoteRobot;
  }

  private static FormatterSteps INSTANCE = null;

  public static FormatterSteps getInstance(RemoteRobot remoteRobot) {
    if (INSTANCE == null) {
      INSTANCE = new FormatterSteps(remoteRobot);
    }
    return INSTANCE;
  }

  public void inputDataText(String text) {
    step(
        "input text via keyboard",
        () -> {
          TextAreaFixture textArea = getDataTextArea();
          textArea.inputTextViaKeyboard(text);
        });
  }

  public void inputDataTextViaPasteKeyboardShortcut(String text) {
    step(
        "input text via keyboard",
        () -> {
          TextAreaFixture textArea = getDataTextArea();
          textArea.inputTextViaPasteShortcut(text);
        });
  }

  public void inputDataTextViaPasteButton(String text) {
    step(
        "input text via paste button",
        () -> {
          ClipboardUtils.copy(text);
          getPasteButton().click();
        });
  }

  public String getResultText() {
    TextAreaFixture textArea = getResultTextArea();
    return textArea.getText();
  }

  public String getResultTextByCopyButton() {
    getCopyButton().click();
    return ClipboardUtils.paste().orElse(null);
  }

  public String getResultTextByCopyKeyboardShortcut() {
    TextAreaFixture textArea = getResultTextArea();
    return textArea.getTextViaCopyShortcut();
  }

  private TextAreaFixture getDataTextArea() {
    return TextAreaFixture.byName(remoteRobot, "data-text-area");
  }

  private TextAreaFixture getResultTextArea() {
    return TextAreaFixture.byName(remoteRobot, "result-text-area");
  }

  private JButtonFixture getPasteButton() {
    return remoteRobot.find(
        JButtonFixture.class, byXpath(String.format("//div[@text='%s']", "Paste")));
  }

  private JButtonFixture getCopyButton() {
    return remoteRobot.find(
        JButtonFixture.class, byXpath(String.format("//div[@text='%s']", "Copy")));
  }
}
