package com.intellij.devtools.component.table;

import com.intellij.devtools.exec.HttpMethod;
import java.util.UUID;

public class MockMetadata {

  public String id;
  public String path;
  public int port;
  public HttpMethod httpMethod;
  public String responseCode;
  public String responseHeaders;
  public String responseBody;
  public boolean isSelected;

  public MockMetadata(
      String path,
      int portValue,
      HttpMethod httpMethod,
      String responseCode,
      String responseHeaders,
      String responseBody) {
    this.httpMethod = httpMethod;
    this.responseCode = responseCode;
    this.responseHeaders = responseHeaders;
    this.responseBody = responseBody;
    this.id = UUID.randomUUID().toString();
    this.path = path;
    this.port = portValue;
    this.isSelected = false;
  }

  @Override
  public String toString() {
    return "ServerMeta{" + "id='" + id + '\'' + ", path='" + path + '\'' + ", port=" + port + '}';
  }
}
