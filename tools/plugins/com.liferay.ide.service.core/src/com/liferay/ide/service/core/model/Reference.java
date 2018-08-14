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

package com.liferay.ide.service.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
@Image(path = "images/references_16x16.png")
public interface Reference extends Element {

	public ElementType TYPE = new ElementType(Reference.class);

	public Value<String> getEntity();

	// *** Package-path ***

	public Value<String> getPackagePath();

	public void setEntity(String value);

	public void setPackagePath(String value);

	// *** Entity ***

	@Label(standard = "&entity")
	@XmlBinding(path = "@entity")
	public ValueProperty PROP_ENTITY = new ValueProperty(TYPE, "Entity");

	@Label(standard = "&Package path")
	@XmlBinding(path = "@package-path")
	public ValueProperty PROP_PACKAGE_PATH = new ValueProperty(TYPE, "PackagePath");

}