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

import java.util.List;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.xml.XmlAttribute;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import org.w3c.dom.Attr;

/**
 * @author Kamesh Sampath
 */
public class QNamespaceValueBinding extends XmlValueBindingImpl {

	String[] params;
	XmlPath path;

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.sapphire.modeling.xml.XmlValuePropertyBinding#getXmlNode()
	 */
	@Override
	public XmlNode getXmlNode() {
		XmlElement element = xml(false);

		if (element != null) {
			return element.getChildNode(this.path, false);
		}

		return null;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.sapphire.modeling.PropertyBinding#init(org.eclipse.sapphire.
	 *      modeling.Element, org.eclipse.sapphire.modeling.Property,
	 *      java.lang.String[])
	 */
	@Override
	public void init(Property property) {
		super.init(property);

		this.params = property.definition().getAnnotation(CustomXmlValueBinding.class).params();
		this.path = new XmlPath(params[0], resource().getXmlNamespaceResolver());
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.sapphire.modeling.ValuePropertyBinding#read()
	 */
	@Override
	public String read() {
		String value = null;
		XmlElement parent = xml(false);
		XmlElement qNameElement = null;

		// Fix for Alias QName not displayed in list

		if (parent.getLocalName().equals(params[0])) {
			qNameElement = parent;
		}
		else {
			qNameElement = parent.getChildElement(params[0], false);
		}

		if (qNameElement != null) {

			// System.out.println( qNameElement );

			List<XmlAttribute> listOfAttibutes = qNameElement.getAttributes();

			XmlAttribute xmlAttribute =
				listOfAttibutes != null && listOfAttibutes.size() > 0 ? listOfAttibutes.get(0) : null;

			if (xmlAttribute != null) {
				value = xmlAttribute.getText();
			}
		}

		// System.out.println( "QNamespaceValueBinding.read() - Value:" + value );

		if (value != null) {
			return value.trim();
		}

		return value;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.sapphire.modeling.ValuePropertyBinding#write(java.lang.String)
	 */
	@Override
	public void write(String value) {
		String val = value;

		// System.out.println( "VALUE ___________________ " + val );

		XmlElement parent = xml(true);

		/*
		 * In some cases the parent node and the child nodes will be same, we need to
		 * ensure that we dont create them accidentally again
		 */
		// System.out.println( "QNamespaceValueBinding.write() - Parent local name:" +
		// parent.getLocalName() );

		XmlElement qNameElement = null;

		if (parent.getLocalName().equals(params[0])) {
			qNameElement = parent;
		}
		else {
			qNameElement = parent.getChildElement(params[0], true);
		}

		if ((qNameElement != null) && (val != null)) {

			// System.out.println( "QNamespaceValueBinding.write() - 1" );

			val = value.trim();
			org.w3c.dom.Element qnameDef = qNameElement.getDomNode();
			/*
			 * Check to ensure that the attribute is not added multiple times, check if the
			 * attribute already exist if yes remove it add add it afresh
			 */
			Attr oldQnsAttribute = qnameDef.getAttributeNode(PortletAppModelConstants.QNAME_NS_DECL);

			if (oldQnsAttribute == null) {

				// System.out.println( "QNamespaceValueBinding.write() - Attrib does not
				// exist");

				qnameDef
					.setAttributeNS(PortletAppModelConstants.XMLNS_NS_URI, PortletAppModelConstants.QNAME_NS_DECL, val);
			}
			else {

				// System.out.println( "QNamespaceValueBinding.write() - Attrib exist" );

				qnameDef.removeAttributeNode(oldQnsAttribute);
				qnameDef.setAttributeNS(
					PortletAppModelConstants.XMLNS_NS_URI, PortletAppModelConstants.QNAME_NS_DECL, val);
			}
		}

		// Remove the nod/e if it exists and current value is null

		else if ((qNameElement != null) && (val == null)) {
			qNameElement.remove();
		}
	}

}