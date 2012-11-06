/*******************************************************************************
 * Copyright (c) 2010-2012 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.service.core.model;

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlDocumentType;

/**
 * @author Gregory Amerson
 */
@GenerateImpl
@XmlDocumentType
( 
    publicId = "-//Liferay//DTD Service Builder 6.0.0//EN", 
    systemId = "http://www.liferay.com/dtd/liferay-service-builder_6_0_0.dtd" 
)
@XmlBinding( path = "service-builder" )
public interface ServiceBuilder600 extends ServiceBuilder
{
	ModelElementType TYPE = new ModelElementType( ServiceBuilder600.class );
}
