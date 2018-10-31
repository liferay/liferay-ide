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
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeConstraintBehavior;
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
 * @author Simon Jiang
 */
@Image(path = "images/elcl16/filter_16x16.gif")
public interface Filter extends Describeable, Displayable {

	public ElementType TYPE = new ElementType(Filter.class);

	public ReferenceValue<JavaTypeName, JavaType> getImplementation();

	public ElementList<Param> getInitParams();

	public ElementList<LifeCycle> getLifeCycle();

	public Value<String> getName();

	public void setImplementation(JavaTypeName value);

	public void setImplementation(String value);

	public void setName(String value);

	@JavaTypeConstraint(
		behavior = JavaTypeConstraintBehavior.AT_LEAST_ONE, kind = JavaTypeKind.CLASS,
		type = {
			"javax.portlet.filter.ResourceFilter", "javax.portlet.filter.RenderFilter",
			"javax.portlet.filter.ActionFilter", "javax.portlet.filter.EventFilter"
		}
	)
	@Label(full = "Filter implementation class", standard = "implementation class")
	@MustExist
	@Reference(target = JavaType.class)
	@Required
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "filter-class")
	public ValueProperty PROP_IMPLEMENTATION = new ValueProperty(TYPE, "Implementation");

	@Label(standard = "initialization parameters")
	@Type(base = Param.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "init-param", type = Param.class))
	public ListProperty PROP_INIT_PARAMS = new ListProperty(TYPE, "InitParams");

	@Label(standard = "lifecycle")
	@Length(min = 1)
	@Type(base = LifeCycle.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "lifecycle", type = LifeCycle.class))
	public ListProperty PROP_LIFE_CYCLE = new ListProperty(TYPE, "LifeCycle");

	@Label(standard = "name")
	@Required
	@XmlBinding(path = "filter-name")
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

}