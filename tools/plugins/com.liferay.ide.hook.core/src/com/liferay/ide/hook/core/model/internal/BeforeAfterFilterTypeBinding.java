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

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author Gregory Amerson
 */
public class BeforeAfterFilterTypeBinding extends XmlValueBindingImpl {

	@Override
	public void init(Property property) {
		super.init(property);

		PropertyDef propertyDef = property.definition();

		DefaultValue defaultValue = propertyDef.getAnnotation(DefaultValue.class);

		_defaultValueText = defaultValue.text();
	}

	@Override
	public String read() {

		// check for existence of before-filter or after-filter elements, if neither
		// exist, then return default value

		XmlElement xmlElement = xml();

		XmlElement beforeFilterElement = xmlElement.getChildElement(
			BeforeAfterFilterType.BEFORE_FILTER.getText(), false);

		if (beforeFilterElement != null) {
			return BeforeAfterFilterType.BEFORE_FILTER.getText();
		}

		XmlElement afterFilterElement = xmlElement.getChildElement(BeforeAfterFilterType.AFTER_FILTER.getText(), false);

		if (afterFilterElement != null) {
			return BeforeAfterFilterType.AFTER_FILTER.getText();
		}

		if (_localValue != null) {
			return _localValue;
		}

		return _defaultValueText;
	}

	@Override
	public void write(String value) {
		XmlElement xmlElement = xml();

		XmlElement filterElement = xmlElement.getChildElement(BeforeAfterFilterType.BEFORE_FILTER.getText(), false);

		if (filterElement == null) {
			filterElement = xmlElement.getChildElement(BeforeAfterFilterType.AFTER_FILTER.getText(), false);
		}

		String existingFilterValue = null;

		if (filterElement != null) {
			existingFilterValue = filterElement.getText();

			filterElement.remove();

			XmlElement newElement = xmlElement.getChildElement(value, true);

			newElement.setText(existingFilterValue);
		}
		else {
			_localValue = value;
		}
	}

	private String _defaultValueText;
	private String _localValue;

}