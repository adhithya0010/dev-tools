package com.intellij.devtools.utils;

import org.junit.jupiter.api.Test;

public class JsonUtilsTest {

  private static final String DATA =
      """
      {
        "glossary" : {
          "title" : "example glossary",
          "GlossDiv" : {
            "title" : "S",
            "GlossList" : {
              "GlossEntry" : {
                "ID" : "SGML",
                "SortAs" : "SGML",
                "GlossTerm" : "Standard Generalized Markup Language",
                "Acronym" : "SGML",
                "Abbrev" : "ISO 8879:1986",
                "GlossDef" : {
                  "para" : "A meta-markup language, used to create markup languages such as DocBook.",
                  "GlossSeeAlso" : [
                    "GML",
                    "XML"
                  ]
                },
                "GlossSee" : "markup"
              }
            }
          }
        }
      }
      """;

  @Test
  void test() {
    String response = JsonUtils.executeJQ(".", DATA);
    System.out.println(response);
  }
}
