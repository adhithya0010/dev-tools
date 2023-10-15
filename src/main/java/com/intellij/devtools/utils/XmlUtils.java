package com.intellij.devtools.utils;

import com.intellij.devtools.exec.PrettifyConfig;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtils {

  private XmlUtils() {}

  private static final Transformer TRANSFORMER;
  private static final DocumentBuilder DOCUMENT_BUILDER;

  static {
    try {
      TRANSFORMER = TransformerFactory.newInstance().newTransformer();
    } catch (TransformerConfigurationException e) {
      throw new RuntimeException(e);
    }
    TRANSFORMER.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    TRANSFORMER.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    TRANSFORMER.setOutputProperty(OutputKeys.INDENT, "yes");

    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      // to be compliant, completely disable DOCTYPE declaration:
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      // or completely disable external entities declarations:
      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      // or prohibit the use of all protocols by external entities:
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      // or disable entity expansion but keep in mind that this doesn't prevent fetching external
      // entities
      // and this solution is not correct for OpenJDK < 13 due to a bug:
      // https://bugs.openjdk.java.net/browse/JDK-8206132
      factory.setExpandEntityReferences(false);
      DOCUMENT_BUILDER = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  public static String minify(String xmlString) {
    if (StringUtils.isEmpty(xmlString)) {
      return null;
    }
    try {
      Document document = toDocument(xmlString);
      StringWriter writer = new StringWriter();
      TRANSFORMER.transform(new DOMSource(document), new StreamResult(writer));
      return writer.getBuffer().toString().replaceAll("\\s*[\\r\\n]+\\s*", "");
    } catch (TransformerException | IOException | SAXException e) {
      e.printStackTrace();
      return "ERROR";
    }
  }

  public static String prettify(String xmlString, PrettifyConfig prettifyConfig) {
    if (StringUtils.isEmpty(xmlString)) {
      return null;
    }
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformerFactory.setAttribute("indent-number", prettifyConfig.getIndentLength());
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      Document document = toDocument(minify(xmlString));
      Writer out = new StringWriter();
      transformer.transform(new DOMSource(document), new StreamResult(out));
      return out.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return "ERROR";
    }
  }

  private static Document toDocument(String xmlString) throws SAXException, IOException {
    InputSource src = new InputSource(new StringReader(xmlString));
    return DOCUMENT_BUILDER.parse(src);
  }
}
