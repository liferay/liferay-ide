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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.ProjectName;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
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
 * @author Terry Jia
 */
public interface BaseModuleOp extends ExecutableElement
{

    ElementType TYPE = new ElementType( BaseModuleOp.class );

    // *** ProjectName ***

    @Label( standard = "project name" )
    @Listeners( ModuleProjectNameListener.class )
    @Service( impl = ModuleProjectNameValidationService.class )
    @Required
    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, "ProjectName" );

    Value<String> getProjectName();

    void setProjectName( String value );

    // *** UseDefaultLocation ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "true" )
    @Label( standard = "use default location" )
    @Listeners( ModuleProjectUseDefaultLocationListener.class )
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
    @Service( impl = ModuleProjectLocationValidationService.class )
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" );

    Value<Path> getLocation();

    void setLocation( String value );

    void setLocation( Path value );

    // *** FinalProjectName ***

    @DefaultValue( text = "${ProjectName}" )
    ValueProperty PROP_FINAL_PROJECT_NAME = new ValueProperty( TYPE, "FinalProjectName" );

    Value<String> getFinalProjectName();

    void setFinalProjectName( String value );

    // *** ProjectNames ***

    @Type( base = ProjectName.class )
    ListProperty PROP_PROJECT_NAMES = new ListProperty( TYPE, "ProjectNames" );

    ElementList<ProjectName> getProjectNames();

    // *** ProjectProvider ***

    @Type( base = ILiferayProjectProvider.class )
    @Label( standard = "build type" )
    @Listeners( ModuleProjectNameListener.class )
    @Enablement( expr = "false" )
    @Services
    (
        value =
        {
            @Service( impl = ModuleProjectProviderPossibleValuesService.class ),
            @Service( impl = ModuleProjectProviderDefaultValueService.class )
        }
    )
    ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty( TYPE, "ProjectProvider" );

    Value<NewLiferayProjectProvider<NewLiferayModuleProjectOp>> getProjectProvider();

    void setProjectProvider( String value );

    void setProjectProvider( NewLiferayProjectProvider<NewLiferayModuleProjectOp> value );

}
