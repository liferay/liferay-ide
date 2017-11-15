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
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlValueBinding;

/**
 * @author Kamesh Sampath
 */
@Image(path = "images/elcl16/filter_mapping_16x16.gif")
@Label(standard = "filter mapping")
public interface FilterMapping extends Element {

	public ElementType TYPE = new ElementType(FilterMapping.class);

	// *** Filter ***

	@Label(standard = "filter")
	@Required
	@PossibleValues(property = "/Filters/Name")
	@XmlBinding(path = "filter-name")
	public ValueProperty PROP_FILTER = new ValueProperty(TYPE, "Filter");

	public Value<String> getFilter();

	public void setFilter(String value);

	// *** Portlet ***

	@Label(standard = "portlet")
	@Required
	@PossibleValues(property = "/Portlets/PortletName")
	@XmlValueBinding(path = "portlet-name", removeNodeOnSetIfNull = false)
	public 	ValueProperty PROP_PORTLET = new ValueProperty(TYPE, "Portlet");

	public Value<String> getPortlet();

	public void setPortlet(String value);

}
