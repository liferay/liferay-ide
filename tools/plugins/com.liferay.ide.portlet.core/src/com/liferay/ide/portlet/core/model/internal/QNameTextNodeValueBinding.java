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

package com.liferay.ide.portlet.core.model.internal;

import com.liferay.ide.portlet.core.util.PortletAppModelConstants;
import com.liferay.ide.portlet.core.util.PortletModelUtil;
import com.liferay.ide.portlet.core.util.PortletUtil;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * @author Kamesh Sampath
 */
public class QNameTextNodeValueBinding extends XmlValueBindingImpl {

	@Override
	public XmlNode getXmlNode() {
		XmlElement element = xml(false);

		if (element != null) {
			return element.getChildNode(_path, false);
		}

		return null;
	}

	@Override
	public void init(Property property) {
		super.init(property);

		PropertyDef propertyDef = property.definition();

		CustomXmlValueBinding customXmlValueBinding = propertyDef.getAnnotation(CustomXmlValueBinding.class);

		_params = customXmlValueBinding.params();

		_path = new XmlPath(_params[0], resource().getXmlNamespaceResolver());
	}

	@Override
	public String read() {
		XmlElement parent = xml(false);

		if (parent == null) {
			return "";
		}

		XmlElement qNamedElement = parent.getChildElement(_params[0], false);

		if (qNamedElement == null) {
			return "";
		}

		Element domNode = qNamedElement.getDomNode();

		String value = qNamedElement.getText();

		if (value == null) {
			return "";
		}

		String prefix = PortletUtil.stripSuffix(value.trim());

		Attr attrib = domNode.getAttributeNode(String.format(PortletAppModelConstants.NS_DECL, prefix));

		if (attrib != null) {
			QName qName = new QName(attrib.getValue(), PortletUtil.stripPrefix(value));

			value = qName.toString();
		}

		return value;
	}

	@Override
	public void write(String value) {
		String qNameAsString = value;
		XmlElement parent = xml(true);

		if ((qNameAsString != null) && !"Q_NAME".equals(qNameAsString)) {
			qNameAsString = value.trim();

			QName qName = QName.valueOf(qNameAsString);

			XmlElement qNamedElement = parent.getChildElement(_params[0], true);

			String qualifiedNodeValue = PortletModelUtil.defineNS(qNamedElement, qName);

			qNamedElement.setText(qualifiedNodeValue);
		}
		else {
			parent.remove();
		}
	}

	private String[] _params;
	private XmlPath _path;

}