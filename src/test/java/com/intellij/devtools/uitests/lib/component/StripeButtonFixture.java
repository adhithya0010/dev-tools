package com.intellij.devtools.uitests.lib.component;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.data.RemoteComponent;
import com.intellij.remoterobot.fixtures.ComponentFixture;
import com.intellij.remoterobot.fixtures.FixtureName;
import com.intellij.remoterobot.search.locators.Locator;
import org.jetbrains.annotations.NotNull;

@FixtureName(name = "StripeButton")
public class StripeButtonFixture extends ComponentFixture {

  public StripeButtonFixture(
      @NotNull RemoteRobot remoteRobot,
      @NotNull RemoteComponent remoteComponent) {
    super(remoteRobot, remoteComponent);
  }

  // //div[@text='Dev Tools']
  public static Locator byTitle(String title) {
    return byXpath(String.format("title %s", title),
        String.format("//div[@text='%s']", title));
  }

}
