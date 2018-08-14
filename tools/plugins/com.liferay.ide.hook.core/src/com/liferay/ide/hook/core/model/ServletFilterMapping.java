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

import com.liferay.ide.hook.core.model.internal.BeforeAfterFilterNameBinding;
import com.liferay.ide.hook.core.model.internal.BeforeAfterFilterTypeBinding;
import com.liferay.ide.hook.core.model.internal.PortalFilterNamesPossibleValuesService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 */
@Image(path = "images/elcl16/filter_mapping_16x16.gif")
public interface ServletFilterMapping extends Element {

	public ElementType TYPE = new ElementType(ServletFilterMapping.class);

	public Value<String> getBeforeAfterFilterName();

	public Value<BeforeAfterFilterType> getBeforeAfterFilterType();

	public ElementList<Dispatcher> getDispatchers();

	public Value<String> getServletFilterName();

	public ElementList<URLPattern> getURLPatterns();

	public void setBeforeAfterFilterName(String value);

	public void setBeforeAfterFilterType(BeforeAfterFilterType value);

	public void setBeforeAfterFilterType(String value);

	public void setServletFilterName(String value);

	@CustomXmlValueBinding(impl = BeforeAfterFilterNameBinding.class)
	@Label(standard = "Portal Filter Name")
	@Service(impl = PortalFilterNamesPossibleValuesService.class)
	public ValueProperty PROP_BEFORE_AFTER_FILTER_NAME = new ValueProperty(TYPE, "BeforeAfterFilterName");

	@CustomXmlValueBinding(impl = BeforeAfterFilterTypeBinding.class)
	@DefaultValue(text = "before-filter")
	@Type(base = BeforeAfterFilterType.class)
	public ValueProperty PROP_BEFORE_AFTER_FILTER_TYPE = new ValueProperty(TYPE, "BeforeAfterFilterType");

	@Label(standard = "Dispatchers")
	@Type(base = Dispatcher.class)
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "dispatcher", type = Dispatcher.class)})
	public ListProperty PROP_DISPATCHERS = new ListProperty(TYPE, "Dispatchers");

	@Label(standard = "Servlet Filter Name")
	@PossibleValues(property = "/ServletFilters/ServletFilterName")
	@XmlBinding(path = "servlet-filter-name")
	public ValueProperty PROP_SERVLET_FILTER_NAME = new ValueProperty(TYPE, "ServletFilterName");

	@Label(standard = "url patterns")
	@Length(min = 1)
	@Type(base = URLPattern.class)
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "url-pattern", type = URLPattern.class)})
	public ListProperty PROP_URL_PATTERNS = new ListProperty(TYPE, "URLPatterns");

}