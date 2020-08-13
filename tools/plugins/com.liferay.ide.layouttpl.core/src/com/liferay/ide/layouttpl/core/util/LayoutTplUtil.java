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

package com.liferay.ide.layouttpl.core.util;

import com.liferay.ide.core.templates.ITemplateContext;
import com.liferay.ide.core.templates.ITemplateOperation;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.ArrayStack;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 * @author Joye Luo
 */
@SuppressWarnings("restriction")
public class LayoutTplUtil implements SapphireContentAccessor {

	public static void createLayoutTplContext(ITemplateOperation op, LayoutTplElement layouttpl) {
		ITemplateContext ctx = op.getContext();

		ctx.put("root", layouttpl);
		ctx.put("stack", new ArrayStack());
	}

	public static IDOMElement[] findChildElementsByClassName(
		IDOMElement parentElement, String childElementTag, String className) {

		if ((parentElement == null) || !parentElement.hasChildNodes()) {
			return null;
		}

		List<IDOMElement> childElements = new ArrayList<>();

		List<Element> divChildren = getChildElementsByTagName(parentElement, childElementTag);

		for (Element child : divChildren) {
			IDOMElement childDivElement = (IDOMElement)child;

			if (hasClassName(childDivElement, className)) {
				childElements.add(childDivElement);
			}
		}

		return childElements.toArray(new IDOMElement[0]);
	}

	public static IDOMElement findMainContentElement(IDOMDocument rootDocument) {
		if ((rootDocument == null) || !rootDocument.hasChildNodes()) {
			return null;
		}

		IDOMElement mainContentElement = null;

		mainContentElement = (IDOMElement)rootDocument.getElementById("main-content");

		return mainContentElement;
	}

	public static List<Element> getChildElementsByTagName(IDOMElement parentElement, String childElementTag) {
		Node parentElementNode = (Node)parentElement;

		NodeList childNodes = parentElementNode.getChildNodes();

		List<Element> childElements = new ArrayList<>();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);

			if ((childNode.getNodeType() == 1) && (childElementTag != null)) {
				Element element = (Element)childNode;

				if (StringUtil.equals(element.getTagName(), childElementTag)) {
					childElements.add(element);
				}
			}
		}

		return childElements;
	}

	public static String getRoleValue(IDOMElement mainContentElement, String defaultValue) {
		String retval = defaultValue;
		String currentRoleValue = mainContentElement.getAttribute("role");

		if (CoreUtil.isNotNullOrEmpty(currentRoleValue)) {
			retval = currentRoleValue;
		}

		return retval;
	}

	public static int getWeightValue(IDOMElement portletColumnElement, int defaultValue) {
		int weightValue = defaultValue;

		if (portletColumnElement == null) {
			return weightValue;
		}

		String classAttr = portletColumnElement.getAttribute("class");

		if (CoreUtil.isNullOrEmpty(classAttr)) {
			return weightValue;
		}

		// resolve column weight of bootstrap style, portal version equal to 62

		Matcher matcher = _classAttributePattern.matcher(classAttr);

		if (matcher.matches()) {
			String weightString = matcher.group(2);

			if (CoreUtil.isNotNullOrEmpty(weightString)) {
				try {
					weightValue = Integer.parseInt(weightString);
				}
				catch (NumberFormatException nfe) {
					weightValue = 0;
				}
			}
		}
		else {
			matcher = _pattern.matcher(classAttr);

			if (matcher.matches()) {
				String weightString = matcher.group(2);

				if (CoreUtil.isNotNullOrEmpty(weightString)) {
					try {
						weightValue = Integer.parseInt(weightString);
					}
					catch (NumberFormatException nfe) {
						weightValue = 0;
					}
				}
			}
		}

		return weightValue;
	}

	public static boolean hasClassName(IDOMElement domElement, String className) {
		boolean retval = false;

		if (domElement != null) {
			String classAttr = domElement.getAttribute("class");

			if (!CoreUtil.isNullOrEmpty(classAttr)) {
				retval = classAttr.contains(className);
			}
		}

		return retval;
	}

	private static final Pattern _classAttributePattern = Pattern.compile("(.*span)(\\d+)");
	private static final Pattern _pattern = Pattern.compile(".*col-(xs|sm|md|lg)-(\\d+).*");

}