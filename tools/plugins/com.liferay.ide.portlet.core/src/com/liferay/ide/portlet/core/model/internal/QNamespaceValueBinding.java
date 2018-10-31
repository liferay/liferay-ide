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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.portlet.core.util.PortletAppModelConstants;

import java.util.List;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.modeling.xml.XmlAttribute;
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
public class QNamespaceValueBinding extends XmlValueBindingImpl {

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
		String value = null;

		XmlElement parent = xml(false);

		XmlElement qNameElement = null;

		// Fix for Alias QName not displayed in list

		if (_params[0].equals(parent.getLocalName())) {
			qNameElement = parent;
		}
		else {
			qNameElement = parent.getChildElement(_params[0], false);
		}

		if (qNameElement != null) {
			List<XmlAttribute> listOfAttibutes = qNameElement.getAttributes();

			XmlAttribute xmlAttribute = ListUtil.isNotEmpty(listOfAttibutes) ? listOfAttibutes.get(0) : null;

			if (xmlAttribute != null) {
				value = xmlAttribute.getText();
			}
		}

		if (value != null) {
			return value.trim();
		}

		return value;
	}

	@Override
	public void write(String value) {
		String val = value;

		XmlElement parent = xml(true);

		/**
		 * In some cases the parent node and the child nodes will be same, we need to
		 * ensure that we dont create them accidentally again
		 */
		XmlElement qNameElement = null;

		if (_params[0].equals(parent.getLocalName())) {
			qNameElement = parent;
		}
		else {
			qNameElement = parent.getChildElement(_params[0], true);
		}

		if ((qNameElement != null) && (val != null)) {
			val = value.trim();

			Element qnameDef = qNameElement.getDomNode();

			/**
			 * Check to ensure that the attribute is not added multiple times, check if the
			 * attribute already exist if yes remove it add add it afresh
			 */
			Attr oldQnsAttribute = qnameDef.getAttributeNode(PortletAppModelConstants.QNAME_NS_DECL);

			if (oldQnsAttribute == null) {
				qnameDef.setAttributeNS(
					PortletAppModelConstants.XMLNS_NS_URI, PortletAppModelConstants.QNAME_NS_DECL, val);
			}
			else {
				qnameDef.removeAttributeNode(oldQnsAttribute);
				qnameDef.setAttributeNS(
					PortletAppModelConstants.XMLNS_NS_URI, PortletAppModelConstants.QNAME_NS_DECL, val);
			}
		}
		else if ((qNameElement != null) && (val == null)) {
			qNameElement.remove();
		}
	}

	private String[] _params;
	private XmlPath _path;

}