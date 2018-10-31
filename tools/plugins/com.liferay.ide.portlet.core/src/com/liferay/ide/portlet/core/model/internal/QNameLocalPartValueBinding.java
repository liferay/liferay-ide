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
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

/**
 * @author Kamesh Sampath
 */
public class QNameLocalPartValueBinding extends XmlValueBindingImpl {

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

		CustomXmlValueBinding customXmlValueBinding = propertyDef.getAnnotation(CustomXmlValueBinding.class);

		_params = customXmlValueBinding.params();

		_path = new XmlPath(_params[0], resource().getXmlNamespaceResolver());
	}

	@Override
	public String read() {
		String value = null;

		XmlElement parent = xml(false);

		// Fix for Alias QName not displayed in list

		XmlElement qNameElement = null;

		if (_params[0].equals(parent.getLocalName())) {
			qNameElement = parent;
		}
		else {
			qNameElement = parent.getChildElement(_params[0], false);
		}

		if (qNameElement != null) {
			value = qNameElement.getText();

			if (value != null) {
				value = PortletUtil.stripPrefix(value.trim());
			}
		}

		return value;
	}

	@Override
	public void write(String value) {
		String val = value;
		XmlElement parent = xml(true);

		/**
		 * In some cases the parent node and the child nodes will be same, we need to
		 * ensure that we dont create them accidentally again
		 */
		XmlElement qNameElement = null;

		if (_params[0].equals(parent.getLocalName())) {
			qNameElement = parent;
		}
		else {
			qNameElement = parent.getChildElement(_params[0], true);
		}

		if (qNameElement != null) {
			val = val != null ? value.trim() : StringPool.EMPTY;

			// update only local part

			if ((_params.length == 2) && "localpart".equals(_params[1])) {
				String existingText = qNameElement.getText();

				if ((existingText != null) && (existingText.indexOf(":") != -1)) {
					String updatedLocalPart = existingText.substring(0, existingText.indexOf(":") + 1);

					updatedLocalPart = updatedLocalPart + val;

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

	private String[] _params;
	private XmlPath _path;

}