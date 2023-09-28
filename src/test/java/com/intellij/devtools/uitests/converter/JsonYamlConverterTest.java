package com.intellij.devtools.uitests.converter;

import com.intellij.devtools.uitests.lib.steps.ToolPanelSteps;

public class JsonYamlConverterTest extends ConverterBaseTest {

  @Override
  protected void selectOperation() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectJsonToYaml();
  }

  @Override
  protected String getFromDataSmall() {
    return "converter/json-to-yaml/json-valid";
  }

  @Override
  protected String getFromDataLarge() {
    return "converter/json-to-yaml/json-valid-large";
  }

  @Override
  protected String getToDataSmall() {
    return "converter/json-to-yaml/yaml-valid";
  }

  @Override
  protected String getToDataLarge() {
    return "converter/json-to-yaml/yaml-valid-large";
  }
}
