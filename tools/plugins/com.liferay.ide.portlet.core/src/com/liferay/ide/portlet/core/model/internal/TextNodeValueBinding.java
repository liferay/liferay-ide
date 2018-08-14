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
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

/**
 * @author Kamesh Sampath
 */
public class TextNodeValueBinding extends XmlValueBindingImpl {

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

		CustomXmlValueBinding binding = propertyDef.getAnnotation(CustomXmlValueBinding.class);

		_params = binding.params();

		_path = new XmlPath(_params[0], resource().getXmlNamespaceResolver());
	}

	@Override
	public String read() {
		String value = "";

		XmlElement element = xml(false);

		if (element != null) {
			value = xml(true).getText();

			if (value != null) {
				value = value.trim();
			}
		}

		return value;
	}

	@Override
	public void write(String value) {
		String val = value;

		if (val != null) {
			val = value.trim();
		}

		xml(true).setText(val);
	}

	private String[] _params;
	private XmlPath _path;

}