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

package com.liferay.ide.portlet.core.display.model;

import org.eclipse.sapphire.Element;
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
@Image(path = "images/elcl16/category_16x16.gif")
public interface Category extends Element {

	public ElementType TYPE = new ElementType(Category.class);

	public ElementList<Category> getCategories();

	public Value<String> getName();

	public ElementList<DisplayPortlet> getPortlets();

	public void setName(String name);

	@Label(standard = "Categories")
	@Type(base = Category.class)
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "category", type = Category.class)})
	public ListProperty PROP_CATEGORIES = new ListProperty(TYPE, "Categories");

	@Label(standard = "Name")
	@Required
	@Unique
	@XmlBinding(path = "@name")
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	@Label(standard = "Portlets")
	@Type(base = DisplayPortlet.class)
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "portlet", type = DisplayPortlet.class)})
	public ListProperty PROP_PORTLETS = new ListProperty(TYPE, "Portlets");

}