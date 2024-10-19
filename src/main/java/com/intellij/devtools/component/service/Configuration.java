package com.intellij.devtools.component.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.Orientation;
import com.intellij.devtools.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Configuration {

  private Map<String, Orientation> orientationState;

  public Configuration() {
    this.orientationState = new HashMap<>();
  }

  public String getOrientationState() {
    try {
      return JsonUtils.OBJECT_MAPPER.writeValueAsString(orientationState);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public void setOrientationState(String state) {
    try {
      orientationState = JsonUtils.OBJECT_MAPPER.readValue(state, new TypeReference<Map<String, Orientation>>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public Orientation getOrientation(Operation operation) {
    return orientationState.getOrDefault(
        operation.getClass().getCanonicalName(), Orientation.HORIZONTAL);
  }

  public void setOrientation(Operation operation, Orientation orientation) {
    orientationState.put(operation.getClass().getCanonicalName(), orientation);
  }
}
