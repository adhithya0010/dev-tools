package com.intellij.devtools.uitests.lib.component;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;

import com.intellij.devtools.utils.ClipboardUtils;
import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.JTextAreaFixture;
import com.intellij.remoterobot.utils.Keyboard;
import java.awt.event.KeyEvent;

public class TextAreaFixture {

  private final JTextAreaFixture textArea;
  private final Keyboard keyboard;

  private TextAreaFixture(RemoteRobot remoteRobot, String name) {
    this.textArea =
        remoteRobot.find(JTextAreaFixture.class, byXpath(String.format("//div[@name='%s']", name)));
    this.keyboard = new Keyboard(remoteRobot);
  }

  public void inputTextViaKeyboard(String text) {
    textArea.click();
    String[] lines = text.split("\n");

    for (String line : lines) {
      keyboard.enterText(line, 0);
      keyboard.enter();
    }
  }

  public void inputTextViaPasteShortcut(String text) {
    textArea.click();
    ClipboardUtils.copy(text);
    keyboard.hotKey(KeyEvent.VK_META, KeyEvent.VK_V);
  }

  public String getText() {
    return textArea.getText();
  }

  public String getTextViaCopyShortcut() {
    textArea.click();
    keyboard.hotKey(KeyEvent.VK_META, KeyEvent.VK_A);
    keyboard.hotKey(KeyEvent.VK_META, KeyEvent.VK_C);
    return ClipboardUtils.paste().orElse(null);
  }

  public static TextAreaFixture byName(RemoteRobot remoteRobot, String name) {
    return new TextAreaFixture(remoteRobot, name);
  }
}
