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
public interface NewJSPHookModuleOp extends BaseModuleOp
{
    ElementType TYPE = new ElementType( NewJSPHookModuleOp.class );

    // *** Liferay Bundle ***

    @Label( standard = "Portal Bundle" )
    @Services
    (
        value =
        {
            @Service( impl = PortalBundleNamePossibleValuesService.class ),
            @Service( impl = PortalBundleNameDefaultValueService.class ),
            @Service( impl = NewLiferayBundleValidationService.class )
        }
    )
    ValueProperty PROP_BUNDLE_NAME = new ValueProperty( TYPE, "BundleName" );

    Value<String> getBundleName();
    void setBundleName( String value );

    // *** CustomOSGiBundle ***

    @Label( standard = "Host Bundle" )
    @Service( impl = OSGiBundlePossibleValuesService.class )
    @Listeners( OSGiBundleListener.class )
    @Required
    ValueProperty PROP_CUSTOM_OSGI_BUNDLE = new ValueProperty( TYPE, "CustomOSGiBundle" );

    Value<String> getCustomOSGiBundle();
    void setCustomOSGiBundle(String value);

    // *** CustomJSPs ***

    @Type( base = OSGiCustomJSP.class )
    @Label( standard = "custom jsps" )
    ListProperty PROP_CUSTOM_JSPS = new ListProperty( TYPE, "CustomJSPs" );

    ElementList<OSGiCustomJSP> getCustomJSPs();

    // *** RealOSGiBUndleFile ***

    ValueProperty PROP_REAL_OSGI_BUNDLE_FILE = new ValueProperty( TYPE, "RealOSGiBundleFile" );

    Value<String> getRealOSGiBundleFile();
    void setRealOSGiBundleFile( String value );

    // *** Method: execute ***

    @Override
    @DelegateImplementation( NewJSPHookModuleOpMethods.class )
    Status execute( ProgressMonitor monitor );
}