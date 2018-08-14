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

import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.core.util.PortletModelUtil;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import org.w3c.dom.Element;

/**
 * @author Kamesh Sampath
 */
public class QNameValueBinding extends XmlValueBindingImpl {

	@Override
	public XmlNode getXmlNode() {
		XmlElement parent = xml();

		XmlElement element = parent.getChildElement(_params[0], false);

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
			XmlElement qNameElement = parent.getChildElement(_params[0], false);

			value = qNameElement.getText();
		}

		return value;
	}

	@Override
	public void write(String value) {
		XmlElement parent = xml(true);

		// System.out.println( "EventDefinitionValueBinding.write()" + parent );

		XmlElement qNameElement = parent.getChildElement(_params[0], true);

		qNameElement.setChildNodeText(_path, value, true);

		// Only for debugging purposes

		try {
			Element element = parent.getDomNode();

			PortletModelUtil.printDocument(element.getOwnerDocument(), System.out);
		}
		catch (IOException ioe) {
			PortletCore.logError(ioe);
		}
		catch (TransformerException te) {
			PortletCore.logError(te);
		}
	}

	private String[] _params;
	private XmlPath _path;

}