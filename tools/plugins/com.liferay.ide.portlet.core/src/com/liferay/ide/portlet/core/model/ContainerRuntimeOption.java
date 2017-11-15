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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 */
@Image(path = "images/elcl16/option_16x16.gif")
public interface ContainerRuntimeOption extends Element, Nameable {

	public ElementType TYPE = new ElementType(ContainerRuntimeOption.class);

	// *** Values ***

	@Type(base = OptionValue.class)
	@Label(standard = "Options")
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(type = OptionValue.class, element = "value")})
	public 	ListProperty PROP_OPTIONS = new ListProperty(TYPE, "Options");

	public ElementList<OptionValue> getOptions();

}