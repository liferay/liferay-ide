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
package com.liferay.ide.gradle.core.modules;

import com.liferay.ide.project.core.modules.BaseModuleOp;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;

/**
 * @author Terry Jia
 */
public interface NewModuleFragmentOp extends BaseModuleOp
{
    ElementType TYPE = new ElementType( NewModuleFragmentOp.class );

    // *** Liferay Runtime ***

    @Services
    (
        value =
        {
            @Service( impl = LiferayRuntimeNamePossibleValuesService.class ),
            @Service( impl = LiferayRuntimeNameDefaultValueService.class ),
            @Service( impl = LiferayRuntimeNameValidationService.class )
        }
    )
    @Required
    ValueProperty PROP_LIFERAY_RUNTIME_NAME = new ValueProperty( TYPE, "LiferayRuntimeName" );

    Value<String> getLiferayRuntimeName();
    void setLiferayRuntimeName( String value );

    // *** HostOSGiBundle ***

    @Label( standard = "Host OSGi Bundle" )
    @Service( impl = HostOSGiBundlePossibleValuesService.class )
    @Listeners( OSGiBundleListener.class )
    @Required
    ValueProperty PROP_HOST_OSGI_BUNDLE = new ValueProperty( TYPE, "HostOsgiBundle" );

    Value<String> getHostOsgiBundle();
    void setHostOsgiBundle(String value);

    // *** OverrideFiles ***

    @Type( base = OverrideFilePath.class )
    @Label( standard = "Overridden files" )
    ListProperty PROP_OVERRIDE_FILES = new ListProperty( TYPE, "OverrideFiles" );

    ElementList<OverrideFilePath> getOverrideFiles();

    // *** Method: execute ***

    @Override
    @DelegateImplementation( NewModuleFragmentOpMethods.class )
    Status execute( ProgressMonitor monitor );
}