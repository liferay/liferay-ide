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

import com.liferay.ide.project.core.modules.BaseModuleOp;
import com.liferay.ide.project.core.modules.ModuleProjectNameValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Simon Jiang
 */
public interface NewLiferayJSFModuleProjectOp extends BaseModuleOp
{

    ElementType TYPE = new ElementType( NewLiferayJSFModuleProjectOp.class );

    // *** ProjectName ***

    @Listeners( JSFModuleProjectNameListener.class )
    @Service( impl = ModuleProjectNameValidationService.class )
    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, BaseModuleOp.PROP_PROJECT_NAME );

    // *** ProjectLocation ***

    @Service( impl = JSFModuleProjectLocationValidationService.class )
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, BaseModuleOp.PROP_LOCATION);

    // *** UseDefaultLocation ***

    @Listeners( JSFModuleProjectUseDefaultLocationListener.class )
    ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty( TYPE, BaseModuleOp.PROP_USE_DEFAULT_LOCATION );

    @Service( impl = JSFModuleProjectArchetypeDefaultValueService.class )
    ValueProperty PROP_ARCHETYPE = new ValueProperty( TYPE, "Archetype" );

    Value<String> getArchetype();

    void setArchetype( String value );

    // *** JSF Project Template ***

    @DefaultValue( text = "jsf" )
    @Label( standard = "Component Suite" )
    @PossibleValues( values= {"jsf", "alloy", "icefaces", "primefaces", "richfaces"})
    @Listeners( JSFModuleProjectNameListener.class )
    ValueProperty PROP_TEMPLATE_NAME = new ValueProperty( TYPE, "TemplateName" );

    Value<String> getTemplateName();

    void setTemplateName( String value );
    // *** ProjectProvider ***

    @Label( standard = "Build type:" )
    @Listeners( JSFModuleProjectNameListener.class )
    @Service( impl = JSFModuleProjectProviderPossibleValuesService.class )
    @Service( impl = JSFModuleProjectProviderDefaultValueService.class )
    ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty( TYPE, BaseModuleOp.PROP_PROJECT_PROVIDER );

    // *** Method: execute ***

    @Override
    @DelegateImplementation( NewLiferayJSFModuleProjectOpMethods.class )
    Status execute( ProgressMonitor monitor );
}
