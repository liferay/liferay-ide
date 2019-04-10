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
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Kamesh Sampath
 */
@Image(path = "images/elcl16/event_16x16.gif")
@Label(full = "Event Definition", standard = "Event Definition")
public interface EventDefinition extends Describeable, Identifiable, QName {

	public ElementType TYPE = new ElementType(EventDefinition.class);

	public ElementList<AliasQName> getAliases();

	public ReferenceValue<JavaTypeName, JavaType> getEventValueType();

	public void setEventValueType(JavaTypeName eventValueType);

	public void setEventValueType(String eventValueType);

	@Label(standard = "Aliases")
	@Type(base = AliasQName.class)
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "alias", type = AliasQName.class)})
	public ListProperty PROP_ALIASES = new ListProperty(TYPE, "Aliases");

	@JavaTypeConstraint(kind = {JavaTypeKind.CLASS, JavaTypeKind.INTERFACE}, type = "java.io.Serializable")
	@Label(standard = "Value Type")
	@Reference(target = JavaType.class)
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "value-type")
	public ValueProperty PROP_EVENT_VALUE_TYPE = new ValueProperty(TYPE, "EventValueType");

}