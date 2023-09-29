package com.intellij.devtools.exec;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Parameter {

  private String name;
  private String label;
  private Type type;
  private String defaultValue;
  private List<String> values;
  private long minValue;
  private long maxValue;

  public enum Type {
    TEXT,
    NUMBER,
    SELECT;
  }
}
