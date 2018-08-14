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

package com.liferay.ide.portlet.core.lfportlet.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Simon Jiang
 */
@Image(path = "images/elcl16/custom.png")
public interface CustomUserAttribute extends Element {

	public ElementType TYPE = new ElementType(CustomUserAttribute.class);

	public ReferenceValue<JavaTypeName, JavaType> getCustomClass();

	public ElementList<CutomUserAttributeName> getCustomUserAttributeNames();

	public void setCustomClass(JavaTypeName value);

	public void setCustomClass(String value);

	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = "com.liferay.portlet.CustomUserAttributes")
	@Label(standard = "Custom Class")
	@MustExist
	@Reference(target = JavaType.class)
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "custom-class")
	public ValueProperty PROP_CUSTOM_CLASS = new ValueProperty(TYPE, "CustomClass");

	@Label(standard = "Custom Name")
	@Type(base = CutomUserAttributeName.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "name", type = CutomUserAttributeName.class))
	public ListProperty PROP_CUSTOM_USER_ATTRIBUTE_NAMES = new ListProperty(TYPE, "CustomUserAttributeNames");

}