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

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Gregory Amerson
 */
public class CDataValueBindingImpl extends XmlValueBindingImpl {

	@Override
	public void init(Property property) {
		super.init(property);

		PropertyDef propertyDef = property.definition();

		XmlBinding bindingAnnotation = propertyDef.getAnnotation(XmlBinding.class);

		_path = new XmlPath(bindingAnnotation.path(), resource().getXmlNamespaceResolver());
	}

	@Override
	public String read() {
		String retval = null;

		XmlElement xmlElement = xml(false);

		if (xmlElement != null) {
			retval = xmlElement.getChildNodeText(_path);
		}

		return retval;
	}

	@Override
	public void write(String value) {
		XmlElement xmlElement = xml(true);

		XmlNode childNode = xmlElement.getChildNode(_path, true);

		Node cdataNode = childNode.getDomNode();

		CoreUtil.removeChildren(cdataNode);

		Document document = cdataNode.getOwnerDocument();

		cdataNode.insertBefore(document.createCDATASection(value), null);
	}

	private XmlPath _path;

}