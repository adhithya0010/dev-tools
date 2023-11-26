package com.intellij.devtools.exec;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class HttpRequestConfig {
  private final String id = UUID.randomUUID().toString();
  private String path;
  private int port;
  private int responseCode;
  private HttpMethod method;
  private Map<String, List<String>> headers;
  private String responseBody;

  @Setter private boolean isSelected;

  public HttpRequestConfig(
      String path,
      int port,
      int responseCode,
      HttpMethod method,
      Map<String, List<String>> headers,
      String responseBody,
      boolean isSelected) {
    this.path = path;
    this.port = port;
    this.responseCode = responseCode;
    this.method = method;
    this.headers = headers;
    this.responseBody = responseBody;
    this.isSelected = isSelected;
  }

  @Override
  public String toString() {
    return "ServerMeta{" + "id='" + id + '\'' + ", path='" + path + '\'' + ", port=" + port + '}';
  }
}
