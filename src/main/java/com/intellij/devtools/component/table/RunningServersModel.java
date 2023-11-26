package com.intellij.devtools.component.table;

import com.intellij.devtools.exec.HttpRequestConfig;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class RunningServersModel extends DefaultTableModel {

  private final List<HttpRequestConfig> httpRequestConfigs = new ArrayList<>();

  public RunningServersModel() {
    super(
        null,
        new String[] {
          "", "Path", "Port", "Method", "Response code", "Response Headers", "Response Body"
        });
    addTableModelListener(
        e -> {
          RunningServersModel source = (RunningServersModel) e.getSource();
          if (source == null || e.getFirstRow() < 0 || e.getLastRow() < 0 || e.getColumn() < 0) {
            return;
          }
          httpRequestConfigs
              .get(e.getFirstRow())
              .setSelected((boolean) source.getValueAt(e.getFirstRow(), e.getColumn()));
        });
  }

  public void addRow(HttpRequestConfig httpRequestConfig) {
    this.httpRequestConfigs.add(httpRequestConfig);
    addRow(
        new Object[] {
          httpRequestConfig.isSelected(),
          httpRequestConfig.getPath(),
          httpRequestConfig.getPort(),
          httpRequestConfig.getMethod().getValue(),
          httpRequestConfig.getResponseCode(),
          httpRequestConfig.getHeaders(),
          httpRequestConfig.getResponseBody()
        });
  }

  public void removeRow(HttpRequestConfig httpRequestConfigToRemove) {
    for (int i = 0; i < httpRequestConfigs.size(); i++) {
      HttpRequestConfig httpRequestConfig = this.httpRequestConfigs.get(i);
      if (httpRequestConfig.equals(httpRequestConfigToRemove)) {
        this.httpRequestConfigs.remove(httpRequestConfig);
        removeRow(i);
      }
    }
  }

  public void removeSelectedRows() {
    for (int i = 0; i < httpRequestConfigs.size(); i++) {
      HttpRequestConfig httpRequestConfig = this.httpRequestConfigs.get(i);
      if (!httpRequestConfig.isSelected()) {
        continue;
      }
      this.httpRequestConfigs.remove(httpRequestConfig);
      removeRow(i);
    }
  }

  public List<HttpRequestConfig> getSelectedRequestConfigs() {
    List<HttpRequestConfig> selectedRequestConfigs = new ArrayList<>();
    httpRequestConfigs.forEach(
        mockMetadata -> {
          if (mockMetadata.isSelected()) {
            selectedRequestConfigs.add(mockMetadata);
          }
        });
    return selectedRequestConfigs;
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch (columnIndex) {
      case 0:
        return Boolean.class;
      case 2:
        return Integer.class;
      default:
        return String.class;
    }
  }
}
