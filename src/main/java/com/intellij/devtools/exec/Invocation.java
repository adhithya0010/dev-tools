package com.intellij.devtools.exec;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public class Invocation {
  public Instant time;
  public HttpMethod httpMethod;
  public String path;
  public Integer port;
  public String requestHeaders;
  public String requestBody;
  public Integer responseCode;
  public Map<String, List<String>> responseHeaders;
  public String responseBody;
}
