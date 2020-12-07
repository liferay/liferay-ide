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

package com.liferay.ide.portlet.core.model;

import com.liferay.ide.portlet.core.model.internal.LocaleBundleValidationService;
import com.liferay.ide.portlet.core.model.internal.LocalePossibleValueService;
import com.liferay.ide.portlet.core.model.internal.LocaleTextNodeValueBinding;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
@Image(path = "images/elcl16/locale_16x16.gif")
public interface SupportedLocales extends Element {

	public ElementType TYPE = new ElementType(SupportedLocales.class);

	public Value<String> getSupportedLocale();

	public void setSupportedLocale(String value);

	@CustomXmlValueBinding(impl = LocaleTextNodeValueBinding.class)
	@Label(standard = "Locale")
	@Services({@Service(impl = LocalePossibleValueService.class), @Service(impl = LocaleBundleValidationService.class)})
	@Unique
	@XmlBinding(path = "")
	public ValueProperty PROP_SUPPORTED_LOCALE = new ValueProperty(TYPE, "SupportedLocale");

}