package com.intellij.devtools.exec;

public enum HttpMethod {
  POST("Post"),
  GET("Get")
  ;

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
