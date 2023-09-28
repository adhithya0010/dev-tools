package com.intellij.devtools.utils;

import com.intellij.devtools.exec.HttpMethod;
import com.intellij.devtools.exec.Invocation;
import com.intellij.devtools.exec.Invocation.InvocationBuilder;
import com.intellij.devtools.component.table.InvocationsModel;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import org.apache.commons.lang3.StringUtils;

public class HttpRequestHandler implements HttpHandler {

  private static final String OPERATION_NOT_SUPPORTED = "Unsupported Operation";

  private final InvocationsModel invocationsModel;
  private final HttpMethod method;
  private final int responseCode;
  private final Map<String, List<String>> headers;
  private final String responseHeaders;
  private final String responseBody;

  public HttpRequestHandler(InvocationsModel invocationsModel, HttpMethod method, int responseCode,
      String responseHeaders, String responseBody) {
    this.invocationsModel = invocationsModel;
    this.method = method;
    this.responseCode = responseCode;
    this.headers = toMap(responseHeaders);
    this.responseHeaders = responseHeaders;
    this.responseBody = responseBody;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    InvocationBuilder invocationBuilder = Invocation.builder()
        .time(Instant.now())
        .path(exchange.getRequestURI().getPath())
        .httpMethod(HttpMethod.fromString(exchange.getRequestMethod()))
        .requestHeaders(toString(exchange.getRequestHeaders()));

    if (!method.getValue().equalsIgnoreCase(exchange.getRequestMethod())) {
      invocationBuilder.responseCode(400)
              .responseBody(OPERATION_NOT_SUPPORTED);
      exchange.sendResponseHeaders(400, OPERATION_NOT_SUPPORTED.length());
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(OPERATION_NOT_SUPPORTED.getBytes(StandardCharsets.UTF_8));
      }
    } else {
      invocationBuilder.responseCode(responseCode)
          .responseHeaders(responseHeaders)
          .responseBody(responseBody);
      exchange.getResponseHeaders().putAll(headers);
      exchange.sendResponseHeaders(responseCode, responseBody.length());
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(responseBody.getBytes(StandardCharsets.UTF_8));
      }
    }
    invocationsModel.addRow(invocationBuilder.build());
  }

  private static Map<String, List<String>> toMap(String requestHeaders) {
    if (StringUtils.isEmpty(requestHeaders)) {
      return Map.of();
    }
    String[] headerValues = requestHeaders.split("\n");
    Map<String, List<String>> headersMap = new HashMap<>();
    for (String header : headerValues) {
      String[] parts = header.split(":");
      headersMap.put(parts[0], Collections.singletonList(parts[1]));
    }
    return headersMap;
  }

  private static String toString(Headers headers) {
    StringJoiner sj = new StringJoiner("\n");
    headers.forEach((key, values) -> {
      sj.add(key + ":" + values.get(0));
    });
    return sj.toString();
  }
}
