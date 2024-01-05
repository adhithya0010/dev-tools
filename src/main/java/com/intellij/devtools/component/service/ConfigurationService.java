package com.intellij.devtools.component.service;

import com.intellij.devtools.exec.Operation;
import com.intellij.devtools.exec.Orientation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@Service
@State(
    name = "dev-tools-config",
    storages = {@Storage(value = "dev-tools-config.xml")})
public final class ConfigurationService implements PersistentStateComponent<Configuration> {

  private Configuration configuration = new Configuration();

  public static ConfigurationService getInstance() {
    return ApplicationManager.getApplication().getService(ConfigurationService.class);
  }

  @Override
  public Configuration getState() {
    return configuration;
  }

  @Override
  public void loadState(@NotNull Configuration state) {
      this.configuration = state;
  }

  public Orientation getOrientation(Operation operation) {
    return configuration.getOrientation(operation);
  }

  public void setOrientation(Operation operation, Orientation orientation) {
    configuration.setOrientation(operation, orientation);
  }
}
