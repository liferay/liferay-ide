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

package com.liferay.ide.portlet.core.model.lfrportlet.common;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author kamesh
 */
@GenerateImpl
public interface ICustomAttributeDisplay extends Element {

	public ElementType TYPE = new ElementType(ICustomAttributeDisplay.class);

	// *** CustomAttributeDisplay ***

	@Type(base = JavaTypeName.class)
	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = {"com.liferay.portlet.expando.model.CustomAttributesDisplay"})
	@Reference(target = JavaType.class)
	@Label(standard = "Custom Attribute Display")
	@MustExist
	@Required
	@NoDuplicates
	@XmlBinding(path = "custom-attribute-display")
	public ValueProperty PROP_CUSTOM_ATTRIBUTE_DISPLAY = new ValueProperty(TYPE, "CustomAttributeDisplay");

	public ReferenceValue<JavaTypeName, JavaType> getCustomAttributeDisplay();

	public void setCustomAttributeDisplay(String value);

	public void setCustomAttributeDisplay(JavaTypeName value);

}