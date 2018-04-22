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

package com.liferay.ide.core.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class NodeUtil {

	public static Element appendChildElement(Element parentElement, String newElementName) {
		return appendChildElement(parentElement, newElementName, null);
	}

	public static Element appendChildElement(Element parentElement, String newElementName, String initialTextContent) {
		Element newChildElement = null;

		if ((parentElement == null) || (newElementName == null)) {
			return null;
		}

		Document ownerDocument = parentElement.getOwnerDocument();

		newChildElement = ownerDocument.createElement(newElementName);

		if (initialTextContent != null) {
			newChildElement.appendChild(ownerDocument.createTextNode(initialTextContent));
		}

		parentElement.appendChild(newChildElement);

		return newChildElement;
	}

	public static Node appendTextNode(Element parentElement, String initialTextContent) {
		if (parentElement == null) {
			return null;
		}

		Document ownerDocument = parentElement.getOwnerDocument();

		Node newChildElement = ownerDocument.createTextNode(initialTextContent);

		parentElement.appendChild(newChildElement);

		return newChildElement;
	}

	public static Element findChildElement(Element parentElement, String elementName) {
		if (parentElement == null) {
			return null;
		}

		NodeList children = parentElement.getChildNodes();

		if (isEmpty(children)) {
			return null;
		}

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);

			if (child instanceof Element && child.getNodeName().equals(elementName)) {
				return (Element)child;
			}
		}

		return null;
	}

	public static Node findFirstChild(Element element, String elementName) {
		if ((element == null) || CoreUtil.isNullOrEmpty(elementName)) {
			return null;
		}

		NodeList children = element.getChildNodes();

		if (isEmpty(children)) {
			return null;
		}

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);

			if (elementName.equals(child.getNodeName())) {
				return child;
			}
		}

		return null;
	}

	public static Node findLastChild(Element element, String elementName) {
		if ((element == null) || CoreUtil.isNullOrEmpty(elementName)) {
			return null;
		}

		NodeList children = element.getChildNodes();

		if (isEmpty(children)) {
			return null;
		}

		for (int i = children.getLength() - 1; i >= 0; i--) {
			Node child = children.item(i);

			if (elementName.equals(child.getNodeName())) {
				return child;
			}
		}

		return null;
	}

	public static String getChildElementContent(Node parent, String childElement) {
		NodeList children = parent.getChildNodes();

		if (isEmpty(children)) {
			return null;
		}

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);

			if (child instanceof Element && child.getNodeName().equals(childElement)) {
				return getTextContent((Element)child);
			}
		}

		return null;
	}

	public static Node getFirstNamedChildNode(Element element, String string) {
		NodeList children = element.getChildNodes();

		if (isEmpty(children)) {
			return null;
		}

		for (int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);

			if (item.getNodeName().equals(string)) {
				return item;
			}
		}

		return null;
	}

	public static String getTextContent(Node node) {
		NodeList children = node.getChildNodes();

		if (children.getLength() == 1) {
			String value = children.item(0).getNodeValue();

			return value.trim();
		}

		StringBuffer sb = new StringBuffer();

		Node child = node.getFirstChild();

		while (child != null) {
			sb.append(child.getNodeValue().trim());

			child = child.getNextSibling();
		}

		return sb.toString().trim();
	}

	public static Element insertChildElement(
		Element parentElement, Node refNode, String newElementName, String initialTextContent) {

		Element newChildElement = null;

		if ((parentElement != null) && (newElementName != null)) {
			Document ownerDocument = parentElement.getOwnerDocument();

			newChildElement = ownerDocument.createElement(newElementName);

			if (initialTextContent != null) {
				newChildElement.appendChild(ownerDocument.createTextNode(initialTextContent));
			}

			parentElement.insertBefore(newChildElement, refNode);
		}

		return newChildElement;
	}

	public static Element insertChildElementAfter(
		Element parentElement, Node refNode, String newElementName, String initialTextContent) {

		Element newChildElement = null;

		if ((parentElement != null) && (newElementName != null)) {
			Document ownerDocument = parentElement.getOwnerDocument();

			newChildElement = ownerDocument.createElement(newElementName);

			if (initialTextContent != null) {
				newChildElement.appendChild(ownerDocument.createTextNode(initialTextContent));
			}

			if (parentElement.getLastChild().equals(refNode)) {
				parentElement.appendChild(newChildElement);
			}
			else {
				parentElement.insertBefore(newChildElement, refNode.getNextSibling());
			}
		}

		return newChildElement;
	}

	public static boolean isEmpty(NodeList nodes) {
		if ((nodes == null) || (nodes.getLength() == 0)) {
			return true;
		}

		return false;
	}

	public static void removeChildren(Element element) {
		while ((element != null) && element.hasChildNodes()) {
			element.removeChild(element.getFirstChild());
		}
	}

	public static void removeChildren(Node node) {
		if ((node == null) || (node.getChildNodes() == null) || (node.getChildNodes().getLength() <= 0)) {
			return;
		}

		NodeList children = node.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			node.removeChild(children.item(i));
		}
	}

	public static Text setTextContent(Node namespaceNode, String textContent) {
		Text retval = null;

		if (namespaceNode instanceof Text) {
			namespaceNode.setNodeValue(textContent);

			retval = (Text)namespaceNode;
		}
		else if (namespaceNode instanceof Element) {
			Element namespaceElement = (Element)namespaceNode;

			removeChildren(namespaceElement);

			retval = namespaceElement.getOwnerDocument().createTextNode(textContent);

			namespaceElement.appendChild(retval);
		}

		return retval;
	}

}