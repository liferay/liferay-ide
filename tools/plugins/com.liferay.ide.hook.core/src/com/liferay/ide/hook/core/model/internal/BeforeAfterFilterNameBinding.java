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

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.hook.core.model.BeforeAfterFilterType;
import com.liferay.ide.hook.core.model.ServletFilterMapping;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author Gregory Amerson
 */
public class BeforeAfterFilterNameBinding extends XmlValueBindingImpl {

	@Override
	public String read() {
		String retval = null;
		XmlElement filterElement = null;

		XmlElement xmlElement = xml();
		BeforeAfterFilterType filterType = _getFilterType();

		String filterTypeText = filterType.getText();

		filterElement = xmlElement.getChildElement(filterTypeText, false);

		if (filterElement != null) {
			retval = filterElement.getText();
		}

		return retval;
	}

	@Override
	public void write(String value) {
		XmlElement xmlElement = xml();
		BeforeAfterFilterType filterType = _getFilterType();

		String filterTypeText = filterType.getText();

		XmlElement filterElement = xmlElement.getChildElement(filterTypeText, true);

		filterElement.setText(value);
	}

	private BeforeAfterFilterType _getFilterType() {
		ServletFilterMapping servletFilterMapping = property().nearest(ServletFilterMapping.class);

		Value<BeforeAfterFilterType> typeValue = servletFilterMapping.getBeforeAfterFilterType();

		return typeValue.content(true);
	}

}