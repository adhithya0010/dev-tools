package com.intellij.devtools.uitests.lib.component;

import static com.intellij.devtools.uitests.lib.component.utils.Actions.CREATE_PROJECT_LINK;
import static com.intellij.devtools.uitests.lib.component.utils.Actions.NEW_PROJECT_LINK;
import static com.intellij.remoterobot.search.locators.Locators.byXpath;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.data.RemoteComponent;
import com.intellij.remoterobot.fixtures.ActionLinkFixture;
import com.intellij.remoterobot.fixtures.CommonContainerFixture;
import com.intellij.remoterobot.fixtures.DefaultXpath;
import com.intellij.remoterobot.fixtures.FixtureName;
import com.intellij.remoterobot.fixtures.JButtonFixture;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.jetbrains.annotations.NotNull;

@FixtureName(name = "Welcome Frame")
@DefaultXpath(by = "type", xpath = "//div[@class='FlatWelcomeFrame']")
public class WelcomeFrame extends CommonContainerFixture {

  private RemoteRobot remoteRobot;

  public WelcomeFrame(@NotNull RemoteRobot remoteRobot, @NotNull RemoteComponent remoteComponent) {
    super(remoteRobot, remoteComponent);
    this.remoteRobot = remoteRobot;
  }

  public ActionLinkFixture getCreateNewProjectLink() {
    return actionLink(
        byXpath(NEW_PROJECT_LINK.getDescription(), NEW_PROJECT_LINK.getXPath()),
        NEW_PROJECT_LINK.getDuration());
  }

  public JButtonFixture getCreateNewProjectButtonLink() {
    return button(
        byXpath(CREATE_PROJECT_LINK.getDescription(), CREATE_PROJECT_LINK.getXPath()),
        Duration.of(3, ChronoUnit.SECONDS));
  }
}
