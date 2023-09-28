package com.intellij.devtools.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class TextUtils {

  private TextUtils() {
  }

  private static final Map<String, EscapeUnescape> ESCAPER = new HashMap<>();

  static {
    ESCAPER.put("csv", new EscapeUnescape(StringEscapeUtils::unescapeCsv, StringEscapeUtils::escapeCsv));
    ESCAPER.put("html", new EscapeUnescape(StringEscapeUtils::escapeHtml, StringEscapeUtils::unescapeHtml));
    ESCAPER.put("java", new EscapeUnescape(StringEscapeUtils::escapeJava, StringEscapeUtils::unescapeJava));
    ESCAPER.put("javascript", new EscapeUnescape(StringEscapeUtils::escapeJavaScript, StringEscapeUtils::unescapeJavaScript));
    ESCAPER.put("xml", new EscapeUnescape(StringEscapeUtils::escapeXml, StringEscapeUtils::unescapeXml));
  }

  public static String escapeText(String data, String type) {
    return ESCAPER.get(StringUtils.lowerCase(type)).escape(data);
  }

  public static String unescapeText(String data, String type) {
    return ESCAPER.get(StringUtils.lowerCase(type)).unescape(data);
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
