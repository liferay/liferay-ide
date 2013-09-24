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
package com.liferay.ide.adt.core;

import com.liferay.ide.core.util.CoreUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;


/**
 * @author Gregory Amerson
 */
public class DefaultPreferenceInitializer extends AbstractPreferenceInitializer
{

    public DefaultPreferenceInitializer()
    {
        super();
    }

    private String getDefaultProjectTemplateLocation()
    {
        // https://github.com/brunofarache/liferay-mobile-sdk-sample-android
        try
        {
            final URL templateFile = ADTCore.getDefault().getBundle().getEntry( "templates/version.txt" );
            final String latestFile = CoreUtil.readStreamToString( templateFile.openStream() );
            final URL versionUrl = ADTCore.getDefault().getBundle().getEntry( "templates/" + latestFile );

            return new File( FileLocator.toFileURL( versionUrl ).getFile() ).getCanonicalPath();
        }
        catch( IOException e )
        {
            ADTCore.logError( "Could not get built-in project template.", e );
        }

        return null;
    }

    @Override
    public void initializeDefaultPreferences()
    {
        final String defaultProjectTemplateLocation = getDefaultProjectTemplateLocation();

        if( defaultProjectTemplateLocation != null )
        {
            DefaultScope.INSTANCE.getNode( ADTCore.PLUGIN_ID ).put(
                ADTCore.PREF_PROJECT_TEMPLATE_LOCATION, defaultProjectTemplateLocation );
        }
    }

}
