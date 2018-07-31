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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 */
@Image(path = "images/elcl16/filter_16x16.gif")
public interface ServletFilter extends Element {

	public ElementType TYPE = new ElementType(ServletFilter.class);

	public ElementList<Param> getInitParams();

	public ReferenceValue<JavaTypeName, JavaType> getServletFilterImpl();

	public Value<String> getServletFilterName();

	public void setServletFilterImpl(JavaTypeName value);

	public void setServletFilterImpl(String value);

	public void setServletFilterName(String value);

	// *** InitParams ***

	@Label(standard = "Init Params")
	@Type(base = Param.class)
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "init-param", type = Param.class)})
	public ListProperty PROP_INIT_PARAMS = new ListProperty(TYPE, "InitParams");

	// ** ServletFilterImpl

	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = "javax.servlet.Filter")
	@Label(standard = "Servlet Filter Implementation")
	@MustExist
	@Reference(target = JavaType.class)
	@Required
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "servlet-filter-impl")
	public ValueProperty PROP_SERVLET_FILTER_IMPL = new ValueProperty(TYPE, "ServletFilterImpl");

	// *** Servlet Filter Name ***

	@Label(standard = "Servlet Filter Name")
	@Required
	@XmlBinding(path = "servlet-filter-name")
	public ValueProperty PROP_SERVLET_FILTER_NAME = new ValueProperty(TYPE, "ServletFilterName");

}