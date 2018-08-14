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
public class NameAndQNameChoiceValueBinding extends XmlValueBindingImpl {

	@Override
	public XmlNode getXmlNode() {
		XmlElement parent = xml();

		XmlElement element = parent.getChildElement(_Q_NAME, false);

		if (element != null) {
			return element;
		}

		element = parent.getChildElement(_NAME, false);

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

		String value = null;

		if (parent != null) {
			XmlElement eventNameElement = parent.getChildElement(_NAME, false);
			XmlElement eventQNameElement = parent.getChildElement(_Q_NAME, false);

			if ((eventNameElement != null) && _NAME.equals(_params[0])) {
				value = eventNameElement.getText();
			}
			else if ((eventQNameElement != null) && _Q_NAME.equals(_params[0])) {
				value = eventQNameElement.getText();
			}
		}

		return value;
	}

	@Override
	public void write(String value) {
		XmlElement parent = xml(true);

		XmlElement eventNameElement = parent.getChildElement(_NAME, false);
		XmlElement eventQNameElement = parent.getChildElement(_Q_NAME, false);

		if (_NAME.equals(_params[0]) && (eventQNameElement != null)) {
			parent.removeChildNode(_Q_NAME);
		}
		else if (_Q_NAME.equals(_params[0]) && (eventNameElement != null)) {
			parent.removeChildNode(_NAME);
		}

		parent.setChildNodeText(_path, value, true);
	}

	private static final String _NAME = "name";

	private static final String _Q_NAME = "qname";

	private String[] _params;
	private XmlPath _path;

}