package com.intellij.devtools.component.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class RunningServersModel extends DefaultTableModel {

  private final List<MockMetadata> mockMetadata = new ArrayList<>();

  public RunningServersModel() {
    super(null, new String[]{"", "Path", "Port", "Method", "Response code", "Response Headers", "Response Body"});
    addTableModelListener(e -> {
      RunningServersModel source = (RunningServersModel) e.getSource();
      if(source == null || e.getFirstRow() < 0 || e.getLastRow() < 0 || e.getColumn() < 0) {
        return;
      }
      mockMetadata.get(e.getFirstRow()).isSelected = (boolean) source.getValueAt(e.getFirstRow(), e.getColumn());
    });
  }

  public void addRow(MockMetadata mockMetadata) {
    this.mockMetadata.add(mockMetadata);
    addRow(new Object[]{mockMetadata.isSelected, mockMetadata.path, mockMetadata.port, mockMetadata.httpMethod.getValue(), mockMetadata.responseCode, mockMetadata.responseHeaders, mockMetadata.responseBody});
  }

  public void removeRow(MockMetadata mockMetadataToRemove) {
    for (int i = 0; i < mockMetadata.size(); i++) {
      MockMetadata mockMetadata = this.mockMetadata.get(i);
      if(mockMetadata.equals(mockMetadataToRemove)) {
        this.mockMetadata.remove(mockMetadata);
        removeRow(i);
      }
    }
  }

  public void removeSelectedRows() {
    for (int i = 0; i < mockMetadata.size(); i++) {
      MockMetadata mockMetadata = this.mockMetadata.get(i);
      if(!mockMetadata.isSelected) {
        continue;
      }
      this.mockMetadata.remove(mockMetadata);
      removeRow(i);
    }
  }

  public List<MockMetadata> getSelectedServerMetas() {
    List<MockMetadata> selectedServers = new ArrayList<>();
    mockMetadata.forEach(mockMetadata -> {
      if(mockMetadata.isSelected) {
        selectedServers.add(mockMetadata);
      }
    });
    return selectedServers;
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
