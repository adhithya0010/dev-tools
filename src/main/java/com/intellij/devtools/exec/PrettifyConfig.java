package com.intellij.devtools.exec;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrettifyConfig {
  private final int indentLength;
}
