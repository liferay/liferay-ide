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

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.modules.BaseModuleOp;
import com.liferay.ide.project.core.service.CommonProjectLocationInitialValueService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
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
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Whitespace;

/**
 * @author Terry Jia
 */
public interface NewModuleFragmentOp extends BaseModuleOp
{

    ElementType TYPE = new ElementType( NewModuleFragmentOp.class );

    // *** ProjectName ***

    @Label( standard = "project name" )
    @Listeners( FragmentProjectNameListener.class )
    @Service( impl = FragmentProjectNameValidationService.class )
    @Required
    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, "ProjectName" );

    Value<String> getProjectName();
    void setProjectName( String value );

    // *** ProjectLocation ***

    @Type( base = Path.class )
    @AbsolutePath
    @Enablement( expr = "${ UseDefaultLocation == 'false' }" )
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Label( standard = "location" )
    @Service( impl = FragmentProjectLocationValidationService.class )
    @Service( impl = CommonProjectLocationInitialValueService.class )
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" );

    Value<Path> getLocation();
    void setLocation( String value );
    void setLocation( Path value );

    // *** UseDefaultLocation ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "true" )
    @Label( standard = "use default location" )
    @Listeners( ModuleFragmentProjectUseDefaultLocationListener.class )
    ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty( TYPE, "UseDefaultLocation" );

    Value<Boolean> getUseDefaultLocation();
    void setUseDefaultLocation( String value );
    void setUseDefaultLocation( Boolean value );

    // *** Liferay Runtime ***

    @Services(
                    value = { @Service( impl = LiferayRuntimeNamePossibleValuesService.class ),
                        @Service( impl = LiferayRuntimeNameDefaultValueService.class ),
                        @Service( impl = LiferayRuntimeNameValidationService.class ) } )
    @Required
    ValueProperty PROP_LIFERAY_RUNTIME_NAME = new ValueProperty( TYPE, "LiferayRuntimeName" );

    Value<String> getLiferayRuntimeName();

    void setLiferayRuntimeName( String value );

    // *** HostOSGiBundle ***

    @DefaultValue( text = "" )
    @Label( standard = "Host OSGi Bundle" )
    @Service( impl = HostOSGiBundlePossibleValuesService.class )
    @Listeners( OSGiBundleListener.class )
    @Required
    ValueProperty PROP_HOST_OSGI_BUNDLE = new ValueProperty( TYPE, "HostOsgiBundle" );

    Value<String> getHostOsgiBundle();
    void setHostOsgiBundle( String value );

    // *** HostOSGiBundle ***

    ValueProperty PROP_LPKG_NAME = new ValueProperty( TYPE, "LpkgName" );

    Value<String> getLpkgName();
    void setLpkgName( String value );

    // *** OverrideFiles ***

    @Type( base = OverrideFilePath.class )
    @Label( standard = "Overridden files" )
    ListProperty PROP_OVERRIDE_FILES = new ListProperty( TYPE, "OverrideFiles" );

    ElementList<OverrideFilePath> getOverrideFiles();

    // *** ProjectProvider ***

    @Type( base = ILiferayProjectProvider.class )
    @Label( standard = "build type" )
    @Listeners( FragmentProjectNameListener.class )
    @Service( impl = FragmentProjectProviderPossibleValuesService.class )
    @Service( impl = FragmentProjectProviderDefaultValueService.class )
    ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty( TYPE, "ProjectProvider" );

    Value<NewLiferayProjectProvider<NewModuleFragmentOp>> getProjectProvider();
    void setProjectProvider( String value );
    void setProjectProvider( NewLiferayProjectProvider<NewModuleFragmentOp> value );

    // *** Maven settings ***
    // *** ArtifactVersion ***

    @Label( standard = "artifact version" )
    @Service( impl = ModuleFragmentProjectArtifactVersionDefaultValueService.class )
    ValueProperty PROP_ARTIFACT_VERSION = new ValueProperty( TYPE, "ArtifactVersion" );

    Value<String> getArtifactVersion();
    void setArtifactVersion( String value );


    // *** GroupId ***

    @Label( standard = "group id" )
    @Service( impl = ModuleFragmentProjectGroupIdValidationService.class )
    @Service( impl = ModuleFragmentProjectGroupIdDefaultValueService.class )
    @Whitespace( trim = false )
    ValueProperty PROP_GROUP_ID = new ValueProperty( TYPE, "GroupId" );

    Value<String> getGroupId();
    void setGroupId( String value );
    
    // *** Method: execute ***

    @Override
    @DelegateImplementation( NewModuleFragmentOpMethods.class )
    Status execute( ProgressMonitor monitor );
}
