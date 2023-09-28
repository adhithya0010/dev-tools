package com.intellij.devtools.exec;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParameterGroup {
  private List<Parameter> parameters;
}
