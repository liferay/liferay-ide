/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.portlet.core.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.sapphire.modeling.xml.XmlElement;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Kamesh Sampath
 */
public class PortletModelUtil {

	/**
	 * @param domNode
	 * @param namespaceURI
	 * @param currValue
	 * @return
	 */
	public static String defineNS(XmlElement element, QName currValueAsQName) {
		String qualifiedNodeValue = null;

		String namespaceURI = currValueAsQName.getNamespaceURI();

		String defaultPrefix = PortletAppModelConstants.DEFAULT_QNAME_PREFIX;
		Element domNode = element.getDomNode();
		boolean nsDefined = false;
		String currNodeValue = element.getText();
		Attr attrib = null;

		if ((currNodeValue != null) && (currNodeValue.indexOf(":") != -1)) {
			String name = PortletUtil.stripSuffix(currNodeValue);

			// Check if the NS is already declared

			attrib = domNode.getAttributeNode(String.format(PortletAppModelConstants.NS_DECL, name));

			if (attrib != null) {
				String nsURI = attrib.getValue();

				if (namespaceURI.equals(nsURI)) {
					nsDefined = true;
					defaultPrefix = name;
				}
				else {

					// only NS changed, update it and send the existing value

					attrib.setNodeValue(namespaceURI);

					return element.getText();
				}
			}
		}

		// remove the exisiting attribute

		if (nsDefined) {
			if (attrib != null) {
				domNode.removeAttributeNode(attrib);
			}
		}

		// update the element

		String qualifiedName = String.format(PortletAppModelConstants.NS_DECL, defaultPrefix);
		Attr attr = domNode.getAttributeNodeNS(namespaceURI, defaultPrefix);

		if (attr == null) {
			domNode.setAttributeNS(namespaceURI, qualifiedName, namespaceURI);
		}

		qualifiedNodeValue = defaultPrefix + ":" + currValueAsQName.getLocalPart();

		return qualifiedNodeValue;
	}

	/**
	 * @param doc
	 * @param out
	 * @throws IOException
	 * @throws TransformerException
	 */
	public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();

		Transformer transformer = tf.newTransformer();

		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		try (OutputStreamWriter outputStream = new OutputStreamWriter(out, "UTF-8")) {
			transformer.transform(new DOMSource(doc), new StreamResult(outputStream));
		}
	}

}