package com.intellij.devtools.utils;


import com.intellij.diff.DiffContentFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;


public class TextUtils {

  private TextUtils() {
  }

  private static final Map<String, EscapeUnescape> ESCAPER = new HashMap<>();

  static {
    ESCAPER.put("csv", new EscapeUnescape(StringEscapeUtils::unescapeCsv, StringEscapeUtils::escapeCsv));
    ESCAPER.put("html", new EscapeUnescape(StringEscapeUtils::escapeHtml4, StringEscapeUtils::unescapeHtml4));
    ESCAPER.put("java", new EscapeUnescape(StringEscapeUtils::escapeJava, StringEscapeUtils::unescapeJava));
    ESCAPER.put("javascript", new EscapeUnescape(StringEscapeUtils::escapeEcmaScript, StringEscapeUtils::unescapeEcmaScript));
    ESCAPER.put("xml", new EscapeUnescape(StringEscapeUtils::escapeXml11, StringEscapeUtils::unescapeXml));
  }

  public static String escapeText(String data, String type) {
    return ESCAPER.get(StringUtils.lowerCase(type)).escape(data);
  }

  public static String unescapeText(String data, String type) {
    return ESCAPER.get(StringUtils.lowerCase(type)).unescape(data);
  }

  public static String sortLines(String data) {
    return Arrays.stream(data.split("\n"))
            .sorted()
            .collect(Collectors.joining("\n"));
  }

  public static String removeDuplicates(String data) {
    return Arrays.stream(data.split("\n"))
            .distinct()
            .collect(Collectors.joining("\n"));
  }

  public static String findDifference(String original, String changed) {
    return StringUtils.difference(original, changed);
  }

  static class EscapeUnescape {
    private final UnaryOperator<String> escaper;
    private final UnaryOperator<String> unescaper;

    public EscapeUnescape(UnaryOperator<String> escaper, UnaryOperator<String> unescaper) {
      this.escaper = escaper;
      this.unescaper = unescaper;
    }

    public String escape(String data) {
      return escaper.apply(data);
    }

    public String unescape(String data) {
      return unescaper.apply(data);
    }
  }
}
