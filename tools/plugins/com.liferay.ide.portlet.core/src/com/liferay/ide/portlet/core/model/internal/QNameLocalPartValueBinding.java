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

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.util.PortletAppModelConstants;
import com.liferay.ide.portlet.core.util.PortletUtil;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

/**
 * @author Kamesh Sampath
 */
public class QNameLocalPartValueBinding extends XmlValueBindingImpl {

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

		// Fix for Alias QName not displayed in list

		XmlElement qNameElement = null;

		if (parent.getLocalName().equals(params[0])) {
			qNameElement = parent;
		}
		else {
			qNameElement = parent.getChildElement(params[0], false);
		}

		// System.out.println( "Reading VALUE for Element ___________________ " +
		// qNameElement );

		if (qNameElement != null) {
			value = qNameElement.getText();

			if (value != null) {
				value = value.trim();
				value = PortletUtil.stripPrefix(value);
			}
		}

		return value;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.sapphire.modeling.ValuePropertyBinding#write(java.lang.String)
	 */
	@Override
	public void write(String value) {
		String val = value;
		XmlElement parent = xml(true);

		/*
		 * In some cases the parent node and the child nodes will be same, we need to
		 * ensure that we dont create them accidentally again
		 */
		// System.out.println( "QNameLocalPartValueBinding.write() - Parent local name:"
		// + parent.getLocalName() );

		XmlElement qNameElement = null;

		if (parent.getLocalName().equals(params[0])) {
			qNameElement = parent;
		}
		else {
			qNameElement = parent.getChildElement(params[0], true);
		}

		// System.out.println( "TextNodeValueBinding.write() - Parent " + xml( true
		// ).getParent() );

		if (qNameElement != null) {
			val = val != null ? value.trim() : StringPool.EMPTY;

			if (params.length == 2 && "localpart".equals(params[1])) {// update only local part

				// System.out.println( "VALUE
				// ___________________ " + val );

				String existingText = qNameElement.getText();

				if ((existingText != null) && (existingText.indexOf(":") != -1)) {
					String updatedLocalPart = existingText.substring(0, (existingText.indexOf(":") + 1));
					updatedLocalPart = updatedLocalPart + val;

					// System.out.println( "Updated value ___________________ " + updatedLocalPart
					// );

					qNameElement.setText(updatedLocalPart);
				}
				else {
					qNameElement.setText(PortletAppModelConstants.DEFAULT_QNAME_PREFIX + ":" + val);
				}
			}
			else {
				qNameElement.setText(PortletAppModelConstants.DEFAULT_QNAME_PREFIX + ":" + val);
			}
		}
	}

	private String[] params;
	private XmlPath path;

}