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

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

/**
 * @author Kamesh Sampath
 */
public class TextNodeValueBinding extends XmlValueBindingImpl {

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

		XmlElement element = xml(false);

		if (element != null) {
			value = xml(true).getText();

			// System.out.println( "Reading VALUE ___________________ " + value );

			if (value != null) {
				value = value.trim();
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

		// System.out.println( "VALUE ___________________ " + val );

		if (val != null) {
			val = value.trim();
		}

		// System.out.println( "TextNodeValueBinding.write() - Parent " + xml( true
		// ).getParent() );

		xml(true).setText(val);
	}

	private String[] params;
	private XmlPath path;

}