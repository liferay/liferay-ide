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

import com.liferay.ide.portlet.core.util.PortletUtil;

import java.util.Locale;

import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author Kamesh Sampath
 */
public class LocaleTextNodeValueBinding extends XmlValueBindingImpl {

	@Override
	public String read() {
		String value = null;

		XmlElement xmlElement = xml(false);

		if (xmlElement != null) {
			xmlElement = xml(true);

			value = xmlElement.getText();

			if (!value.isEmpty()) {
				value = value.trim();

				for (Locale locale : _availableLocales) {
					if (value.equals(locale.toString())) {
						value = PortletUtil.buildLocaleDisplayString(locale.getDisplayName(), locale);

						break;
					}
				}
			}
		}

		return value;
	}

	@Override
	public void write(String value) {
		String val = value;

		if (val != null) {
			val = PortletUtil.localeString(value.trim());

			XmlElement xmlElement = xml(true);

			xmlElement.setText(val);
		}
	}

	private Locale[] _availableLocales = Locale.getAvailableLocales();

}