package com.intellij.devtools.uitests.lib.component.utils;

import static com.intellij.devtools.uitests.lib.component.utils.Durations.DEFAULT;
import static com.intellij.devtools.uitests.lib.component.utils.Durations.THREE_SECONDS;

import java.time.Duration;
import java.util.Optional;

public enum Actions {
  NEW_PROJECT_LINK("New Project", "//div[(@class='MainButton' and @text='New Project') or (@accessiblename='New Project' and @class='JButton')]", THREE_SECONDS),
  CREATE_PROJECT_LINK("Create", "//div[@text.key='button.create']", THREE_SECONDS),
  ;

  private final String description;
  private final String xPath;
  private final Durations durations;

  Actions(String description, String xPath, Durations durations) {
    this.description = description;
    this.xPath = xPath;
    this.durations = durations;
  }

  public String getDescription() {
    return description;
  }

  public String getXPath() {
    return xPath;
  }

  public Duration getDuration() {
    return Optional.ofNullable(durations).orElse(DEFAULT).getDuration();
  }
}
