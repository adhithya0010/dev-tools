package com.intellij.devtools.utils;

import graphql.language.AstPrinter;
import graphql.language.Document;
import graphql.language.PrettyAstPrinter;
import graphql.parser.Parser;
import org.apache.commons.lang3.StringUtils;

public class GraphqlUtils {

    public static String minify(String data) {
        if(StringUtils.isEmpty(data)) {
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

    public static String prettify(String data) {
        if(StringUtils.isEmpty(data)) {
            return null;
        }
        try {
            Parser parser = new Parser();
            Document document = parser.parseDocument(data);
            return PrettyAstPrinter.printAst(document);
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}
