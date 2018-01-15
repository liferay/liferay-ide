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

package com.liferay.ide.portlet.core.model.lfrportlet.common;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlValuePropertyBinding;

/**
 * @author Kamesh Sampath
 */
public final class TextNodeValueBinding extends XmlValuePropertyBinding {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sapphire.modeling.PropertyBinding#init(org.eclipse.sapphire.
	 * modeling.Element, org.eclipse.sapphire.modeling.Property, java.lang.String[])
	 */
	@Override
	public void init(final Element element, final Property property, final String[] params) {
		super.init(element, property, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sapphire.modeling.ValuePropertyBinding#read()
	 */
	@Override
	public String read() {
		String value = null;

		final XmlElement element = xml(false);

		if (element != null) {

			value = xml(true).getText();

			// System.out.println( "Reading VALUE ___________________ " + value );

			if (value != null) {
				value = value.trim();
			}
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.sapphire.modeling.ValuePropertyBinding#write(java.lang.String)
	 */
	@Override
	public void write(final String value) {
		String val = value;

		// System.out.println( "VALUE ___________________ " + val );

		if (val != null) {
			val = value.trim();
		}

		// System.out.println( "TextNodeValueBinding.write() - Parent " + xml( true
		// ).getParent() );

		xml(true).setText(val);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.sapphire.modeling.xml.XmlValuePropertyBinding#getXmlNode()
	 */
	@Override
	public XmlNode getXmlNode() {
		final XmlElement element = xml(false);

		if (element != null) {
			return element;
		}
		return null;
	}

}
