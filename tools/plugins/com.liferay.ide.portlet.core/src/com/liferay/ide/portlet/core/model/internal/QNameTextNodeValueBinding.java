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

		if (parent != null) {
			XmlElement qNamedElement = parent.getChildElement(this.params[0], false);

			if (qNamedElement != null) {
				Element domNode = qNamedElement.getDomNode();
				value = qNamedElement.getText();

				if (value != null) {
					String prefix = PortletUtil.stripSuffix(value.trim());

					Attr attrib = domNode.getAttributeNode(String.format(PortletAppModelConstants.NS_DECL, prefix));

					if (attrib != null) {
						QName qname = new QName(attrib.getValue(), PortletUtil.stripPrefix(value));

						value = qname.toString();
					}
				}
			}
		}

		// System.out.println( "QNamedTextNodeValueBinding.read()" + value );

		return value;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.sapphire.modeling.ValuePropertyBinding#write(java.lang.String)
	 */
	@Override
	public void write(String value) {
		String qNameAsString = value;
		XmlElement parent = xml(true);

		// System.out.println( "VALUE ___________________ " + qNameAsString );

		if ((qNameAsString != null) && !"Q_NAME".equals(qNameAsString)) {
			qNameAsString = value.trim();

			QName qName = QName.valueOf(qNameAsString);

			XmlElement qNamedElement = parent.getChildElement(this.params[0], true);

			String qualifiedNodeValue = PortletModelUtil.defineNS(qNamedElement, qName);

			qNamedElement.setText(qualifiedNodeValue);
		}
		else {

			// System.out.println( "Remove:" + params[0] + " from " + parent );

			parent.remove();
		}

		// System.out.println( "TextNodeValueBinding.write() - Parent " + xml( true
		// ).getParent() );

	}

	private String[] params;
	private XmlPath path;

}