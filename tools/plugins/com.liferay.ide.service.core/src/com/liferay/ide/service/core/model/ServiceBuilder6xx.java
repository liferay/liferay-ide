/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.service.core.model.internal.ServiceBuilderDefaultValueService;
import com.liferay.ide.service.core.model.internal.ServiceBuilderRootElementController;

import org.eclipse.sapphire.Version;
import org.eclipse.sapphire.VersionCompatibilityTarget;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
@GenerateImpl
@XmlBinding( path = "service-builder" )
@VersionCompatibilityTarget( version = "${ Version }", versioned = "Service Builder" )
@CustomXmlRootBinding( value =  ServiceBuilderRootElementController.class )
public interface ServiceBuilder6xx extends ServiceBuilder
{
	ModelElementType TYPE = new ModelElementType( ServiceBuilder6xx.class );

	// *** Version ***

    @Type( base = Version.class )
    @Service( impl = ServiceBuilderDefaultValueService.class )
    ValueProperty PROP_VERSION = new ValueProperty( TYPE, "Version" ); //$NON-NLS-1$

    Value<Version> getVersion();
    void setVersion( String value );
    void setVersion( Version value );
}
