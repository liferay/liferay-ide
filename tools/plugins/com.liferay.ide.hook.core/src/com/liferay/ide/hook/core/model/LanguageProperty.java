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

package com.liferay.ide.hook.core.model;

import com.liferay.ide.core.model.internal.GenericResourceBundlePathService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.FileExtensions;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
@Image(path = "images/elcl16/locale_16x16.gif")
public interface LanguageProperty extends Element {

	public ElementType TYPE = new ElementType(LanguageProperty.class);

	public Value<String> getValue();

	public void setValue(String value);

	@FileExtensions(expr = "properties")
	@Label(standard = "Language Properties")
	@Service(impl = GenericResourceBundlePathService.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FILE)
	@XmlBinding(path = "")
	public ValueProperty PROP_VALUE = new ValueProperty(TYPE, "Value");

}