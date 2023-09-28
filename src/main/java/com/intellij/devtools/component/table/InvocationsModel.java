package com.intellij.devtools.component.table;

import com.intellij.devtools.exec.Invocation;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class InvocationsModel extends DefaultTableModel {

  private final List<Invocation> invocations = new ArrayList<>();

  public InvocationsModel() {
    super(null, new String[]{"Time", "Path", "Port", "Method", "Request headers", "Request body",
        "Response code", "Response headers", "Response body"});
  }

  public void addRow(Invocation invocation) {
    this.invocations.add(invocation);
    addRow(new Object[]{invocation.time, invocation.path, invocation.port,
        invocation.httpMethod.getValue(), invocation.responseHeaders, invocation.responseBody,
        invocation.responseCode, invocation.responseHeaders, invocation.responseBody});
  }

  public void clear() {
    for (int i = invocations.size() - 1; i >=0; i--) {
      Invocation invocation = this.invocations.get(i);
      this.invocations.remove(invocation);
      removeRow(i);
    }
  }
}
