/*******************************************************************************
 *  Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *  
 *   This library is free software; you can redistribute it and/or modify it under
 *   the terms of the GNU Lesser General Public License as published by the Free
 *   Software Foundation; either version 2.1 of the License, or (at your option)
 *   any later version.
 *  
 *   This library is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *   FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *   details.
 *  
 *   Contributors:
 *          Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.eclipse.hook.core.model;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@GenerateImpl
public interface IService extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IService.class );

	// *** ServiceType ***

	@Label( standard = "Service Type" )
	@XmlBinding( path = "service-type" )
	ValueProperty PROP_SERVICE_TYPE = new ValueProperty( TYPE, "ServiceType" );

	Value<String> getServiceType();

	void setServiceType( String value );

	// *** ServiceImpl ***
	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@Label( standard = "Service Implementation" )
	@JavaTypeConstraint( kind = { JavaTypeKind.CLASS, JavaTypeKind.ABSTRACT_CLASS }, type = { "java.lang.Object" } )
	@MustExist
	@Required
	@XmlBinding( path = "service-impl" )
	ValueProperty PROP_SERVICE_IMPL = new ValueProperty( TYPE, "ServiceImpl" );

	ReferenceValue<JavaTypeName, JavaType> getServiceImpl();

	void setServiceImpl( String value );

	void setServiceImpl( JavaTypeName value );

}
