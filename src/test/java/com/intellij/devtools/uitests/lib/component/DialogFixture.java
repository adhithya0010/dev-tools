package com.intellij.devtools.uitests.lib.component;

// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import static com.intellij.remoterobot.search.locators.Locators.byXpath;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.data.RemoteComponent;
import com.intellij.remoterobot.fixtures.CommonContainerFixture;
import com.intellij.remoterobot.fixtures.FixtureName;
import com.intellij.remoterobot.search.locators.Locator;

@FixtureName(name = "Dialog")
public class DialogFixture extends CommonContainerFixture {
    RemoteRobot remoteRobot;
    RemoteComponent remoteComponent;

  public DialogFixture(RemoteRobot remoteRobot, RemoteComponent remoteComponent) {
    super(remoteRobot, remoteComponent);
    this.remoteRobot = remoteRobot;
    this.remoteComponent = remoteComponent;
  }

  public static Locator byTitle(String title) {
    return byXpath(String.format("title %s", title),
        String.format("//div[@title='%s' and @class='MyDialog']", title));
  }

  public String getTitle() {
    return remoteRobot.callJs("component.getTitle();");
  }
}