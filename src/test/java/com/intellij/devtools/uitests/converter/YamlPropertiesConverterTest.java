package com.intellij.devtools.uitests.converter;

import com.intellij.devtools.uitests.lib.steps.ToolPanelSteps;

public class YamlPropertiesConverterTest extends ConverterBaseTest {

  @Override
  protected void selectOperation() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectYamlToProperties();
  }

  @Override
  protected String getFromDataSmall() {
    return "converter/yaml-to-properties/yaml-valid";
  }

  @Override
  protected String getFromDataLarge() {
    return "converter/yaml-to-properties/yaml-valid-large";
  }

  @Override
  protected String getToDataSmall() {
    return "converter/yaml-to-properties/properties-valid";
  }

  @Override
  protected String getToDataLarge() {
    return "converter/yaml-to-properties/properties-valid-large";
  }
}
