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
package com.liferay.ide.adt.core.model;

import com.liferay.ide.adt.core.model.internal.LiferayApisPossibleValueService;
import com.liferay.ide.adt.core.model.internal.LocationValidationService;
import com.liferay.ide.adt.core.model.internal.NewLiferayAndroidProjectOpMethods;
import com.liferay.ide.adt.core.model.internal.ProjectNameListener;
import com.liferay.ide.adt.core.model.internal.ProjectNameValidationService;
import com.liferay.ide.adt.core.model.internal.TargetSDKDefaultValueService;
import com.liferay.ide.adt.core.model.internal.TargetSDKPossibleValuesService;
import com.liferay.ide.adt.core.model.internal.UseDefaultLocationListener;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.IExecutableModelElement;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public interface NewLiferayAndroidProjectOp extends IExecutableModelElement
{

    ElementType TYPE = new ElementType( NewLiferayAndroidProjectOp.class );

    // *** ProjectName ***

    @Label( standard = "project name" )
    @Listeners( ProjectNameListener.class )
    @Service( impl = ProjectNameValidationService.class )
    @Required
    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, "ProjectName" );

    Value<String> getProjectName();

    void setProjectName( String value );

    // *** UseDefaultLocation ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "true" )
    @Label( standard = "use default location" )
    @Listeners( UseDefaultLocationListener.class )
    ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty( TYPE, "UseDefaultLocation" );

    Value<Boolean> getUseDefaultLocation();

    void setUseDefaultLocation( String value );

    void setUseDefaultLocation( Boolean value );

    // *** ProjectLocation ***

    @Type( base = Path.class )
    @AbsolutePath
    @Enablement( expr = "${ UseDefaultLocation == 'false' }" )
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Label( standard = "location" )
    @Service( impl = LocationValidationService.class )
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" );

    Value<Path> getLocation();

    void setLocation( String value );

    void setLocation( Path value );

    // *** SDKLevel ***

    @Label( standard = "Target SDK" )
    @Services
    ( 
        value = 
        {
            @Service( impl = TargetSDKPossibleValuesService.class ),
            @Service( impl = TargetSDKDefaultValueService.class ),
        }
    )
    ValueProperty PROP_TARGET_SDK = new ValueProperty( TYPE, "TargetSDK" );

    Value<String> getTargetSDK();

    void setTargetSDK( String value );

    // *** LiferayApis ***

    @Type( base = LiferayApi.class )
    @Label( standard = "Liferay APIs" )
    @Service( impl = LiferayApisPossibleValueService.class )
    ListProperty PROP_LIFERAY_APIS = new ListProperty( TYPE, "LiferayApis" );

    ElementList<LiferayApi> getLiferayApis();

    // *** IncludeExample ***

//    @Type( base = Boolean.class )
//    @Label( standard = "include example activity" )
//    @DefaultValue( text = "true" )
//    ValueProperty PROP_INCLUDE_EXAMPLE = new ValueProperty( TYPE, "IncludeExample" );
//
//    Value<Boolean> getIncludeExample();
//
//    void setIncludeExample( String value );
//
//    void setIncludeExample( Boolean value );


    // *** Method: execute ***

    @DelegateImplementation( NewLiferayAndroidProjectOpMethods.class )
    Status execute( ProgressMonitor monitor );


}
