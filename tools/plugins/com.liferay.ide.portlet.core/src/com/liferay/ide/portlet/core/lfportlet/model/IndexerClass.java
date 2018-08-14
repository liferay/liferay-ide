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
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Simon Jiang
 */
public interface IndexerClass extends Element {

	public ElementType TYPE = new ElementType(IndexerClass.class);

	public ReferenceValue<JavaTypeName, JavaType> getValue();

	public void setValue(JavaTypeName value);

	public void setValue(String value);

	@JavaTypeConstraint(kind = JavaTypeKind.CLASS, type = "com.liferay.portal.kernel.search.Indexer")
	@Label(standard = "Indexer Class")
	@MustExist
	@Reference(target = JavaType.class)
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "")
	public ValueProperty PROP_VALUE = new ValueProperty(TYPE, "Value");

}