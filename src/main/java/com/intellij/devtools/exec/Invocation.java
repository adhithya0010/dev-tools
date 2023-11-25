package com.intellij.devtools.exec;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public class Invocation {
  public final Instant time;
  public final HttpMethod httpMethod;
  public final String path;
  public final Integer port;
  public final String requestHeaders;
  public final String requestBody;
  public final Integer responseCode;
  public final Map<String, List<String>> responseHeaders;
  public final String responseBody;
}
