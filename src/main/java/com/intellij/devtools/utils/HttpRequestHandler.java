package com.intellij.devtools.utils;

import com.intellij.devtools.component.table.InvocationsModel;
import com.intellij.devtools.exec.HttpMethod;
import com.intellij.devtools.exec.HttpRequestConfig;
import com.intellij.devtools.exec.Invocation;
import com.intellij.devtools.exec.Invocation.InvocationBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.StringJoiner;

public class HttpRequestHandler implements HttpHandler {

  private static final String OPERATION_NOT_SUPPORTED = "Unsupported Operation";

  private final InvocationsModel invocationsModel;
  private final HttpRequestConfig httpRequestConfig;

  public HttpRequestHandler(
      InvocationsModel invocationsModel, HttpRequestConfig httpRequestConfig) {
    this.invocationsModel = invocationsModel;
    this.httpRequestConfig = httpRequestConfig;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {

    InvocationBuilder invocationBuilder =
        Invocation.builder()
            .time(Instant.now())
            .path(exchange.getRequestURI().getPath())
            .httpMethod(HttpMethod.fromString(exchange.getRequestMethod()))
            .requestHeaders(toString(exchange.getRequestHeaders()));

    if (!httpRequestConfig.getMethod().getValue().equalsIgnoreCase(exchange.getRequestMethod())) {
      invocationBuilder.responseCode(400).responseBody(OPERATION_NOT_SUPPORTED);
      exchange.sendResponseHeaders(400, OPERATION_NOT_SUPPORTED.length());
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(OPERATION_NOT_SUPPORTED.getBytes(StandardCharsets.UTF_8));
      }
    } else {
      invocationBuilder
          .responseCode(httpRequestConfig.getResponseCode())
          .responseHeaders(httpRequestConfig.getHeaders())
          .responseBody(httpRequestConfig.getResponseBody());
      exchange.getResponseHeaders().putAll(httpRequestConfig.getHeaders());
      exchange.sendResponseHeaders(
          httpRequestConfig.getResponseCode(), httpRequestConfig.getResponseBody().length());
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(httpRequestConfig.getResponseBody().getBytes(StandardCharsets.UTF_8));
      }
    }
    invocationsModel.addRow(invocationBuilder.build());
  }

  private static String toString(Headers headers) {
    StringJoiner sj = new StringJoiner("\n");
    headers.forEach(
        (key, values) -> {
          sj.add(key + ":" + values.get(0));
        });
    return sj.toString();
  }
}
