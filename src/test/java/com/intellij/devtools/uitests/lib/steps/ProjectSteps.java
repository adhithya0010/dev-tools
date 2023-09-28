package com.intellij.devtools.uitests.lib.steps;

import static com.intellij.devtools.uitests.lib.component.utils.Durations.FIVE_SECONDS;
import static com.intellij.devtools.uitests.lib.component.utils.Durations.TEN_SECONDS;
import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static com.intellij.remoterobot.stepsProcessing.StepWorkerKt.step;
import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitFor;

import com.intellij.devtools.uitests.lib.component.DialogFixture;
import com.intellij.devtools.uitests.lib.component.WelcomeFrameFixture;
import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.JListFixture;
import com.intellij.remoterobot.utils.Keyboard;
import java.awt.event.KeyEvent;

public class ProjectSteps {

  final private RemoteRobot remoteRobot;
  final private Keyboard keyboard;

  private ProjectSteps(RemoteRobot remoteRobot) {
    this.remoteRobot = remoteRobot;
    this.keyboard = new Keyboard(remoteRobot);
  }

  private static ProjectSteps INSTANCE = null;

  public static ProjectSteps getInstance(RemoteRobot remoteRobot) {
    if(INSTANCE == null) {
      INSTANCE = new ProjectSteps(remoteRobot);
    }
    return INSTANCE;
  }

  public void createNewJavaProject() {
    step("Create New Project", () -> {
      final WelcomeFrameFixture welcomeFrame = remoteRobot.find(WelcomeFrameFixture.class, TEN_SECONDS.getDuration());
      welcomeFrame.createNewProjectLink().click();

      final DialogFixture newProjectDialog = welcomeFrame.find(DialogFixture.class, DialogFixture.byTitle("New Project"), TEN_SECONDS.getDuration());
      newProjectDialog.find(JListFixture.class, byXpath("//div[@class='JBList']")).clickItem("New Project", true);
      newProjectDialog.findText("Java").click();
      newProjectDialog.button("Create").click();
    });
  }

  public void closeTipOfTheDay() {
    step("Close Tip of the Day if it appears", () -> {
      waitFor(TEN_SECONDS.getDuration(), () -> remoteRobot.findAll(DialogFixture.class, byXpath("//div[@class='MyDialog'][.//div[@text='Running startup activities...']]")).size() == 0);
//      final IdeaFrame idea = remoteRobot.find(IdeaFrame.class, ofSeconds(10));
//      idea.dumbAware(() -> {
//        try {
//          idea.find(DialogFixture.class, byTitle("Tip of the Day")).button("Close").click();
//        } catch (Throwable ignore) {
//        }
//        return Unit.INSTANCE;
//      });
    });
  }

  public void openProject() {
    step("Open Project", () -> {
      JListFixture projectListFixture = remoteRobot.find(JListFixture.class,
          byXpath("//div[@class='MyList']"), FIVE_SECONDS.getDuration());
      projectListFixture.clickItemAtIndex(0);
    });
  }

  public void closeProject() {
    step("Close Project", () -> {
      if(remoteRobot.isMac()) {
        keyboard.hotKey(KeyEvent.VK_META, KeyEvent.VK_SHIFT, KeyEvent.VK_A);
        keyboard.enterText("Close Project");
        keyboard.enter();
      } else {
//        menuBar.select("File", "Close Project")
      }
    });
  }
}
