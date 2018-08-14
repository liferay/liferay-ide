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
 * @author kamesh.sampath
 */
public class ChoiceValueBinding extends XmlValueBindingImpl {

	@Override
	public XmlNode getXmlNode() {
		XmlElement parent = xml();

		XmlElement element = parent.getChildElement(_params[1], false);

		if (element != null) {
			return element;
		}

		element = parent.getChildElement(_params[1], false);

		if (element != null) {
			return element;
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

		String value = "";

		XmlElement param1Element = parent.getChildElement(_params[1], false);
		XmlElement param2Element = parent.getChildElement(_params[2], false);

		if ((param1Element != null) && _params[0].equals(_params[1])) {
			value = param1Element.getText();
		}
		else if ((param2Element != null) && _params[0].equals(_params[2])) {
			value = param2Element.getText();
		}

		return value;
	}

	@Override
	public void write(String value) {
		XmlElement parent = xml(true);

		XmlElement param1Element = parent.getChildElement(_params[1], false);
		XmlElement param2Element = parent.getChildElement(_params[2], false);

		if ((param1Element != null) && _params[0].equals(_params[1])) {
			parent.removeChildNode(_params[2]);
		}
		else if ((param2Element != null) && _params[0].equals(_params[2])) {
			parent.removeChildNode(_params[1]);
		}

		parent.setChildNodeText(_path, value, true);
	}

	private String[] _params;
	private XmlPath _path;

}