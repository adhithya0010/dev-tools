package com.intellij.devtools.uitests.lib.component;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.data.RemoteComponent;
import com.intellij.remoterobot.fixtures.CommonContainerFixture;
import com.intellij.remoterobot.fixtures.DefaultXpath;
import com.intellij.remoterobot.fixtures.FixtureName;
import com.intellij.remoterobot.fixtures.JMenuBarFixture;
import com.intellij.remoterobot.search.locators.Locator;
import org.jetbrains.annotations.NotNull;

@FixtureName(name = "Idea frame")
@DefaultXpath(by = "IdeFrameImpl type", xpath = "//div[@class='IdeFrameImpl']")
public class IdeaFrame extends CommonContainerFixture {

  private RemoteRobot remoteRobot;

  public IdeaFrame(@NotNull RemoteRobot remoteRobot,
      @NotNull RemoteComponent remoteComponent) {
    super(remoteRobot, remoteComponent);
    this.remoteRobot = remoteRobot;
  }

  Locator getProjectViewTree() {
    return byXpath("ProjectViewTree", "//div[@class='ProjectViewTree']");
  }

  String getProjectName() {
    return callJs("component.getProject().getName()");
  }

  JMenuBarFixture getMenuBar() {
    return remoteRobot.find(JMenuBarFixture.class, JMenuBarFixture.byType());
  }
}