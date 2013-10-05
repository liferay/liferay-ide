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
package com.liferay.ide.adt.core.model.internal;

import com.liferay.ide.adt.core.ADTCore;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.services.DerivedValueService;
import org.eclipse.sapphire.services.DerivedValueServiceData;


/**
 * @author Gregory Amerson
 */
public class TemplateOptionDerivedValueService extends DerivedValueService
{

    @Override
    protected DerivedValueServiceData compute()
    {
        String retval = null;
        final IScopeContext[] prefContexts = { DefaultScope.INSTANCE, InstanceScope.INSTANCE };
        final String templateOption = Platform.getPreferencesService().getString( ADTCore.PLUGIN_ID, ADTCore.PREF_PROJECT_TEMPLATE_LOCATION_OPTION, null, prefContexts );

        if( ADTCore.VALUE_USE_CUSTOM_TEMPLATE.equals( templateOption ) )
        {
            final String customLocation = Platform.getPreferencesService().getString( ADTCore.PLUGIN_ID, ADTCore.PREF_PROJECT_TEMPLATE_LOCATION, "", prefContexts );

            final File templateFile = new File( customLocation );

            retval = "custom template file " + ( templateFile.exists() ? templateFile.getName() : "" );
        }
        else
        {
            retval = "default embedded template";
        }

        return new DerivedValueServiceData( retval );
    }

    @Override
    protected void initDerivedValueService()
    {
        super.initDerivedValueService();

        final IPreferenceChangeListener prefListener = new IPreferenceChangeListener()
        {
            public void preferenceChange( PreferenceChangeEvent event )
            {
                refresh();
            }
        };

        final IEclipsePreferences node = InstanceScope.INSTANCE.getNode( ADTCore.PLUGIN_ID );

        node.addPreferenceChangeListener( prefListener );

        FilteredListener<DisposeEvent> disposeListener = new FilteredListener<DisposeEvent>()
        {
            protected void handleTypedEvent( DisposeEvent event )
            {
                node.removePreferenceChangeListener( prefListener );
            }
        };

        attach( disposeListener );
    }

}
