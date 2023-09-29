package com.intellij.devtools.exec;

import java.time.Instant;
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
  public String responseHeaders;
  public String responseBody;
}
