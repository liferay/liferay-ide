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

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 */
@Image(path = "/images/elcl16/parameter_16x16.gif")
@Label(full = "Public Render Parameter", standard = "Public Render Parameter")
public interface PublicRenderParameter extends QName, Identifiable {

	public ElementType TYPE = new ElementType(PublicRenderParameter.class);

	public ElementList<AliasQName> getAliases();

	public Value<String> getIdentifier();

	public void setIdentifier(String value);

	@Label(standard = "Aliases")
	@Type(base = AliasQName.class)
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "alias", type = AliasQName.class)})
	public ListProperty PROP_ALIASES = new ListProperty(TYPE, "Aliases");

	@Label(standard = "Identifier")
	@Required
	@Unique
	@XmlBinding(path = "identifier")
	public ValueProperty PROP_IDENTIFIER = new ValueProperty(TYPE, "Identifier");

}