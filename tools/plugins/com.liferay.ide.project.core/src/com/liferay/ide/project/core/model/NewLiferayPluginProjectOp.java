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

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.internal.ActiveProfilesValidationService;
import com.liferay.ide.project.core.model.internal.ArchetypeDefaultValueService;
import com.liferay.ide.project.core.model.internal.ArtifactVersionDefaultValueService;
import com.liferay.ide.project.core.model.internal.CreateNewPortletDefaultValueService;
import com.liferay.ide.project.core.model.internal.DisplayNameDefaultValueService;
import com.liferay.ide.project.core.model.internal.GroupIdDefaultValueService;
import com.liferay.ide.project.core.model.internal.GroupIdValidationService;
import com.liferay.ide.project.core.model.internal.HasWorkspaceSdkDefaultValueService;
import com.liferay.ide.project.core.model.internal.IncludeSampleCodeDefaultValueService;
import com.liferay.ide.project.core.model.internal.LocationValidationService;
import com.liferay.ide.project.core.model.internal.PluginTypeListener;
import com.liferay.ide.project.core.model.internal.PluginTypePossibleValuesService;
import com.liferay.ide.project.core.model.internal.PluginTypeValidationService;
import com.liferay.ide.project.core.model.internal.PortletFrameworkAdvancedPossibleValuesService;
import com.liferay.ide.project.core.model.internal.PortletFrameworkPossibleValuesService;
import com.liferay.ide.project.core.model.internal.PortletFrameworkValidationService;
import com.liferay.ide.project.core.model.internal.ProfileIdPossibleValuesService;
import com.liferay.ide.project.core.model.internal.ProjectNameListener;
import com.liferay.ide.project.core.model.internal.ProjectNameValidationService;
import com.liferay.ide.project.core.model.internal.ProjectProviderDefaultValueService;
import com.liferay.ide.project.core.model.internal.ProjectProviderListener;
import com.liferay.ide.project.core.model.internal.ProjectProviderPossibleValuesService;
import com.liferay.ide.project.core.model.internal.SDKLocationListener;
import com.liferay.ide.project.core.model.internal.SDKLocationValidationService;
import com.liferay.ide.project.core.model.internal.ThemeFrameworkValidationService;
import com.liferay.ide.project.core.model.internal.UseDefaultLocationListener;
import com.liferay.ide.project.core.model.internal.UseDefaultLocationValidationService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Fact;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Whitespace;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 * @author Tao Tao
 */
public interface NewLiferayPluginProjectOp extends ExecutableElement
{
    ElementType TYPE = new ElementType( NewLiferayPluginProjectOp.class );

    // *** Archetype ***

    @Service( impl = ArchetypeDefaultValueService.class )
    ValueProperty PROP_ARCHETYPE = new ValueProperty( TYPE, "Archetype" ); //$NON-NLS-1$

    Value<String> getArchetype();
    void setArchetype( String value );

    // *** ProjectName ***

    @Label( standard = "project name" )
    @Listeners( ProjectNameListener.class )
    @Service( impl = ProjectNameValidationService.class )
    @Required
    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, "ProjectName" ); //$NON-NLS-1$

    Value<String> getProjectName();
    void setProjectName( String value );


    // *** DisplayName ***

    @Label( standard = "display name" )
    @Service( impl = DisplayNameDefaultValueService.class )
    ValueProperty PROP_DISPLAY_NAME = new ValueProperty( TYPE, "DisplayName" ); //$NON-NLS-1$

    Value<String> getDisplayName();
    void setDisplayName( String value );


    @Type( base = Boolean.class )
    @Service( impl = HasWorkspaceSdkDefaultValueService.class )
    ValueProperty PROP_HAS_WORKSPACE_SDK = new ValueProperty( TYPE, "HasWorkspaceSDK" ); //$NON-NLS-1$


    // *** UseDefaultLocation ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "true" )
    @Enablement( expr = "${ ProjectProvider != 'ant' }" )
    @Label( standard = "use default location" )
    @Listeners( UseDefaultLocationListener.class )
    @Service( impl = UseDefaultLocationValidationService.class )
    ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty( TYPE, "UseDefaultLocation" ); //$NON-NLS-1$

    Value<Boolean> getUseDefaultLocation();
    void setUseDefaultLocation( String value );
    void setUseDefaultLocation( Boolean value );


    // *** ProjectLocation ***

    @Type( base = Path.class )
    @AbsolutePath
    @Enablement( expr = "${ UseDefaultLocation == 'false' && ProjectProvider != 'ant' }" )
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Label( standard = "location" )
    @Service( impl = LocationValidationService.class )
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" ); //$NON-NLS-1$

    Value<Path> getLocation();
    void setLocation( String value );
    void setLocation( Path value );


    // *** ProjectProvider ***

    @Type( base = ILiferayProjectProvider.class )
    @Label( standard = "build type" )
    @Listeners( ProjectProviderListener.class )
    @Services
    (
        value=
        {
            @Service( impl = ProjectProviderPossibleValuesService.class ),
            @Service( impl = ProjectProviderDefaultValueService.class )
        }
    )
    ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty( TYPE, "ProjectProvider" ); //$NON-NLS-1$

    Value<NewLiferayProjectProvider<NewLiferayPluginProjectOp>> getProjectProvider();
    void setProjectProvider( String value );
    void setProjectProvider( NewLiferayProjectProvider<NewLiferayPluginProjectOp> value );

    // *** SDK Location ***
    @Type( base = Path.class )
    @AbsolutePath
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Label( standard = "SDK Location" )
    @Listeners( SDKLocationListener.class )
    @Service( impl = SDKLocationValidationService.class )
    ValueProperty PROP_SDK_LOCATION = new ValueProperty( TYPE, "SdkLocation" ); //$NON-NLS-1$

    Value<Path> getSdkLocation();
    void setSdkLocation( String value );
    void setSdkLocation( Path value );

    // *** PluginType ***

    @Type( base = PluginType.class )
    @Label( standard = "plugin type" )
    @Listeners( PluginTypeListener.class )
    @DefaultValue( text = "portlet" )
    @Services
    (
        value=
        {
            @Service( impl = PluginTypePossibleValuesService.class ),
            @Service( impl = PluginTypeValidationService.class )
        }
    )
    ValueProperty PROP_PLUGIN_TYPE = new ValueProperty( TYPE, "PluginType" ); //$NON-NLS-1$

    Value<PluginType> getPluginType();
    void setPluginType( String value );
    void setPluginType( PluginType value );


    // *** IncludeSampleCode ***

    @Type( base = Boolean.class )
    @Service( impl = IncludeSampleCodeDefaultValueService.class )
    ValueProperty PROP_INCLUDE_SAMPLE_CODE = new ValueProperty( TYPE, "IncludeSampleCode" );

    Value<Boolean> getIncludeSampleCode();
    void setIncludeSampleCode( String value );
    void setIncludeSampleCode( Boolean value );


    // *** CreateNewPortlet ***

    @Type( base = Boolean.class )
    @Service( impl = CreateNewPortletDefaultValueService.class )
    ValueProperty PROP_CREATE_NEW_PORTLET = new ValueProperty( TYPE, "CreateNewPortlet" );

    Value<Boolean> getCreateNewPortlet();
    void setCreateNewPortlet( String value );
    void setCreateNewPortlet( Boolean value );


    // *** PortletFramework ***

    @Type( base = IPortletFramework.class )
    @Label( standard = "portlet framework" )
    @DefaultValue( text = "mvc" )
    @Services
    (
        value=
        {
            @Service( impl = PortletFrameworkValidationService.class ),
            @Service( impl = PortletFrameworkPossibleValuesService.class )
        }
    )
    ValueProperty PROP_PORTLET_FRAMEWORK = new ValueProperty( TYPE, "PortletFramework" ); //$NON-NLS-1$

    Value<IPortletFramework> getPortletFramework();
    void setPortletFramework( String value );
    void setPortletFramework( IPortletFramework value );


    // *** PortletFrameworkAdvanced ***

    @Type( base = IPortletFramework.class )
    @DefaultValue( text = "jsf" )
    @Service( impl = PortletFrameworkAdvancedPossibleValuesService.class )
    ValueProperty PROP_PORTLET_FRAMEWORK_ADVANCED = new ValueProperty( TYPE, "PortletFrameworkAdvanced" ); //$NON-NLS-1$

    Value<IPortletFramework> getPortletFrameworkAdvanced();
    void setPortletFrameworkAdvanced( String value );
    void setPortletFrameworkAdvanced( IPortletFramework value );


    // *** ThemeParent ***

    @Label( standard = "theme parent" )
    @PossibleValues( ordered = true, values = { "_unstyled", "_styled", "classic" } )
    @DefaultValue( text = "_styled" )
    ValueProperty PROP_THEME_PARENT = new ValueProperty( TYPE, "ThemeParent" ); //$NON-NLS-1$

    Value<String> getThemeParent();
    void setThemeParent( String value );


    // *** ThemeFramework ***

    @Label( standard = "theme framework" )
    @DefaultValue( text = "Freemarker" )
    @PossibleValues( ordered = true, values = { "Velocity", "Freemarker", "JSP" } )
    @Service( impl = ThemeFrameworkValidationService.class )
    ValueProperty PROP_THEME_FRAMEWORK = new ValueProperty( TYPE, "ThemeFramework" ); //$NON-NLS-1$

    Value<String> getThemeFramework();
    void setThemeFramework( String value );


    // *** Maven settings ***
    // TODO move this to maven.core plugin

    // *** ArtifactVersion ***

    @Label( standard = "artifact version" )
    @Service( impl = ArtifactVersionDefaultValueService.class )
    ValueProperty PROP_ARTIFACT_VERSION = new ValueProperty( TYPE, "ArtifactVersion" ); //$NON-NLS-1$

    Value<String> getArtifactVersion();
    void setArtifactVersion( String value );


    // *** GroupId ***

    @Label( standard = "group id" )
    @Services
    (
        value =
        {
            @Service( impl = GroupIdValidationService.class ),
            @Service( impl = GroupIdDefaultValueService.class )
        }
    )
    @Whitespace( trim = false )
    ValueProperty PROP_GROUP_ID = new ValueProperty( TYPE, "GroupId" ); //$NON-NLS-1$

    Value<String> getGroupId();
    void setGroupId( String value );


    // *** ActiveProfiles ***

    @Label( standard = "active profiles" )
    @Fact( statement = "Supports comma separated list of active profiles" )
    @Service( impl = ActiveProfilesValidationService.class )
    @Whitespace( trim = false )
    ValueProperty PROP_ACTIVE_PROFILES_VALUE = new ValueProperty( TYPE, "ActiveProfilesValue" );

    Value<String> getActiveProfilesValue();
    void setActiveProfilesValue( String value );


    // *** SelectedProfiles ***

    @Type( base = Profile.class )
    @Label( standard = "selected profiles" )
    @Service( impl = ProfileIdPossibleValuesService.class )
    ListProperty PROP_SELECTED_PROFILES = new ListProperty( TYPE, "SelectedProfiles" );

    ElementList<Profile> getSelectedProfiles();


    // *** NewLiferayProfiles ***

    @Type( base = NewLiferayProfile.class )
    ListProperty PROP_NEW_LIFERAY_PROFILES = new ListProperty( TYPE, "NewLiferayProfiles" );

    ElementList<NewLiferayProfile> getNewLiferayProfiles();


    // *** FinalProjectName ***

    @DefaultValue( text = "${ProjectName}" )
    ValueProperty PROP_FINAL_PROJECT_NAME = new ValueProperty( TYPE, "FinalProjectName" );

    Value<String> getFinalProjectName();
    void setFinalProjectName( String value );


    // *** PortletName ***

    @Label( standard = "portlet name" )
    @DefaultValue( text = "${ProjectName}" )
    ValueProperty PROP_PORTLET_NAME = new ValueProperty( TYPE, "PortletName" );

    Value<String> getPortletName();
    void setPortletName( String value );

    // *** ProjectNames ***

    @Type( base = ProjectName.class )
    ListProperty PROP_PROJECT_NAMES = new ListProperty( TYPE, "ProjectNames" );

    ElementList<ProjectName> getProjectNames();

    // *** Method: execute ***

    @Override
    @DelegateImplementation( NewLiferayPluginProjectOpMethods.class )
    Status execute( ProgressMonitor monitor );
}
