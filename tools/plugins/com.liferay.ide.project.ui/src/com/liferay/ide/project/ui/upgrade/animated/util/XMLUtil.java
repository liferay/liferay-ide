/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package com.liferay.ide.project.ui.upgrade.animated.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public final class XMLUtil
{
  private XMLUtil()
  {
  }

  public static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException
  {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    documentBuilderFactory.setValidating(false);
    return documentBuilderFactory.newDocumentBuilder();
  }

  public static Document loadDocument(DocumentBuilder documentBuilder, InputStream inputStream) throws SAXException, IOException
  {
    try
    {
      return documentBuilder.parse(inputStream);
    }
    finally
    {
      IOUtil.close(inputStream);
    }
  }

  public static Document loadDocument(DocumentBuilder documentBuilder, File file) throws SAXException, IOException
  {
    return documentBuilder.parse(file);
  }

  public static Element loadRootElement(DocumentBuilder documentBuilder, InputStream inputStream) throws Exception
  {
    Document document = loadDocument(documentBuilder, inputStream);
    return document.getDocumentElement();
  }

  public static Element loadRootElement(DocumentBuilder documentBuilder, File file) throws Exception
  {
    Document document = loadDocument(documentBuilder, file);
    return document.getDocumentElement();
  }

  public static int handleElements(NodeList nodeList, ElementHandler handler) throws Exception
  {
    int count = 0;
    for (int i = 0; i < nodeList.getLength(); i++)
    {
      Node node = nodeList.item(i);
      if (node instanceof Element)
      {
        Element element = (Element)node;
        handler.handleElement(element);
        ++count;
      }
    }

    return count;
  }

  public static int handleChildElements(Element rootElement, ElementHandler handler) throws Exception
  {
    NodeList childNodes = rootElement.getChildNodes();
    return handleElements(childNodes, handler);
  }

  public static int handleElementsByTagName(Element rootElement, String tagName, ElementHandler handler) throws Exception
  {
    int count = 0;

    NodeList nodeList = rootElement.getElementsByTagName(tagName);
    count += handleElements(nodeList, handler);

    int pos = tagName.indexOf(':');
    if (pos != -1)
    {
      tagName = tagName.substring(pos + 1);
      nodeList = rootElement.getElementsByTagName(tagName);
      count += handleElements(nodeList, handler);
    }

    return count;
  }

  /**
   * @author Eike Stepper
   */
  public interface ElementHandler
  {
    public void handleElement(Element element) throws Exception;
  }

  /**
   * @author Eike Stepper
   */
  public static final class ElementUpdater
  {
    private final String tagName;

    private String textValue;

    private int index;

    public ElementUpdater(final Element rootElement, String tagName) throws Exception
    {
      this.tagName = tagName;
      handleElementsByTagName(rootElement, tagName, new ElementHandler()
      {
        public void handleElement(Element element) throws Exception
        {
          if (element.getParentNode() == rootElement)
          {
            textValue = element.getTextContent();
          }

          if (textValue == null)
          {
            ++index;
          }
        }
      });
    }

    public String update(String text, String newValue)
    {
      if (!ObjectUtil.equals(textValue, newValue))
      {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < index; i++)
        {
          builder.append(".*?<" + tagName + ">.*?</" + tagName + ">");
        }

        builder.append(".*?<" + tagName + ">(.*?)</" + tagName + ">.*");

        Pattern pattern = Pattern.compile(builder.toString(), Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches())
        {
          text = text.substring(0, matcher.start(1)) + newValue + text.substring(matcher.end(1));
        }
      }

      return text;
    }
  }
}
