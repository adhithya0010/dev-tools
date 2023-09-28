package com.intellij.devtools.uitests.converter;

import com.intellij.devtools.uitests.lib.steps.ToolPanelSteps;

public class JsonPropertiesConverterTest extends ConverterBaseTest {

  @Override
  protected void selectOperation() {
    ToolPanelSteps.getInstance(getRemoteRobot()).selectJsonToProperties();
  }

  @Override
  protected String getFromDataSmall() {
    return "converter/json-to-properties/json-valid";
  }

  @Override
  protected String getFromDataLarge() {
    return "converter/json-to-properties/json-valid-large";
  }

  @Override
  protected String getToDataSmall() {
    return "converter/json-to-properties/properties-valid";
  }

  @Override
  protected String getToDataLarge() {
    return "converter/json-to-properties/properties-valid-large";
  }
}
