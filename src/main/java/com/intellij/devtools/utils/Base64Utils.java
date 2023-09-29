package com.intellij.devtools.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class Base64Utils {

  private Base64Utils() {}

  public static String encode(String data) {
    if (Objects.nonNull(data)) {
      try {
        return new String(
            Base64.getEncoder().encode(data.getBytes(StandardCharsets.UTF_8)),
            StandardCharsets.UTF_8);
      } catch (Exception e) {
        e.printStackTrace();
        return "ERROR";
      }
    }
    return null;
  }

  public static String decode(String data) {
    if (Objects.nonNull(data)) {
      try {
        return new String(Base64.getDecoder().decode(data), StandardCharsets.UTF_8);
      } catch (Exception e) {
        e.printStackTrace();
        return "ERROR";
      }
    }
    return null;
  }
}
