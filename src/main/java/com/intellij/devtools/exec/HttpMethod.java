package com.intellij.devtools.exec;

public enum HttpMethod {
  POST("Post"),
  GET("Get"),
  PATCH("Patch"),
  DELETE("Delete"),
  REPLACE("Replace");

  private final String value;

  HttpMethod(String value) {
    this.value = value;
  }

  public static HttpMethod fromString(String requestMethod) {
    return HttpMethod.valueOf(requestMethod);
  }

  public String getValue() {
    return value;
  }
}
