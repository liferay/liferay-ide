/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.ide.project.core.model;

import com.liferay.ide.project.core.model.internal.SDKProjectImportLocationValidationService;
import com.liferay.ide.project.core.model.internal.SDKProjectImportPluginTypeDerivedValueService;
import com.liferay.ide.project.core.model.internal.SDKProjectImportVersionDerivedValueService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Derived;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Simon Jiang
 */
public interface SDKProjectImportOp extends ExecutableElement
{
    ElementType TYPE = new ElementType( SDKProjectImportOp.class );

    // *** Location ***
    @Type( base = Path.class )
    @AbsolutePath
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Label( standard = "Project Directory" )
    @Service( impl = SDKProjectImportLocationValidationService.class )
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" ); //$NON-NLS-1$

    Value<Path> getLocation();
    void setLocation( String value );
    void setLocation( Path value );


    // *** SDK Version ***
    @Label( standard = "SDK Version" )
    @Derived
    @Service( impl = SDKProjectImportVersionDerivedValueService.class )
    ValueProperty PROP_SDK_VERSION = new ValueProperty( TYPE, "SdkVersion" ); //$NON-NLS-1$

    Value<String> getSdkVersion();
    void setSdkVersion( String value );


    // *** Plugin Type ***
    @Label( standard = "Plugin Type" )
    @Derived
    @Service( impl = SDKProjectImportPluginTypeDerivedValueService.class )
    ValueProperty PROP_PLUGIN_TYPE = new ValueProperty( TYPE, "PluginType" ); //$NON-NLS-1$

    Value<String> getPluginType();
    void setPluginType( String value );

    // *** Final Project Name
    ValueProperty PROP_FINAL_PROJECT_NAME = new ValueProperty( TYPE, "FinalProjectName" );

    Value<String> getFinalProjectName();
    void setFinalProjectName( String value );

    @Override
    @DelegateImplementation( SDKImportProjectOpMethods.class )
    Status execute( ProgressMonitor monitor );
}
