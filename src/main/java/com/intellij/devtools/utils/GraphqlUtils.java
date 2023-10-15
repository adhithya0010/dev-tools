package com.intellij.devtools.utils;

import com.intellij.devtools.exec.PrettifyConfig;
import graphql.language.AstPrinter;
import graphql.language.Document;
import graphql.language.PrettyAstPrinter;
import graphql.parser.Parser;
import org.apache.commons.lang3.StringUtils;

public class GraphqlUtils {

  public static String minify(String data) {
    if (StringUtils.isEmpty(data)) {
      return null;
    }
    try {
      Parser parser = new Parser();
      Document document = parser.parseDocument(data);
      return AstPrinter.printAstCompact(document);
    } catch (Exception e) {
      e.printStackTrace();
      return "ERROR";
    }
  }

  public static String prettify(String data, PrettifyConfig prettifyConfig) {
    if (StringUtils.isEmpty(data)) {
      return null;
    }
    try {
      PrettyAstPrinter.PrettyPrinterOptions printerOptions =
          PrettyAstPrinter.PrettyPrinterOptions.builder()
              .indentType(PrettyAstPrinter.PrettyPrinterOptions.IndentType.SPACE)
              .indentWith(prettifyConfig.getIndentLength())
              .build();
      Parser parser = new Parser();
      Document document = parser.parseDocument(data);
      PrettyAstPrinter.printAst(document);
      return PrettyAstPrinter.print(data, printerOptions);
    } catch (Exception e) {
      e.printStackTrace();
      return "ERROR";
    }
  }
}
