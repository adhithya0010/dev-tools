package com.intellij.devtools.uitests.lib.component.utils;

import java.time.Duration;

public enum Durations {
  THREE_SECONDS(Duration.ofSeconds(3)),
  FIVE_SECONDS(Duration.ofSeconds(5)),
  TEN_SECONDS(Duration.ofSeconds(10)),
  DEFAULT(Duration.ofSeconds(5));
  private final java.time.Duration duration;

  Durations(java.time.Duration duration) {
    this.duration = duration;
  }

  public Duration getDuration() {
    return duration;
  }
}
