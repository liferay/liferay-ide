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

package com.liferay.ide.project.core.jsf;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.modules.ModuleProjectNameListener;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.PossibleValues;
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
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Simon Jiang
 */
public interface NewLiferayJSFModuleProjectOp extends NewLiferayModuleProjectOp
{

    ElementType TYPE = new ElementType( NewLiferayJSFModuleProjectOp.class );

    // *** ProjectName ***

    @Label( standard = "project name" )
    @Listeners( JSFModuleProjectNameListener.class )
    @Service( impl = JSFModuleProjectNameValidationService.class )
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
    @Service( impl = JSFModuleProjectLocationValidationService.class )
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" );

    Value<Path> getLocation();

    void setLocation( String value );

    void setLocation( Path value );

    // *** UseDefaultLocation ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "true" )
    @Label( standard = "use default location" )
    @Listeners( JSFModuleProjectUseDefaultLocationListener.class )
    ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty( TYPE, "UseDefaultLocation" );

    Value<Boolean> getUseDefaultLocation();

    void setUseDefaultLocation( String value );

    void setUseDefaultLocation( Boolean value );

    @Service( impl = JSFModuleProjectArchetypeDefaultValueService.class )
    ValueProperty PROP_ARCHETYPE = new ValueProperty( TYPE, "Archetype" );

    Value<String> getArchetype();

    void setArchetype( String value );

    // *** JSF Project Template ***

    @DefaultValue( text = "JSF Standard" )
    @Label( standard = "Component Suite" )
    @PossibleValues( values = { "JSF Standard", "Liferay Faces Alloy", "ICEFaces", "PrimeFaces", "RichFaces" } )
    @Listeners( ModuleProjectNameListener.class )
    ValueProperty PROP_JSF_TEMPLATE_NAME = new ValueProperty( TYPE, "JsfTemplateName" );

    Value<String> getJsfTemplateName();

    void setJsfTemplateName( String value );
    // *** ProjectProvider ***

    @Type( base = ILiferayProjectProvider.class )
    @Label( standard = "Build Framework" )
    @Listeners( JSFModuleProjectNameListener.class )
    @Service( impl = JSFModuleProjectProviderPossibleValuesService.class )
    @Service( impl = JSFModuleProjectProviderDefaultValueService.class )
    ValueProperty PROP_JSF_PROJECT_PROVIDER = new ValueProperty( TYPE, "JsfProjectProvider" );

    Value<NewLiferayProjectProvider<NewLiferayJSFModuleProjectOp>> getJsfProjectProvider();

    void setJsfProjectProvider( String value );
    void setJsfProjectProvider( NewLiferayProjectProvider<NewLiferayJSFModuleProjectOp> value );

    // *** Method: execute ***

    @Override
    @DelegateImplementation( NewLiferayJSFModuleProjectOpMethods.class )
    Status execute( ProgressMonitor monitor );
}
