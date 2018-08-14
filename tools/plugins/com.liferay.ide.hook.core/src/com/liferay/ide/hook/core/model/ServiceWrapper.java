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

import com.liferay.ide.hook.core.model.internal.ServiceImplJavaTypeConstraintService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
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
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Kamesh Sampath
 */
@Image(path = "images/elcl16/service_16x16.gif")
@Label(standard = "Service Wrapper")
public interface ServiceWrapper extends Element {

	public ElementType TYPE = new ElementType(ServiceWrapper.class);

	public ReferenceValue<JavaTypeName, JavaType> getServiceImpl();

	public ReferenceValue<JavaTypeName, JavaType> getServiceType();

	public void setServiceImpl(JavaTypeName value);

	public void setServiceImpl(String value);

	public void setServiceType(JavaTypeName value);

	public void setServiceType(String value);

	@JavaTypeConstraint(kind = JavaTypeKind.CLASS)
	@Label(standard = "Service Impl")
	@MustExist
	@Reference(target = JavaType.class)
	@Required
	@Service(impl = ServiceImplJavaTypeConstraintService.class)
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "service-impl")
	public ValueProperty PROP_SERVICE_IMPL = new ValueProperty(TYPE, "ServiceImpl");

	@JavaTypeConstraint(kind = JavaTypeKind.INTERFACE)
	@Label(standard = "Service Type")
	@MustExist
	@Reference(target = JavaType.class)
	@Required
	@Type(base = JavaTypeName.class)
	@XmlBinding(path = "service-type")
	public ValueProperty PROP_SERVICE_TYPE = new ValueProperty(TYPE, "ServiceType");

}