package com.intellij.devtools.uitests.lib.steps;

import static com.intellij.devtools.uitests.lib.component.utils.Durations.THREE_SECONDS;
import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static com.intellij.remoterobot.stepsProcessing.StepWorkerKt.step;

import com.intellij.devtools.uitests.lib.component.StripeButtonFixture;
import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.ComboBoxFixture;
import com.intellij.remoterobot.fixtures.ComponentFixture;
import com.intellij.remoterobot.fixtures.JListFixture;
import com.intellij.remoterobot.utils.Keyboard;

public class ToolPanelSteps {

  private final RemoteRobot remoteRobot;
  private final Keyboard keyboard;

  private ToolPanelSteps(RemoteRobot remoteRobot) {
    this.remoteRobot = remoteRobot;
    this.keyboard = new Keyboard(remoteRobot);
  }

  private static ToolPanelSteps INSTANCE = null;

  public static ToolPanelSteps getInstance(RemoteRobot remoteRobot) {
    if (INSTANCE == null) {
      INSTANCE = new ToolPanelSteps(remoteRobot);
    }
    return INSTANCE;
  }

  public void openDevToolsPanel() {
    step(
        "Open Dev Tools Panel",
        () -> {
          ComponentFixture stripeButtonFixture =
              remoteRobot.find(
                  ComponentFixture.class,
                  StripeButtonFixture.byTitle("Dev Tools"),
                  THREE_SECONDS.getDuration());
          stripeButtonFixture.click();
        });
  }

  public void clickCommandComboBox() {
    step(
        "click command combo box",
        () -> {
          ComboBoxFixture comboBoxFixture =
              remoteRobot.find(
                  ComboBoxFixture.class,
                  byXpath("//div[@class='TreeComboBox']"),
                  THREE_SECONDS.getDuration());
          comboBoxFixture.click();
        });
  }

  public void clickResetButtonIfPresent() {
    step(
        "click reset button if present",
        () -> {
          StripeButtonFixture resetButtonFixture =
              remoteRobot.find(
                  StripeButtonFixture.class,
                  StripeButtonFixture.byTitle("Reset"),
                  THREE_SECONDS.getDuration());
          resetButtonFixture.click();
        });
  }

  public void selectJsonPrettify() {
    clickCommandComboBox();
    selectCommand(2);
  }

  public void selectJsonMinify() {
    clickCommandComboBox();
    selectCommand(3);
  }

  public void selectXmlPrettify() {
    clickCommandComboBox();
    selectCommand(5);
  }

  public void selectXmlMinify() {
    clickCommandComboBox();
    selectCommand(6);
  }

  public void selectJsonToProperties() {
    clickCommandComboBox();
    selectCommand(9);
  }

  public void selectJsonToYaml() {
    clickCommandComboBox();
    selectCommand(10);
  }

  public void selectYamlToProperties() {
    clickCommandComboBox();
    selectCommand(11);
  }

  public void selectBase64Encoder() {
    clickCommandComboBox();
    selectCommand(15);
  }

  public void selectUrlEncoder() {
    clickCommandComboBox();
    selectCommand(17);
  }

  public void selectUUIDGenerator() {
    clickCommandComboBox();
    selectCommand(20);
  }

  private void selectCommand(int index) {
    step(
        String.format("Select command at index %d", index),
        () -> {
          JListFixture jListFixture =
              remoteRobot.find(JListFixture.class, byXpath("//div[@class='JList']"));
          jListFixture.clickItemAtIndex(index);
        });
  }
}
